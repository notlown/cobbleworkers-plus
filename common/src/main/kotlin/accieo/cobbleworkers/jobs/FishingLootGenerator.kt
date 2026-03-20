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
import accieo.cobbleworkers.utilities.CobbleworkersJobEffects
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
    private val successTime = mutableMapOf<UUID, Long>()
    private val SUCCESS_PAUSE_TICKS = 40L // 2 seconds pause after catch before going to chest

    override val jobType: JobType = JobType.FishingLootGenerator

    override val blockValidator: ((World, BlockPos) -> Boolean) = { world, pos ->
        world.getBlockState(pos).block == Blocks.WATER
    }

    override fun shouldRun(pokemonEntity: PokemonEntity): Boolean {
        if (!config.fishingLootGeneratorsEnabled) return false
        return CobbleworkersTypeUtils.isAllowedByType(config.typeGeneratesFishingLoot, pokemonEntity) || isDesignatedGenerator(pokemonEntity)
    }

    private fun isNearWater(world: World, pokemonEntity: PokemonEntity): Boolean {
        if (pokemonEntity.isTouchingWater) return true
        val pos = pokemonEntity.blockPos
        return BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1)).any { checkPos ->
            world.getBlockState(checkPos).block == Blocks.WATER
        }
    }

    private fun navigateToWater(world: World, origin: BlockPos, pokemonEntity: PokemonEntity): Boolean {
        val waterTargets = CobbleworkersCacheManager.getTargets(origin, jobType)
        if (waterTargets.isEmpty()) return false
        val closest = waterTargets.minByOrNull { it.getSquaredDistance(pokemonEntity.blockPos) } ?: return false
        CobbleworkersNavigationUtils.navigateTo(pokemonEntity, closest.up())
        return true
    }

    override fun tick(world: World, origin: BlockPos, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        val heldItems = heldItemsByPokemon[pokemonId]
        val now = world.time

        if (heldItems.isNullOrEmpty()) {
            if (isNearWater(world, pokemonEntity)) {
                failedDepositLocations.remove(pokemonId)

                val lastTime = lastGenerationTime[pokemonId] ?: 0L
                if (now - lastTime < cooldownTicks) {
                    // Working - show particles every second as "busy" indicator
                    if (now % 20 == 0L) {
                        CobbleworkersJobEffects.playWorkingParticles(world, pokemonEntity, "fishing")
                    }
                    return
                }

                // Cooldown done - generate loot
                handleGeneration(world, origin, pokemonEntity)
            } else {
                navigateToWater(world, origin, pokemonEntity)
            }
        } else {
            // Has items - wait for success pause, then go deposit
            val catchTime = successTime[pokemonId]
            if (catchTime != null && now - catchTime < SUCCESS_PAUSE_TICKS) {
                // Still in success pause - play cry 20 ticks after the attack animation
                if (now - catchTime == 20L) {
                    CobbleworkersJobEffects.playSuccessCry(world, pokemonEntity, "fishing")
                }
                return
            }
            successTime.remove(pokemonId)
            CobbleworkersInventoryUtils.handleDepositing(world, origin, pokemonEntity, heldItems, failedDepositLocations, heldItemsByPokemon)
        }
    }

    fun handleGeneration(world: World, origin: BlockPos, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        val now = world.time

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
            successTime[pokemonId] = now
            // Attack animation immediately on catch
            CobbleworkersJobEffects.playWorkStartEffect(world, pokemonEntity, "fishing")
            // Cry comes 1 second later (handled in tick)
        }
    }

    private fun isDesignatedGenerator(pokemonEntity: PokemonEntity): Boolean {
        val speciesName = pokemonEntity.pokemon.species.translatedName.string.lowercase()
        return config.fishingLootGenerators.any { it.lowercase() == speciesName }
    }
}
