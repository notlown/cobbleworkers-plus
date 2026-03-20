/*
 * Copyright (C) 2025 Accieo
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.jobs

import accieo.cobbleworkers.cache.CobbleworkersCacheManager
import accieo.cobbleworkers.config.CobbleworkersConfigHolder
import accieo.cobbleworkers.enums.JobType
import accieo.cobbleworkers.interfaces.Worker
import accieo.cobbleworkers.utilities.CobbleworkersInventoryUtils
import accieo.cobbleworkers.utilities.CobbleworkersNavigationUtils
import accieo.cobbleworkers.utilities.CobbleworkersTypeUtils
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.LootTables
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.UUID
import kotlin.collections.set
import kotlin.text.lowercase

object FishingLootGenerator : Worker {
    private val config = CobbleworkersConfigHolder.config.fishing
    private val cooldownTicks get() = config.fishingLootGenerationCooldownSeconds * 20L
    private val treasureChance get() = config.fishingLootTreasureChance
    private val lastGenerationTime = mutableMapOf<UUID, Long>()
    private val heldItemsByPokemon = mutableMapOf<UUID, List<ItemStack>>()
    private val failedDepositLocations = mutableMapOf<UUID, MutableSet<BlockPos>>()

    override val jobType: JobType = JobType.FishingLootGenerator

    /**
     * Block validator: finds water blocks so the scanner caches their positions.
     * Pokemon will navigate to these cached water blocks to fish.
     */
    override val blockValidator: ((World, BlockPos) -> Boolean) = { world, pos ->
        world.getBlockState(pos).block == Blocks.WATER
    }

    override fun shouldRun(pokemonEntity: PokemonEntity): Boolean {
        if (!config.fishingLootGeneratorsEnabled) return false
        return CobbleworkersTypeUtils.isAllowedByType(config.typeGeneratesFishingLoot, pokemonEntity) || isDesignatedGenerator(pokemonEntity)
    }

    /**
     * Checks if a Pokemon is touching water or standing directly next to a water block (1 block radius).
     */
    private fun isNearWater(world: World, pokemonEntity: PokemonEntity): Boolean {
        if (pokemonEntity.isTouchingWater) return true
        val pos = pokemonEntity.blockPos
        return BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1)).any { checkPos ->
            world.getBlockState(checkPos).block == Blocks.WATER
        }
    }

    /**
     * Finds the closest water block near the Pokemon and returns it.
     */
    private fun findNearbyWaterBlock(world: World, pokemonEntity: PokemonEntity): BlockPos? {
        val pos = pokemonEntity.blockPos
        return BlockPos.iterate(pos.add(-2, -2, -2), pos.add(2, 2, 2)).firstOrNull { checkPos ->
            world.getBlockState(checkPos).block == Blocks.WATER
        }?.toImmutable()
    }

    /**
     * Finds the closest cached water block for this pasture and navigates the Pokemon to it.
     */
    private fun navigateToWater(world: World, origin: BlockPos, pokemonEntity: PokemonEntity): Boolean {
        val waterTargets = CobbleworkersCacheManager.getTargets(origin, jobType)
        if (waterTargets.isEmpty()) return false

        val closest = waterTargets.minByOrNull { it.getSquaredDistance(pokemonEntity.blockPos) } ?: return false

        CobbleworkersNavigationUtils.navigateTo(pokemonEntity, closest.up())
        return true
    }

    /**
     * Plays a fishing catch effect: Pokemon cries and water splash particles appear.
     */
    private fun playCatchEffect(world: World, pokemonEntity: PokemonEntity) {
        // Pokemon cry on successful catch
        pokemonEntity.cry()

        // Spawn water splash particles at the nearest water block
        val waterPos = findNearbyWaterBlock(world, pokemonEntity)
        if (waterPos != null && world is ServerWorld) {
            val x = waterPos.x + 0.5
            val y = waterPos.y + 1.0
            val z = waterPos.z + 0.5

            // Splash particles
            world.spawnParticles(
                ParticleTypes.SPLASH,
                x, y, z,
                8,    // count
                0.3,  // deltaX
                0.1,  // deltaY
                0.3,  // deltaZ
                0.05  // speed
            )

            // Fishing wake particles
            world.spawnParticles(
                ParticleTypes.FISHING,
                x, y, z,
                4,
                0.2,
                0.0,
                0.2,
                0.01
            )

            // Bubble particles underwater
            world.spawnParticles(
                ParticleTypes.BUBBLE,
                x, y - 0.3, z,
                6,
                0.2,
                0.1,
                0.2,
                0.05
            )
        }
    }

    override fun tick(world: World, origin: BlockPos, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        val heldItems = heldItemsByPokemon[pokemonId]

        if (heldItems.isNullOrEmpty()) {
            // No items held - go fish
            if (isNearWater(world, pokemonEntity)) {
                // At the water - generate loot
                failedDepositLocations.remove(pokemonId)
                handleGeneration(world, origin, pokemonEntity)
            } else {
                // Not near water - navigate to water
                navigateToWater(world, origin, pokemonEntity)
            }
        } else {
            // Has items - go deposit
            CobbleworkersInventoryUtils.handleDepositing(world, origin, pokemonEntity, heldItems, failedDepositLocations, heldItemsByPokemon)
        }
    }

    fun handleGeneration(world: World, origin: BlockPos, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        val now = world.time
        val lastTime = lastGenerationTime[pokemonId] ?: 0L

        if (now - lastTime < cooldownTicks) {
            return
        }

        val treasureChancePercentage = treasureChance.toDouble() / 100
        val useTreasureTable = world.random.nextFloat() < treasureChancePercentage

        val lootParams = LootContextParameterSet.Builder(world as ServerWorld)
            .add(LootContextParameters.ORIGIN, pokemonEntity.blockPos.toCenterPos())
            .add(LootContextParameters.TOOL, ItemStack(Items.FISHING_ROD))
            .addOptional(LootContextParameters.THIS_ENTITY, pokemonEntity)
            .build(LootContextTypes.FISHING)

        val lootTable = if (useTreasureTable) {
            world.server.reloadableRegistries.getLootTable(LootTables.FISHING_TREASURE_GAMEPLAY)
        } else {
            world.server.reloadableRegistries.getLootTable(LootTables.FISHING_GAMEPLAY)
        }

        val drops = lootTable.generateLoot(lootParams)

        if (drops.isNotEmpty()) {
            lastGenerationTime[pokemonId] = now
            heldItemsByPokemon[pokemonId] = drops
            playCatchEffect(world, pokemonEntity)
        }
    }

    private fun isDesignatedGenerator(pokemonEntity: PokemonEntity): Boolean {
        val speciesName = pokemonEntity.pokemon.species.translatedName.string.lowercase()
        return config.fishingLootGenerators.any { it.lowercase() == speciesName }
    }
}
