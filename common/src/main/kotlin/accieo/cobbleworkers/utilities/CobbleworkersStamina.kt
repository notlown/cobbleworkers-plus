/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.utilities

import org.apache.logging.log4j.LogManager
import accieo.cobbleworkers.config.CobbleworkersConfigHolder
import com.cobblemon.mod.common.CobblemonActivities
import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer
import net.minecraft.entity.ai.brain.MemoryModuleType
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
    private val LOG = LogManager.getLogger("CW-Stamina")
    private val config get() = CobbleworkersConfigHolder.config.general
    private val maxCharges get() = config.staminaCharges
    private val restTicks get() = config.restDurationSeconds * 20L

    private val charges = mutableMapOf<UUID, Int>()
    private val restStartTick = mutableMapOf<UUID, Long>()
    private val isSleeping = mutableSetOf<UUID>()
    // Tick when exhaustion started (before actually sleeping)
    private val exhaustedSinceTick = mutableMapOf<UUID, Long>()
    private val EXHAUSTION_DELAY_TICKS = 100L // 5 seconds before falling asleep

    /**
     * Check if stamina system is enabled.
     */
    fun isEnabled(): Boolean = config.staminaEnabled

    /**
     * Get current charges for a Pokemon. Returns max if not tracked yet.
     */
    fun getCharges(pokemonId: UUID): Int {
        val current = charges.getOrPut(pokemonId) { maxCharges }
        // If config changed to lower max, cap current charges
        if (current > maxCharges) {
            charges[pokemonId] = maxCharges
            return maxCharges
        }
        return current
    }

    /**
     * Check if a Pokemon is currently resting.
     * Also handles the rest timer, Zzz particles, and wake-up.
     * Returns true if the Pokemon is resting (caller should skip job tick).
     */
    fun isResting(world: World, pokemonEntity: PokemonEntity): Boolean {
        if (!isEnabled()) return false

        val pokemonId = pokemonEntity.pokemon.uuid
        val now = world.time

        // Check exhaustion delay - still working but about to sleep
        val exhaustedTick = exhaustedSinceTick[pokemonId]
        if (exhaustedTick != null) {
            if (now - exhaustedTick >= EXHAUSTION_DELAY_TICKS) {
                // Delay is over, actually start sleeping
                exhaustedSinceTick.remove(pokemonId)
                startRest(world, pokemonEntity)
            } else {
                // Still in delay - let the Pokemon finish depositing etc.
                return false
            }
        }

        val startTick = restStartTick[pokemonId] ?: return false
        val elapsed = now - startTick

        if (elapsed >= restTicks) {
            // Wake up!
            wakeUp(world, pokemonEntity)
            return false
        }

        // Keep Pokemon still and sleeping
        pokemonEntity.navigation.stop()
        if (pokemonEntity.dataTracker.get(PokemonEntity.POSE_TYPE) != PoseType.SLEEP) {
            pokemonEntity.dataTracker.set(PokemonEntity.POSE_TYPE, PoseType.SLEEP)
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
        val current = getCharges(pokemonId)
        val remaining = (current - 1).coerceAtLeast(0)
        charges[pokemonId] = remaining
        LOG.info("[STAMINA] useCharge: ${pokemonEntity.pokemon.species.name} charges=$current->$remaining")

        if (remaining <= 0 && !isSleeping.contains(pokemonId) && !exhaustedSinceTick.containsKey(pokemonId)) {
            LOG.info("[STAMINA] ${pokemonEntity.pokemon.species.name} exhausted, will sleep in 5 seconds")
            exhaustedSinceTick[pokemonId] = world.time
        }
    }

    /**
     * Start the rest phase - Pokemon goes to sleep.
     */
    private fun startRest(world: World, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        restStartTick[pokemonId] = world.time
        isSleeping.add(pokemonId)

        // Full Cobblemon sleep: Brain Activity + PoseType + Status
        pokemonEntity.navigation.stop()
        pokemonEntity.enablePoseTypeRecalculation = false
        pokemonEntity.dataTracker.set(PokemonEntity.POSE_TYPE, PoseType.SLEEP)
        try {
            pokemonEntity.brain.doExclusively(CobblemonActivities.POKEMON_SLEEPING_ACTIVITY)
            pokemonEntity.brain.remember(CobblemonMemories.POKEMON_SLEEPING, true)
        } catch (_: Exception) { }
        pokemonEntity.pokemon.status = PersistentStatusContainer(Statuses.SLEEP)
        LOG.info("[STAMINA] startRest: ${pokemonEntity.pokemon.species.name} pose=${pokemonEntity.dataTracker.get(PokemonEntity.POSE_TYPE)} brain-activity=sleeping")
    }

    /**
     * Wake up the Pokemon - restore charges and play wake-up effect.
     */
    private fun wakeUp(world: World, pokemonEntity: PokemonEntity) {
        val pokemonId = pokemonEntity.pokemon.uuid
        charges[pokemonId] = maxCharges
        restStartTick.remove(pokemonId)
        isSleeping.remove(pokemonId)

        // Wake up - restore everything
        pokemonEntity.pokemon.status = null
        pokemonEntity.enablePoseTypeRecalculation = true
        pokemonEntity.dataTracker.set(PokemonEntity.POSE_TYPE, PoseType.STAND)
        try {
            pokemonEntity.brain.forget(CobblemonMemories.POKEMON_SLEEPING)
            pokemonEntity.brain.resetPossibleActivities()
        } catch (_: Exception) { }

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
        exhaustedSinceTick.remove(pokemonId)
    }
}
