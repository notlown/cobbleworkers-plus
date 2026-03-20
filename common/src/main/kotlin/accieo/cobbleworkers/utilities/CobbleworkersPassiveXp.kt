/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.utilities

import accieo.cobbleworkers.config.CobbleworkersConfigHolder
import accieo.cobbleworkers.jobs.GuardExperienceSource
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.world.World
import java.util.UUID

/**
 * Gives passive XP to all Pokemon that are out in the pasture.
 * Very slow - designed for roughly 1 level per real day of play.
 * Default: 10 XP every 5 minutes.
 */
object CobbleworkersPassiveXp {

    private val config get() = CobbleworkersConfigHolder.config.general
    private val intervalTicks get() = config.passiveXpIntervalSeconds * 20L
    private val lastXpTick = mutableMapOf<UUID, Long>()

    fun tick(world: World, pokemonEntity: PokemonEntity) {
        if (!config.passiveXpEnabled) return

        val pokemonId = pokemonEntity.pokemon.uuid
        val now = world.time
        val lastTime = lastXpTick[pokemonId] ?: now.also { lastXpTick[pokemonId] = it }

        if (now - lastTime < intervalTicks) return

        lastXpTick[pokemonId] = now

        val pokemon = pokemonEntity.pokemon
        if (pokemon.canLevelUpFurther()) {
            pokemon.addExperience(GuardExperienceSource, config.passiveXpAmount)
        }
    }

    fun cleanup(pokemonId: UUID) {
        lastXpTick.remove(pokemonId)
    }
}
