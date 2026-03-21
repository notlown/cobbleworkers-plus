/*
 * Copyright (C) 2026 notlown (Cobbleworkers Plus)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.fabric.client.gui

import accieo.cobbleworkers.enums.JobType
import accieo.cobbleworkers.jobs.JobAssignmentManager
import accieo.cobbleworkers.net.JobAssignmentC2SPacket
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket.PasturePokemonDataDTO
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import java.util.UUID

class JobAssignmentScreen(
    private val pastureId: UUID,
    private val pokemonList: List<PasturePokemonDataDTO>,
    private val parentScreen: Screen?
) : Screen(Text.literal("Job Assignment")) {

    private val ROW_HEIGHT = 22
    private val HEADER_Y = 8
    private val LIST_START_Y = 28
    private val POKEMON_COL_WIDTH = 85
    private val BTN_WIDTH = 58
    private val BTN_HEIGHT = 16
    private val BTN_SPACING = 2

    private var scrollX = 0
    private var scrollY = 0
    private val MAX_SCROLL_SPEED = 12

    private val selectedJobs = mutableMapOf<UUID, JobType?>()

    // Store button data for manual rendering (not using addDrawableChild for job buttons)
    private data class JobButton(
        val pokemonId: UUID,
        val jobType: JobType?, // null = Auto
        var x: Int,
        var y: Int,
        var selected: Boolean
    )

    private val allJobButtons = mutableListOf<JobButton>()
    private var backButton: ButtonWidget? = null

    companion object {
        val VALID_JOBS = JobType.entries.filter {
            it != JobType.Generic && it != JobType.CauldronGenerator
        }

        fun getShortName(jobType: JobType): String {
            return when (jobType) {
                JobType.ApricornHarvester -> "Apricorn"
                JobType.AmethystHarvester -> "Amethyst"
                JobType.Archeologist -> "Archeology"
                JobType.BerryHarvester -> "Berry"
                JobType.BrewingStandFuelGenerator -> "Brew Fuel"
                JobType.CropHarvester -> "Crops"
                JobType.CropIrrigator -> "Irrigate"
                JobType.DiveLooter -> "Diving"
                JobType.FireExtinguisher -> "Extinguish"
                JobType.FishingLootGenerator -> "Fishing"
                JobType.FuelGenerator -> "Fuel"
                JobType.GroundItemGatherer -> "Gather"
                JobType.Healer -> "Healing"
                JobType.HoneyCollector -> "Honey"
                JobType.MintHarvester -> "Mints"
                JobType.NetherwartHarvester -> "Netherwart"
                JobType.PickUpLooter -> "Pick-up"
                JobType.Scout -> "Scouting"
                JobType.TumblestoneHarvester -> "Tumble"
                JobType.CauldronGenerator -> "Cauldron"
                else -> jobType.name.take(8)
            }
        }
    }

    override fun init() {
        super.init()

        allJobButtons.clear()

        backButton = ButtonWidget.builder(Text.literal("< Back")) { close() }
            .dimensions(4, height - 18, 50, 16)
            .build()
        addDrawableChild(backButton)

        // Build button data for each Pokemon
        pokemonList.forEachIndexed { index, pokemonData ->
            val pokemonId = pokemonData.pokemonId
            val currentAssignment = JobAssignmentManager.getAssignment(pokemonId)
            selectedJobs[pokemonId] = currentAssignment

            val baseY = LIST_START_Y + index * ROW_HEIGHT
            var btnX = POKEMON_COL_WIDTH + 4

            // Auto button
            allJobButtons.add(JobButton(pokemonId, null, btnX, baseY, currentAssignment == null))
            btnX += BTN_WIDTH + BTN_SPACING

            // Job buttons
            VALID_JOBS.forEach { jobType ->
                allJobButtons.add(JobButton(pokemonId, jobType, btnX, baseY, currentAssignment == jobType))
                btnX += BTN_WIDTH + BTN_SPACING
            }
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)

        // Title (fixed, doesn't scroll)
        context.drawCenteredTextWithShadow(textRenderer, "\u00A7l\u00A7eJob Assignment", width / 2, HEADER_Y, 0xFFFFFF)
        context.drawCenteredTextWithShadow(textRenderer, "\u00A77Scroll: Mouse Wheel | Shift+Scroll: Horizontal", width / 2, HEADER_Y + 12, 0x888888)

        // Enable scissor to clip scrollable content
        context.enableScissor(0, LIST_START_Y - 2, width, height - 22)

        // Pokemon names (scroll vertically only)
        pokemonList.forEachIndexed { index, pokemonData ->
            val y = LIST_START_Y + index * ROW_HEIGHT + scrollY
            if (y < LIST_START_Y - ROW_HEIGHT || y > height) return@forEachIndexed

            val name = pokemonData.displayName.string
            context.drawTextWithShadow(textRenderer, name, 4, y + 2, 0xFFFFFF)
            context.drawTextWithShadow(textRenderer, "\u00A77Lv.${pokemonData.level}", 4, y + 12, 0xAAAAAA)
        }

        // Render job buttons (scroll both directions)
        for (btn in allJobButtons) {
            val renderX = btn.x + scrollX
            val renderY = btn.y + scrollY

            if (renderY < LIST_START_Y - ROW_HEIGHT || renderY > height - 22) continue
            if (renderX + BTN_WIDTH < POKEMON_COL_WIDTH || renderX > width) continue

            val hovered = mouseX >= renderX && mouseX <= renderX + BTN_WIDTH &&
                          mouseY >= renderY && mouseY <= renderY + BTN_HEIGHT

            // Background
            val bgColor = when {
                btn.selected -> 0xFF2D7D2D.toInt() // green
                hovered -> 0xFF555555.toInt()       // hover gray
                else -> 0xFF333333.toInt()          // dark gray
            }
            context.fill(renderX, renderY, renderX + BTN_WIDTH, renderY + BTN_HEIGHT, bgColor)

            // Border
            val borderColor = if (btn.selected) 0xFF44CC44.toInt() else 0xFF666666.toInt()
            context.drawHorizontalLine(renderX, renderX + BTN_WIDTH - 1, renderY, borderColor)
            context.drawHorizontalLine(renderX, renderX + BTN_WIDTH - 1, renderY + BTN_HEIGHT - 1, borderColor)
            context.drawVerticalLine(renderX, renderY, renderY + BTN_HEIGHT - 1, borderColor)
            context.drawVerticalLine(renderX + BTN_WIDTH - 1, renderY, renderY + BTN_HEIGHT - 1, borderColor)

            // Text
            val name = if (btn.jobType == null) "Auto" else getShortName(btn.jobType)
            val textColor = if (btn.selected) 0xFFFFFF else 0xAAAAAA
            val textX = renderX + (BTN_WIDTH - textRenderer.getWidth(name)) / 2
            context.drawTextWithShadow(textRenderer, name, textX, renderY + 4, textColor)
        }

        context.disableScissor()

        // Render back button on top
        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (super.mouseClicked(mouseX, mouseY, button)) return true

        // Check job button clicks
        for (btn in allJobButtons) {
            val renderX = btn.x + scrollX
            val renderY = btn.y + scrollY

            if (mouseX >= renderX && mouseX <= renderX + BTN_WIDTH &&
                mouseY >= renderY && mouseY <= renderY + BTN_HEIGHT &&
                mouseY >= LIST_START_Y && mouseY < height - 22) {
                selectJob(btn.pokemonId, btn.jobType)
                return true
            }
        }
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (hasShiftDown()) {
            // Horizontal scroll
            scrollX += (verticalAmount * MAX_SCROLL_SPEED).toInt()
            scrollX = scrollX.coerceAtMost(0) // can't scroll right past start
        } else {
            // Vertical scroll
            scrollY += (verticalAmount * MAX_SCROLL_SPEED).toInt()
            scrollY = scrollY.coerceAtMost(0) // can't scroll down past start
        }
        return true
    }

    private fun selectJob(pokemonId: UUID, jobType: JobType?) {
        selectedJobs[pokemonId] = jobType

        // Update button states
        for (btn in allJobButtons) {
            if (btn.pokemonId == pokemonId) {
                btn.selected = btn.jobType == jobType
            }
        }

        // Send to server
        ClientPlayNetworking.send(JobAssignmentC2SPacket(pastureId, pokemonId, jobType?.name ?: ""))
    }

    override fun close() {
        client?.setScreen(parentScreen)
    }

    override fun shouldPause(): Boolean = false
}
