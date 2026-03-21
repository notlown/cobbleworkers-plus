/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.fabric.mixin;

import accieo.cobbleworkers.fabric.client.gui.JobAssignmentScreen;
import com.cobblemon.mod.common.client.gui.pasture.PasturePCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pasture.PasturePokemonScrollList;
import com.cobblemon.mod.common.client.gui.pasture.PastureWidget;
import com.cobblemon.mod.common.client.gui.pasture.RecallButton;
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

/**
 * Adds a "Jobs" button to the PastureWidget that opens the Job Assignment Screen.
 */
@Mixin(PastureWidget.class)
public abstract class PastureWidgetMixin {

    @Shadow @Final private PasturePCGUIConfiguration pasturePCGUIConfiguration;
    @Shadow @Final private PasturePokemonScrollList pastureScrollList;

    @Unique
    private ButtonWidget cobbleworkers$jobsButton;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cobbleworkers$onInit(
        com.cobblemon.mod.common.client.gui.pc.StorageWidget storageWidget,
        PasturePCGUIConfiguration config,
        int x, int y,
        CallbackInfo ci
    ) {
        // Create "Jobs" button below the pasture widget
        // Position above the pasture widget
        cobbleworkers$jobsButton = ButtonWidget.builder(Text.literal("\u00A7bJobs"), btn -> {
            cobbleworkers$openJobScreen();
        }).dimensions(x + 2, y - 18, 78, 16).build();
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void cobbleworkers$onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (cobbleworkers$jobsButton != null) {
            cobbleworkers$jobsButton.render(context, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void cobbleworkers$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cobbleworkers$jobsButton != null && cobbleworkers$jobsButton.isMouseOver(mouseX, mouseY)) {
            cobbleworkers$openJobScreen();
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void cobbleworkers$openJobScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        UUID pastureId = pasturePCGUIConfiguration.getPastureId();
        List<OpenPasturePacket.PasturePokemonDataDTO> pokemonList =
            pasturePCGUIConfiguration.getPasturedPokemon().get();

        client.setScreen(new JobAssignmentScreen(pastureId, pokemonList, client.currentScreen));
    }
}
