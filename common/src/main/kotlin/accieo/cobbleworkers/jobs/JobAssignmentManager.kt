/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.jobs

import accieo.cobbleworkers.enums.JobType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.util.math.BlockPos
import java.util.UUID

/**
 * Manages job assignments per Pokemon.
 * When a Pokemon has an assigned job, it will ONLY perform that job.
 * When no job is assigned (null), it works like the original - tries all matching jobs.
 *
 * Uses Pokemon UUID as key (globally unique per Pokemon).
 */
object JobAssignmentManager {

    // pokemonUUID -> assigned JobType (null = auto)
    private val assignments = mutableMapOf<UUID, JobType?>()

    fun isJobAllowed(pokemonId: UUID, workerJobType: JobType): Boolean {
        val assigned = assignments[pokemonId] ?: return true // auto mode
        return assigned == workerJobType
    }

    fun setAssignment(pokemonId: UUID, jobType: JobType?) {
        if (jobType == null) {
            assignments.remove(pokemonId)
        } else {
            assignments[pokemonId] = jobType
        }
    }

    fun getAssignment(pokemonId: UUID): JobType? {
        return assignments[pokemonId]
    }

    fun removeAssignment(pokemonId: UUID) {
        assignments.remove(pokemonId)
    }

    /**
     * Get list of jobs a Pokemon can do (for GUI display).
     */
    fun getAvailableJobs(pokemonEntity: PokemonEntity): List<JobType> {
        return WorkerDispatcher.getWorkers()
            .filter { it.shouldRun(pokemonEntity) }
            .map { it.jobType }
    }

    /**
     * Get a human-readable name for a JobType.
     */
    fun getJobDisplayName(jobType: JobType): String {
        return jobType.name
            .replace("LootGenerator", "")
            .replace("Harvester", "")
            .replace("Generator", "")
            .replace("Extinguisher", "")
            .replace("Collector", "")
            .replace("Irrigator", "")
            .replace("Gatherer", "")
            .replace("Looter", "")
    }
}
