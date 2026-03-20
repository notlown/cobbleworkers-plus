/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.utilities

import accieo.cobbleworkers.config.CobbleworkersConfigHolder
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World
import java.util.UUID

/**
 * Stamina system for working Pokémon.
 * Each Pokémon has a limited number of charges before needing to rest.
 * While resting, the Pokémon enters sleep pose and shows Zzz particles.
 */
object CobbleworkersStamina {

    private val config get() = CobbleworkersConfigHolder.config.general
    private val maxCharges get() = config.staminaCharges
    private val restTicks get() = config.restDurationSeconds * 20L

    // Current charges per Pokemon (starts at max)
    private val charges = mutableMapOf<UUID, Int>()
    // Tick when rest started (null = not resting)
    private val restStartTick = mutableMapOf<UUID, Long>()
    // Track if we already set sleep pose
    private val isSleeping = mutableSetOf<UUID>()

    /**
     * Check if stamina system is enabled.
     */
    fun isEnabled(): Boolean = config.staminaEnabled

    /**
     * Get current charges for a Pokemon. Returns max if not tracked yet.
     */
    fun getCharges(pokemonId: UUID): Int {
        return charges.getOrPut(pokemonId) { maxCharges }
    }

    /**
     * Check if a Pokemon is currently resting.
     * Also handles the rest timer, Zzz particles, and wake-up.
     * Returns true if the Pokemon is resting (caller should skip job tick).
     */
    fun isResting(world: World, pokemonEntity: PokemonEntity): Boolean {
        if (!isEnabled()) return false

        val pokemonId = pokemonEntity.pokemon.uuid
        val startTick = restStartTick[pokemonId] ?: return false
        val now = world.time
        val elapsed = now - startTick

        if (elapsed >= restTicks) {
            // Wake up!
            wakeUp(world, pokemonEntity)
            return false
        }

        // Still resting - show Zzz particles every 30 ticks (~1.5 sec)
        if (world is ServerWorld && now % 30 == 0L) {
            val x = pokemonEntity.x
            val y = pokemonEntity.y + pokemonEntity.height + 0.3
            val z = pokemonEntity.z

            // Zzz note particles floating upward
            world.spawnParticles(
                ParticleTypes.NOTE,
                x, y, z,
                1, 0.1, 0.2, 0.1, 0.0
            )

            // Snoring smoke puffs
            world.spawnParticles(
                ParticleTypes.CLOUD,
                x, y - 0.2, z,
                2, 0.15, 0.05, 0.15, 0.005
            )
        }

        return true
    }

    /**
     * Consume one stamina charge after a successful job action.
     * If charges reach 0, the Pokemon starts resting.
     */
    fun useCharge(world: World, pokemonEntity: PokemonEntity) {
        if (!isEnabled()) return

        val pokemonId = pokemonEntity.pokemon.uuid
        val current = charges.getOrPut(pokemonId) { maxCharges }
        val remaining = (current - 1).coerceAtLeast(0)
        charges[pokemonId] = remaining

        if (remaining <= 0) {
            // Start resting
            startRest(world, pokemonEntity)
        }
    }

    /**
     * Start the rest phase - Pokemon goes to sleep.
     */
    private fun startRest(world: World, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        restStartTick[pokemonId] = world.time
        isSleeping.add(pokemonId)

        // Stop navigation
        pokemonEntity.navigation.stop()

        // Send sleep animation
        if (world is ServerWorld) {
            CobbleworkersJobEffects.sendAnimationPublic(world, pokemonEntity, "sleep")

            // Initial Zzz burst
            val x = pokemonEntity.x
            val y = pokemonEntity.y + pokemonEntity.height + 0.3
            val z = pokemonEntity.z
            world.spawnParticles(ParticleTypes.CLOUD, x, y, z, 5, 0.3, 0.2, 0.3, 0.01)
        }
    }

    /**
     * Wake up the Pokemon - restore charges and play wake-up effect.
     */
    private fun wakeUp(world: World, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        charges[pokemonId] = maxCharges
        restStartTick.remove(pokemonId)
        isSleeping.remove(pokemonId)

        // Wake-up effects
        if (world is ServerWorld) {
            CobbleworkersJobEffects.sendAnimationPublic(world, pokemonEntity, "cry")

            val x = pokemonEntity.x
            val y = pokemonEntity.y + pokemonEntity.height * 0.8
            val z = pokemonEntity.z
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 10, 0.4, 0.3, 0.4, 0.03)
        }
    }

    /**
     * Clean up data for a Pokemon that's no longer in a pasture.
     */
    fun cleanup(pokemonId: UUID) {
        charges.remove(pokemonId)
        restStartTick.remove(pokemonId)
        isSleeping.remove(pokemonId)
    }
}
