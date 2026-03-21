/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.net

import accieo.cobbleworkers.Cobbleworkers
import accieo.cobbleworkers.enums.JobType
import accieo.cobbleworkers.jobs.JobAssignmentManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.UUID

/**
 * Client -> Server packet: Player assigns a job to a Pokemon in a Pasture.
 */
data class JobAssignmentC2SPacket(
    val pastureId: UUID,
    val pokemonId: UUID,
    val jobName: String // empty string = auto mode
) : CustomPayload {

    companion object {
        val ID = CustomPayload.Id<JobAssignmentC2SPacket>(Identifier.of(Cobbleworkers.MODID, "job_assignment"))

        val CODEC: PacketCodec<PacketByteBuf, JobAssignmentC2SPacket> = object : PacketCodec<PacketByteBuf, JobAssignmentC2SPacket> {
            override fun decode(buf: PacketByteBuf): JobAssignmentC2SPacket {
                return JobAssignmentC2SPacket(buf.readUuid(), buf.readUuid(), buf.readString())
            }

            override fun encode(buf: PacketByteBuf, packet: JobAssignmentC2SPacket) {
                buf.writeUuid(packet.pastureId)
                buf.writeUuid(packet.pokemonId)
                buf.writeString(packet.jobName)
            }
        }
    }

    override fun getId(): CustomPayload.Id<JobAssignmentC2SPacket> = ID

    fun handle(player: ServerPlayerEntity) {
        val jobType = if (jobName.isEmpty()) null else {
            try { JobType.valueOf(jobName) } catch (_: Exception) { return }
        }
        JobAssignmentManager.setAssignment(pokemonId, jobType)
    }
}
