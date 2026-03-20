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
 * Effects can be toggled globally (general.globalJobEffectsEnabled) and per job (effectsEnabled in each job group).
 */
object CobbleworkersJobEffects {

    private val config get() = CobbleworkersConfigHolder.config

    private fun isEnabled(jobKey: String): Boolean {
        if (!config.general.globalJobEffectsEnabled) return false
        return when (jobKey) {
            "apricorn" -> config.apricorn.effectsEnabled
            "irrigation" -> config.irrigation.effectsEnabled
            "amethyst" -> config.amethyst.effectsEnabled
            "tumblestone" -> config.tumblestone.effectsEnabled
            "cropHarvest" -> config.cropHarvest.effectsEnabled
            "berries" -> config.berries.effectsEnabled
            "honey" -> config.honey.effectsEnabled
            "mints" -> config.mints.effectsEnabled
            "lava" -> config.lava.effectsEnabled
            "water" -> config.water.effectsEnabled
            "snow" -> config.snow.effectsEnabled
            "fishing" -> config.fishing.effectsEnabled
            "pickup" -> config.pickup.effectsEnabled
            "diving" -> config.diving.effectsEnabled
            "groundItem" -> config.groundItemGathering.effectsEnabled
            "netherwart" -> config.netherwartHarvest.effectsEnabled
            "healing" -> config.healing.effectsEnabled
            "fuel" -> config.fuel.effectsEnabled
            "brewingStandFuel" -> config.brewingStandFuel.effectsEnabled
            "extinguisher" -> config.extinguisher.effectsEnabled
            "archeology" -> config.archeology.effectsEnabled
            "scouts" -> config.scouts.effectsEnabled
            else -> true
        }
    }

    fun playHarvestEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("physical", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.8
        val z = pokemonEntity.z

        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 5, 0.3, 0.2, 0.3, 0.02)
    }

    fun playGenerationEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        pokemonEntity.cry()
        pokemonEntity.playAnimation("special", emptyList())

        val x = pokemonEntity.x
        val y = pokemonEntity.y + pokemonEntity.height * 0.8
        val z = pokemonEntity.z

        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 5, 0.3, 0.2, 0.3, 0.02)
    }

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
