/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.fabric.mixin;

import accieo.cobbleworkers.enums.JobType;
import accieo.cobbleworkers.jobs.JobAssignmentManager;
import accieo.cobbleworkers.net.JobAssignmentC2SPacket;
import com.cobblemon.mod.common.client.gui.pasture.PasturePCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pasture.PasturePokemonScrollList;
import com.cobblemon.mod.common.client.gui.pasture.PastureWidget;
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Adds a clickable job assignment label to each Pokemon slot in the Pasture GUI.
 * Shift+Click on a Pokemon slot to cycle through available jobs.
 */
@Mixin(PasturePokemonScrollList.PastureSlot.class)
public abstract class PastureSlotMixin {

    @Shadow @Final private OpenPasturePacket.PasturePokemonDataDTO pokemon;
    @Shadow @Final private PastureWidget parent;

    @Unique
    private String cobbleworkers$currentJobName = "Auto";

    @Unique
    private int cobbleworkers$jobIndex = -1;

    @Unique
    private static final JobType[] cobbleworkers$ALL_JOBS = JobType.values();

    /**
     * Render the job label at the bottom of each Pokemon slot.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void cobbleworkers$onRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        String label = "\u00A77Job: \u00A7e" + cobbleworkers$currentJobName;
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        int labelWidth = textRenderer.getWidth(label);
        // Draw at bottom-left of the slot
        context.drawText(textRenderer, label, x + 4, y + entryHeight - 10, 0xFFFFFF, true);

        // Draw hint if hovered
        if (hovered) {
            context.drawText(textRenderer, "\u00A78[Shift+Click to change]", x + 4, y + entryHeight - 20, 0xFFFFFF, false);
        }
    }

    /**
     * Intercept mouse clicks - Shift+Click cycles the job.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void cobbleworkers$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == 0 && MinecraftClient.getInstance().options.sneakKey.isPressed()) {
            cobbleworkers$cycleJob();
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void cobbleworkers$cycleJob() {
        cobbleworkers$jobIndex++;

        if (cobbleworkers$jobIndex >= cobbleworkers$ALL_JOBS.length) {
            cobbleworkers$jobIndex = -1;
        }

        // Skip internal types
        while (cobbleworkers$jobIndex >= 0 &&
               (cobbleworkers$ALL_JOBS[cobbleworkers$jobIndex] == JobType.Generic ||
                cobbleworkers$ALL_JOBS[cobbleworkers$jobIndex] == JobType.CauldronGenerator)) {
            cobbleworkers$jobIndex++;
            if (cobbleworkers$jobIndex >= cobbleworkers$ALL_JOBS.length) {
                cobbleworkers$jobIndex = -1;
                break;
            }
        }

        if (cobbleworkers$jobIndex == -1) {
            cobbleworkers$currentJobName = "Auto";
            cobbleworkers$sendAssignment("");
        } else {
            JobType selected = cobbleworkers$ALL_JOBS[cobbleworkers$jobIndex];
            cobbleworkers$currentJobName = JobAssignmentManager.INSTANCE.getJobDisplayName(selected);
            cobbleworkers$sendAssignment(selected.name());
        }
    }

    @Unique
    private void cobbleworkers$sendAssignment(String jobName) {
        try {
            UUID pokemonId = pokemon.getPokemonId();
            PasturePCGUIConfiguration config = parent.getPasturePCGUIConfiguration();
            UUID pastureId = config.getPastureId();

            JobAssignmentC2SPacket packet = new JobAssignmentC2SPacket(pastureId, pokemonId, jobName);
            ClientPlayNetworking.send(packet);
        } catch (Exception e) {
            // Silently fail
        }
    }
}
