/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.jobs

import accieo.cobbleworkers.config.CobbleworkersConfigHolder
import accieo.cobbleworkers.enums.JobType
import accieo.cobbleworkers.interfaces.Worker
import accieo.cobbleworkers.utilities.CobbleworkersJobEffects
import accieo.cobbleworkers.utilities.CobbleworkersNavigationUtils
import accieo.cobbleworkers.utilities.CobbleworkersTypeUtils
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import java.util.UUID
import kotlin.text.lowercase

/**
 * Guard job: Pokemon patrols the area around the Pasture Block,
 * chases away wild Pokemon, and earns XP for each one repelled.
 * Gives half an XS Experience Candy (50 XP) per wild Pokemon repelled.
 */
object Guard : Worker {
    private val config get() = CobbleworkersConfigHolder.config.guard
    private val cooldownTicks get() = config.guardCooldownSeconds * 20L
    private val lastGuardTime = mutableMapOf<UUID, Long>()
    private val currentTarget = mutableMapOf<UUID, Int>() // entity ID of wild mon being chased

    override val jobType: JobType = JobType.Generic
    override val blockValidator: ((World, BlockPos) -> Boolean)? = null

    override fun shouldRun(pokemonEntity: PokemonEntity): Boolean {
        if (!config.guardsEnabled) return false
        return CobbleworkersTypeUtils.isAllowedByType(config.typeGuards, pokemonEntity) || isDesignatedGuard(pokemonEntity)
    }

    override fun tick(world: World, origin: BlockPos, pokemonEntity: PokemonEntity) {
        if (world !is ServerWorld) return
        val pokemonId = pokemonEntity.pokemon.uuid
        val now = world.time

        // Cooldown check
        val lastTime = lastGuardTime[pokemonId] ?: 0L
        if (now - lastTime < cooldownTicks) {
            // Show working particles while patrolling
            if (now % 40 == 0L) {
                CobbleworkersJobEffects.playWorkingParticles(world, pokemonEntity, "guard")
            }
            return
        }

        // Find nearest wild Pokemon within guard radius
        val guardRadius = config.guardRadius.toDouble()
        val searchBox = Box.of(origin.toCenterPos(), guardRadius * 2, guardRadius * 2, guardRadius * 2)
        val wildMons = world.getEntitiesByClass(PokemonEntity::class.java, searchBox) { entity ->
            entity != pokemonEntity && entity.pokemon.isWild() && entity.isAlive
        }

        if (wildMons.isEmpty()) return

        val targetMon = wildMons.minByOrNull { it.squaredDistanceTo(pokemonEntity) } ?: return

        // Check if we're close enough to the wild mon
        if (CobbleworkersNavigationUtils.isPokemonAtPosition(pokemonEntity, targetMon.blockPos, 3.0)) {
            // Repel the wild Pokemon!
            lastGuardTime[pokemonId] = now

            // Give XP to the guard Pokemon
            val xpAmount = config.xpPerRepel
            val pokemon = pokemonEntity.pokemon
            if (pokemon.canLevelUpFurther()) {
                pokemon.addExperience(GuardExperienceSource, xpAmount)
            }

            // Remove the wild Pokemon (repelled)
            targetMon.discard()

            // Play success effects
            CobbleworkersJobEffects.playWorkStartEffect(world, pokemonEntity, "guard")
            CobbleworkersJobEffects.playSuccessCry(world, pokemonEntity, "guard")
        } else {
            // Navigate towards the wild mon
            CobbleworkersNavigationUtils.navigateTo(pokemonEntity, targetMon.blockPos)
        }
    }

    private fun isDesignatedGuard(pokemonEntity: PokemonEntity): Boolean {
        val speciesName = pokemonEntity.pokemon.species.translatedName.string.lowercase()
        return config.guards.any { it.lowercase() == speciesName }
    }
}

/**
 * Custom ExperienceSource for guard duty XP.
 */
object GuardExperienceSource : com.cobblemon.mod.common.api.pokemon.experience.ExperienceSource {
    override fun isSidemod() = true
}
