/*
 * Copyright (C) 2025 Accieo
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.jobs

import accieo.cobbleworkers.enums.JobType
import accieo.cobbleworkers.interfaces.Worker
import accieo.cobbleworkers.utilities.CobbleworkersStamina
import accieo.cobbleworkers.utilities.DeferredBlockScanner
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object WorkerDispatcher {
    private val workers: List<Worker> = listOf(
        ApricornHarvester,
        AmethystHarvester,
        Archeologist,
        BerryHarvester,
        BrewingStandFuelGenerator,
        CropHarvester,
        CropIrrigator,
        DiveLooter,
        FireExtinguisher,
        FishingLootGenerator,
        FuelGenerator,
        GroundItemGatherer,
        Healer,
        HoneyCollector,
        LavaGenerator,
        MintHarvester,
        NetherwartHarvester,
        PickUpLooter,
        Scout,
        SnowGenerator,
        TumblestoneHarvester,
        WaterGenerator,
        Guard,
    )

    private val jobValidators: Map<JobType, (World, BlockPos) -> Boolean> = workers
        .mapNotNull { worker -> worker.blockValidator?.let { worker.jobType to it } }
        .toMap()

    /**
     * Public access to worker list (used by JobAssignmentManager to check available jobs).
     */
    fun getWorkers(): List<Worker> = workers

    fun tickAreaScan(world: World, pastureOrigin: BlockPos) {
        DeferredBlockScanner.tickPastureAreaScan(world, pastureOrigin, jobValidators)
    }

    /**
     * Ticks the action logic for a specific Pokémon.
     * Respects job assignments - if a job is assigned, only that job runs.
     */
    fun tickPokemon(world: World, pastureOrigin: BlockPos, pokemonEntity: PokemonEntity) {
        if (CobbleworkersStamina.isResting(world, pokemonEntity)) return

        val pokemonId = pokemonEntity.pokemon.uuid

        workers
            .filter { it.shouldRun(pokemonEntity) }
            .filter { JobAssignmentManager.isJobAllowed(pokemonId, it.jobType) }
            .forEach { it.tick(world, pastureOrigin, pokemonEntity) }
    }
}
