/*
 * Copyright (C) 2025 Accieo
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
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Handles visual and audio effects when a Pokémon successfully completes a job action.
 * Effects can be toggled globally or per-job via the jobEffects config group.
 */
object CobbleworkersJobEffects {

    private val effectsConfig get() = CobbleworkersConfigHolder.config.jobEffects

    private fun isEnabled(jobKey: String): Boolean {
        if (!effectsConfig.globalEffectsEnabled) return false
        return when (jobKey) {
            "apricorn" -> effectsConfig.apricornEffects
            "irrigation" -> effectsConfig.irrigationEffects
            "amethyst" -> effectsConfig.amethystEffects
            "tumblestone" -> effectsConfig.tumblestoneEffects
            "cropHarvest" -> effectsConfig.cropHarvestEffects
            "berries" -> effectsConfig.berriesEffects
            "honey" -> effectsConfig.honeyEffects
            "mints" -> effectsConfig.mintsEffects
            "lava" -> effectsConfig.lavaEffects
            "water" -> effectsConfig.waterEffects
            "snow" -> effectsConfig.snowEffects
            "fishing" -> effectsConfig.fishingEffects
            "pickup" -> effectsConfig.pickupEffects
            "diving" -> effectsConfig.divingEffects
            "groundItem" -> effectsConfig.groundItemEffects
            "netherwart" -> effectsConfig.netherwartEffects
            "healing" -> effectsConfig.healingEffects
            "fuel" -> effectsConfig.fuelEffects
            "brewingStandFuel" -> effectsConfig.brewingStandFuelEffects
            "extinguisher" -> effectsConfig.extinguisherEffects
            "archeology" -> effectsConfig.archeologyEffects
            "scouts" -> effectsConfig.scoutsEffects
            else -> true
        }
    }

    // ── Harvest effects: physical attack animation + particles ──

    fun playHarvestEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("physical", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.8
        val z = pokemonEntity.z

        world.spawnParticles(
            ParticleTypes.HAPPY_VILLAGER,
            x, y, z,
            5, 0.3, 0.2, 0.3, 0.02
        )
    }

    // ── Generation effects: special attack animation + particles ──

    fun playGenerationEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("special", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.8
        val z = pokemonEntity.z

        world.spawnParticles(
            ParticleTypes.HAPPY_VILLAGER,
            x, y, z,
            5, 0.3, 0.2, 0.3, 0.02
        )
    }

    // ── Fishing: special animation + water splash ──

    fun playFishingEffect(world: World, pokemonEntity: PokemonEntity, waterPos: BlockPos?) {
        if (!isEnabled("fishing")) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("special", emptyList())

        if (waterPos != null) {
            val x = waterPos.x + 0.5
            val y = waterPos.y + 1.0
            val z = waterPos.z + 0.5

            world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 8, 0.3, 0.1, 0.3, 0.05)
            world.spawnParticles(ParticleTypes.FISHING, x, y, z, 4, 0.2, 0.0, 0.2, 0.01)
            world.spawnParticles(ParticleTypes.BUBBLE, x, y - 0.3, z, 6, 0.2, 0.1, 0.2, 0.05)
        }
    }

    // ── Fire jobs: special animation + flame particles ──

    fun playFireEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("special", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.5
        val z = pokemonEntity.z

        world.spawnParticles(ParticleTypes.FLAME, x, y, z, 6, 0.3, 0.2, 0.3, 0.02)
    }

    // ── Healing: special animation + heart particles ──

    fun playHealEffect(world: World, pokemonEntity: PokemonEntity) {
        if (!isEnabled("healing")) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("special", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.8
        val z = pokemonEntity.z

        world.spawnParticles(ParticleTypes.HEART, x, y, z, 4, 0.3, 0.2, 0.3, 0.02)
    }

    // ── Water generation: special animation + splash ──

    fun playWaterEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("special", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.5
        val z = pokemonEntity.z

        world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 6, 0.3, 0.2, 0.3, 0.05)
        world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, y + 0.3, z, 3, 0.2, 0.1, 0.2, 0.01)
    }

    // ── Extinguisher: special animation + cloud particles ──

    fun playExtinguishEffect(world: World, pokemonEntity: PokemonEntity) {
        if (!isEnabled("extinguisher")) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("special", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.5
        val z = pokemonEntity.z

        world.spawnParticles(ParticleTypes.CLOUD, x, y, z, 8, 0.4, 0.2, 0.4, 0.03)
    }
}
