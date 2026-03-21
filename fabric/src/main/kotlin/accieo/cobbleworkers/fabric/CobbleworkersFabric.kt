/*
 * Copyright (C) 2025 Accieo
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.fabric

import accieo.cobbleworkers.Cobbleworkers
import accieo.cobbleworkers.fabric.integration.FabricIntegrationHelper
import accieo.cobbleworkers.integration.CobbleworkersIntegrationHandler
import accieo.cobbleworkers.net.JobAssignmentC2SPacket
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object CobbleworkersFabric : ModInitializer {
    override fun onInitialize() {
        Cobbleworkers.init()

        // Register C2S packet for job assignments
        PayloadTypeRegistry.playC2S().register(JobAssignmentC2SPacket.ID, JobAssignmentC2SPacket.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(JobAssignmentC2SPacket.ID) { packet, context ->
            context.server().execute {
                packet.handle(context.player())
            }
        }

        ServerLifecycleEvents.SERVER_STARTING.register { _ ->
            val integrationHandler = CobbleworkersIntegrationHandler(FabricIntegrationHelper)
            integrationHandler.addIntegrations()
        }
    }
}
