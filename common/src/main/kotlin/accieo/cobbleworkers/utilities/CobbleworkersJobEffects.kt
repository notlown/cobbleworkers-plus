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
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.animation.PlayPosableAnimationPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

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
            "guard" -> config.guard.effectsEnabled
            else -> true
        }
    }

    /**
     * Sends a Cobblemon animation packet to all nearby players so the animation plays client-side.
     * Uses the same approach as PokemonEntity.cry() internally: finds nearby ServerPlayerEntity
     * instances and sends via CobblemonNetwork.
     */
    fun sendAnimationPublic(world: World, pokemonEntity: PokemonEntity, vararg animationNames: String) =
        sendAnimation(world, pokemonEntity, *animationNames)

    private fun sendAnimation(world: World, pokemonEntity: PokemonEntity, vararg animationNames: String) {
        if (world !is ServerWorld) return
        val packet = PlayPosableAnimationPacket(
            pokemonEntity.id,
            animationNames.toSet(),
            emptyList()
        )
        val box = Box.of(pokemonEntity.pos, 128.0, 128.0, 128.0)
        val nearbyPlayers = world.getEntitiesByClass(ServerPlayerEntity::class.java, box) { true }
        for (player in nearbyPlayers) {
            CobblemonNetwork.sendPacketToPlayer(player, packet)
        }
    }

    // ── Phase-based effects (3-phase system: start → working → success) ──

    /**
     * Phase 1: Pokemon starts working - plays attack animation (physical or special).
     * Called once when the Pokemon first arrives at the work location.
     */
    /**
     * Phase 1: Pokemon caught something - plays attack animation + splash particles.
     * Tries physical/special (only ~8% of Pokemon have these), falls back gracefully.
     */
    /**
     * Phase 1: Pokemon caught/completed something - plays attack animation + particles.
     * Uses Cobblemon's fallback chain system so every Pokemon finds a matching animation.
     */
    fun playWorkStartEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        // Use fallback chains like Cobblemon's battle system:
        // Water jobs: watergun → spray → beam → special
        // Fire jobs: ember → flame → special
        // Harvest/physical jobs: tackle → charge → blunt → physical
        // Generic: special → physical
        when (jobKey) {
            "fishing", "diving", "water", "snow" ->
                sendAnimation(world, pokemonEntity, "watergun", "bubble", "spray", "beam", "special")
            "lava", "fuel", "brewingStandFuel" ->
                sendAnimation(world, pokemonEntity, "ember", "flamethrower", "flame", "special")
            "extinguisher" ->
                sendAnimation(world, pokemonEntity, "watergun", "bubble", "spray", "special")
            "healing" ->
                sendAnimation(world, pokemonEntity, "wish", "special")
            "guard" ->
                sendAnimation(world, pokemonEntity, "tackle", "bite", "crunch", "scratch", "charge", "blunt", "physical")
            "apricorn", "amethyst", "berries", "tumblestone", "cropHarvest",
            "mints", "netherwart", "honey", "irrigation", "groundItem" ->
                sendAnimation(world, pokemonEntity, "tackle", "scratch", "pound", "charge", "blunt", "physical")
            else ->
                sendAnimation(world, pokemonEntity, "tackle", "special", "physical")
        }

        // Particle burst as visual feedback
        val x = pokemonEntity.x
        val y = pokemonEntity.y
        val z = pokemonEntity.z

        val h = pokemonEntity.height.toDouble()
        when (jobKey) {
            "fishing" -> {
                // Big splash explosion
                world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 40, 0.5, 0.3, 0.5, 0.3)
                world.spawnParticles(ParticleTypes.FISHING, x, y, z, 15, 0.5, 0.0, 0.5, 0.05)
                world.spawnParticles(ParticleTypes.BUBBLE_POP, x, y + 0.5, z, 10, 0.3, 0.3, 0.3, 0.05)
            }
            "lava", "fuel", "brewingStandFuel" -> {
                world.spawnParticles(ParticleTypes.FLAME, x, y + h * 0.5, z, 25, 0.4, 0.3, 0.4, 0.05)
                world.spawnParticles(ParticleTypes.LAVA, x, y + h * 0.3, z, 8, 0.3, 0.2, 0.3, 0.0)
                world.spawnParticles(ParticleTypes.SMOKE, x, y + h, z, 10, 0.3, 0.2, 0.3, 0.02)
            }
            "water", "snow", "extinguisher" -> {
                world.spawnParticles(ParticleTypes.SPLASH, x, y + h * 0.3, z, 30, 0.4, 0.3, 0.4, 0.2)
                world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, y + h, z, 10, 0.3, 0.2, 0.3, 0.0)
            }
            "healing" -> {
                world.spawnParticles(ParticleTypes.HEART, x, y + h, z, 10, 0.5, 0.3, 0.5, 0.02)
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y + h * 0.5, z, 15, 0.4, 0.3, 0.4, 0.02)
            }
            "guard" -> {
                world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, x, y + h, z, 8, 0.4, 0.3, 0.4, 0.02)
                world.spawnParticles(ParticleTypes.CRIT, x, y + h * 0.5, z, 15, 0.5, 0.3, 0.5, 0.1)
                world.spawnParticles(ParticleTypes.SMOKE, x, y, z, 10, 0.4, 0.2, 0.4, 0.03)
            }
            else -> {
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y + h, z, 15, 0.5, 0.3, 0.5, 0.03)
                world.spawnParticles(ParticleTypes.COMPOSTER, x, y + h * 0.5, z, 10, 0.3, 0.2, 0.3, 0.05)
            }
        }
    }

    /**
     * Phase 2: Pokemon is working - show particles as "busy" indicator.
     * Called periodically (e.g. every second) while on cooldown.
     */
    fun playWorkingParticles(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        val x = pokemonEntity.x
        val y = pokemonEntity.y
        val z = pokemonEntity.z

        val h = pokemonEntity.height.toDouble()
        when (jobKey) {
            "fishing" -> {
                world.spawnParticles(ParticleTypes.FISHING, x, y, z, 5, 0.5, 0.0, 0.5, 0.02)
                world.spawnParticles(ParticleTypes.BUBBLE_POP, x, y + 0.3, z, 3, 0.3, 0.2, 0.3, 0.01)
                world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 8, 0.4, 0.1, 0.4, 0.05)
            }
            "lava", "fuel", "brewingStandFuel" -> {
                world.spawnParticles(ParticleTypes.FLAME, x, y + h * 0.5, z, 4, 0.2, 0.1, 0.2, 0.02)
                world.spawnParticles(ParticleTypes.SMOKE, x, y + h, z, 2, 0.2, 0.1, 0.2, 0.01)
            }
            "water", "snow", "extinguisher" -> {
                world.spawnParticles(ParticleTypes.SPLASH, x, y + h * 0.3, z, 6, 0.3, 0.2, 0.3, 0.03)
                world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, y + h, z, 3, 0.2, 0.1, 0.2, 0.0)
            }
            "healing" -> {
                world.spawnParticles(ParticleTypes.HEART, x, y + h, z, 2, 0.3, 0.2, 0.3, 0.01)
            }
            "diving" -> {
                world.spawnParticles(ParticleTypes.BUBBLE_POP, x, y + 0.3, z, 5, 0.3, 0.2, 0.3, 0.03)
                world.spawnParticles(ParticleTypes.BUBBLE, x, y, z, 5, 0.3, 0.2, 0.3, 0.03)
            }
            "guard" -> {
                world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, x, y + h, z, 2, 0.2, 0.1, 0.2, 0.01)
            }
            else -> {
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y + h, z, 3, 0.3, 0.2, 0.3, 0.02)
            }
        }
    }

    /**
     * Phase 3: Pokemon finished - plays cry as success signal.
     * Called once when loot is generated or action completes.
     */
    fun playSuccessCry(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return

        sendAnimation(world, pokemonEntity, "cry")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
    }

    // ── Legacy single-shot effects (used by jobs not yet migrated to 3-phase) ──

    fun playHarvestEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return
        sendAnimation(world, pokemonEntity, "cry")
        sendAnimation(world, pokemonEntity, "tackle", "scratch", "pound", "physical")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
        val x = pokemonEntity.x; val h = pokemonEntity.height.toDouble(); val z = pokemonEntity.z
        // Green sparkles + leaf/crop particles
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, pokemonEntity.y + h, z, 15, 0.5, 0.3, 0.5, 0.03)
        world.spawnParticles(ParticleTypes.COMPOSTER, x, pokemonEntity.y + h * 0.5, z, 10, 0.3, 0.2, 0.3, 0.05)
    }

    fun playGenerationEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return
        sendAnimation(world, pokemonEntity, "cry")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
        val x = pokemonEntity.x; val h = pokemonEntity.height.toDouble(); val y = pokemonEntity.y; val z = pokemonEntity.z

        when (jobKey) {
            "archeology" -> {
                // Digging effect: dust + dirt particles
                sendAnimation(world, pokemonEntity, "tackle", "scratch", "pound", "physical")
                world.spawnParticles(ParticleTypes.ASH, x, y + h * 0.3, z, 20, 0.4, 0.2, 0.4, 0.02)
                world.spawnParticles(ParticleTypes.CRIT, x, y + h * 0.5, z, 12, 0.3, 0.2, 0.3, 0.05)
                world.spawnParticles(ParticleTypes.ENCHANT, x, y + h, z, 10, 0.3, 0.3, 0.3, 0.3)
            }
            "diving" -> {
                // Underwater treasure effect: bubbles + splash
                sendAnimation(world, pokemonEntity, "watergun", "bubble", "spray", "special")
                world.spawnParticles(ParticleTypes.BUBBLE_POP, x, y + h * 0.5, z, 15, 0.4, 0.3, 0.4, 0.05)
                world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 25, 0.5, 0.2, 0.5, 0.2)
                world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, y + h, z, 8, 0.3, 0.2, 0.3, 0.0)
            }
            "pickup" -> {
                // Lucky find effect: enchant glitter + stars
                sendAnimation(world, pokemonEntity, "special")
                world.spawnParticles(ParticleTypes.ENCHANT, x, y + h + 0.5, z, 25, 0.4, 0.4, 0.4, 0.5)
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y + h, z, 10, 0.4, 0.2, 0.4, 0.03)
                world.spawnParticles(ParticleTypes.CRIT, x, y + h * 0.5, z, 8, 0.3, 0.2, 0.3, 0.1)
            }
            "scouts" -> {
                // Map discovery effect: enchant + sparkle
                sendAnimation(world, pokemonEntity, "special")
                world.spawnParticles(ParticleTypes.ENCHANT, x, y + h + 0.5, z, 30, 0.5, 0.5, 0.5, 0.8)
                world.spawnParticles(ParticleTypes.END_ROD, x, y + h, z, 8, 0.3, 0.3, 0.3, 0.02)
            }
            else -> {
                sendAnimation(world, pokemonEntity, "special")
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y + h, z, 12, 0.5, 0.3, 0.5, 0.03)
                world.spawnParticles(ParticleTypes.ENCHANT, x, y + h + 0.5, z, 20, 0.4, 0.3, 0.4, 0.5)
            }
        }
    }

    fun playFishingEffect(world: World, pokemonEntity: PokemonEntity, waterPos: BlockPos?) {
        if (!isEnabled("fishing")) return
        if (world !is ServerWorld) return
        sendAnimation(world, pokemonEntity, "cry")
        sendAnimation(world, pokemonEntity, "watergun", "bubble", "spray", "special")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
        val x = pokemonEntity.x; val y = pokemonEntity.y; val z = pokemonEntity.z
        world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 40, 0.5, 0.3, 0.5, 0.3)
        world.spawnParticles(ParticleTypes.FISHING, x, y, z, 15, 0.5, 0.0, 0.5, 0.05)
        world.spawnParticles(ParticleTypes.BUBBLE_POP, x, y + 0.5, z, 10, 0.3, 0.3, 0.3, 0.05)
    }

    fun playFireEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return
        sendAnimation(world, pokemonEntity, "cry")
        sendAnimation(world, pokemonEntity, "ember", "flamethrower", "flame", "special")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
        val x = pokemonEntity.x; val h = pokemonEntity.height.toDouble(); val z = pokemonEntity.z
        world.spawnParticles(ParticleTypes.FLAME, x, pokemonEntity.y + h * 0.5, z, 25, 0.4, 0.3, 0.4, 0.05)
        world.spawnParticles(ParticleTypes.LAVA, x, pokemonEntity.y + h * 0.3, z, 8, 0.3, 0.2, 0.3, 0.0)
        world.spawnParticles(ParticleTypes.SMOKE, x, pokemonEntity.y + h, z, 10, 0.3, 0.2, 0.3, 0.02)
    }

    fun playHealEffect(world: World, pokemonEntity: PokemonEntity) {
        if (!isEnabled("healing")) return
        if (world !is ServerWorld) return
        sendAnimation(world, pokemonEntity, "cry")
        sendAnimation(world, pokemonEntity, "wish", "special")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
        val x = pokemonEntity.x; val h = pokemonEntity.height.toDouble(); val z = pokemonEntity.z
        world.spawnParticles(ParticleTypes.HEART, x, pokemonEntity.y + h, z, 10, 0.5, 0.3, 0.5, 0.02)
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, pokemonEntity.y + h * 0.5, z, 15, 0.4, 0.3, 0.4, 0.02)
    }

    fun playWaterEffect(world: World, pokemonEntity: PokemonEntity, jobKey: String) {
        if (!isEnabled(jobKey)) return
        if (world !is ServerWorld) return
        sendAnimation(world, pokemonEntity, "cry")
        sendAnimation(world, pokemonEntity, "watergun", "bubble", "spray", "special")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
        val x = pokemonEntity.x; val h = pokemonEntity.height.toDouble(); val z = pokemonEntity.z
        world.spawnParticles(ParticleTypes.SPLASH, x, pokemonEntity.y + h * 0.3, z, 30, 0.4, 0.3, 0.4, 0.2)
        world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, pokemonEntity.y + h, z, 10, 0.3, 0.2, 0.3, 0.0)
        world.spawnParticles(ParticleTypes.BUBBLE_POP, x, pokemonEntity.y + h * 0.5, z, 8, 0.3, 0.2, 0.3, 0.03)
    }

    fun playExtinguishEffect(world: World, pokemonEntity: PokemonEntity) {
        if (!isEnabled("extinguisher")) return
        if (world !is ServerWorld) return
        sendAnimation(world, pokemonEntity, "cry")
        sendAnimation(world, pokemonEntity, "watergun", "bubble", "spray", "special")
        CobbleworkersStamina.useCharge(world, pokemonEntity)
        val x = pokemonEntity.x; val h = pokemonEntity.height.toDouble(); val z = pokemonEntity.z
        world.spawnParticles(ParticleTypes.CLOUD, x, pokemonEntity.y + h * 0.5, z, 20, 0.5, 0.3, 0.5, 0.05)
        world.spawnParticles(ParticleTypes.SPLASH, x, pokemonEntity.y, z, 15, 0.4, 0.1, 0.4, 0.1)
    }
}
