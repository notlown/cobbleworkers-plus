/*
 * Copyright (C) 2025 Accieo
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package accieo.cobbleworkers.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "cobbleworkers")
class CobbleworkersConfig : ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    var general = GeneralGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var apricorn = ApricornGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var irrigation = IrrigationGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var amethyst = AmethystGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var tumblestone = TumblestoneGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var cropHarvest = CropHarvestGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var berries = BerriesGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var honey = HoneyGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var mints = MintsGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var lava = LavaGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var water = WaterGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var snow = SnowGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var fishing = FishingGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var pickup = PickUpGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var diving = DivingGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var groundItemGathering = GroundItemGathererGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var netherwartHarvest = NetherwartGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var healing = HealingGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var fuel = FuelGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var brewingStandFuel = BrewingStandFuelGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var extinguisher = ExtinguisherGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var archeology = ArcheologyGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var scouts = ScoutGroup()

    @ConfigEntry.Gui.CollapsibleObject
    var guard = GuardGroup()

    class GeneralGroup {
        @ConfigEntry.BoundedDiscrete(min = 10, max = 30)
        var blocksScannedPerTick = 15
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        var searchRadius = 8
        @ConfigEntry.BoundedDiscrete(min = 1, max = 8)
        var searchHeight = 5
        var globalJobEffectsEnabled = true
        var staminaEnabled = false
        @ConfigEntry.BoundedDiscrete(min = 1, max = 50)
        var staminaCharges = 10
        var restDurationSeconds: Long = 60
    }

    class ApricornGroup {
        var apricornHarvestersEnabled = true
        var effectsEnabled = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsApricorns: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.BUG
        var apricornHarvesters: MutableList<String> = mutableListOf("ditto")
    }

    class IrrigationGroup {
        var cropIrrigatorsEnabled = true
        var effectsEnabled = true
        var irrigationRadius = 1
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeIrrigatesCrops: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.WATER
        var cropIrrigators: MutableList<String> = mutableListOf("ditto")
    }

    class AmethystGroup {
        var amethystHarvestersEnabled = true
        var effectsEnabled = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsAmethyst: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.ROCK
        var amethystHarvesters: MutableList<String> = mutableListOf("ditto")
    }

    class TumblestoneGroup {
        var tumblestoneHarvestersEnabled = true
        var effectsEnabled = true
        var shouldReplantTumblestone = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsTumblestone: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.STEEL
        var tumblestoneHarvesters: MutableList<String> = mutableListOf("ditto")
    }

    class CropHarvestGroup {
        var cropHarvestersEnabled = true
        var effectsEnabled = true
        var shouldReplantCrops = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsCrops: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.GRASS
        var cropHarvesters: MutableList<String> = mutableListOf("ditto")
    }

    class BerriesGroup {
        var berryHarvestersEnabled = true
        var effectsEnabled = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsBerries: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.GRASS
        var berryHarvesters: MutableList<String> = mutableListOf("ditto")
    }

    class HoneyGroup {
        var honeyCollectorsEnabled = true
        var effectsEnabled = true
        var combeeLineCollectsHoney = true
        var combeeLineGeneratesHoney = true
        var honeyGenerationCooldownSeconds: Long = 120
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsHoney: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.NONE
        var honeyCollectors: MutableList<String> = mutableListOf("ditto")
    }

    class MintsGroup {
        var mintHarvestersEnabled = true
        var effectsEnabled = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsMints: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.FAIRY
        var mintHarvesters: MutableList<String> = mutableListOf("ditto")
    }

    class LavaGroup {
        var lavaGeneratorsEnabled = true
        var effectsEnabled = true
        var lavaGenerationCooldownSeconds: Long = 90
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGeneratesLava: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.FIRE
        var lavaGenerators: MutableList<String> = mutableListOf("ditto")
    }

    class WaterGroup {
        var waterGeneratorsEnabled = true
        var effectsEnabled = true
        var waterGenerationCooldownSeconds: Long = 90
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGeneratesWater: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.WATER
        var waterGenerators: MutableList<String> = mutableListOf("ditto")
    }

    class SnowGroup {
        var snowGeneratorsEnabled = true
        var effectsEnabled = true
        var snowGenerationCooldownSeconds: Long = 90
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGeneratesSnow: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.ICE
        var snowGenerators: MutableList<String> = mutableListOf("ditto")
    }

    class FishingGroup {
        var fishingLootGeneratorsEnabled = true
        var effectsEnabled = true
        var fishingLootGenerationCooldownSeconds: Long = 60
        var fishingLootTreasureChance = 1
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGeneratesFishingLoot: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.WATER
        var fishingLootGenerators: MutableList<String> = mutableListOf("ditto")
    }

    class PickUpGroup {
        var pickUpLootersEnabled = true
        var effectsEnabled = true
        var pickUpLootingCooldownSeconds: Long = 120
        var lootTables: MutableList<String> = mutableListOf(
            "cobblemon:sets/any_ancient_held_item",
            "cobblemon:sets/any_common_pokeball",
            "cobblemon:sets/any_evo_stone",
            "cobblemon:sets/any_exp_candy",
            "cobblemon:sets/any_natural_heal_item",
            "cobblemon:sets/any_type_gem",
            "cobblemon:sets/any_apricorn_seed",
            "cobblemon:villages/village_pokecenters",
        )
    }

    class DivingGroup {
        var divingLootersEnabled = true
        var effectsEnabled = true
        var divingLootingCooldownSeconds: Long = 210
        var lootTables: MutableList<String> = mutableListOf(
            "cobbleworkers:dive_treasure",
        )
    }

    class GroundItemGathererGroup {
        var groundItemGatheringEnabled = true
        var effectsEnabled = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGathersGroundItems: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.PSYCHIC
        var groundItemGatherers: MutableList<String> = mutableListOf("ditto")
    }

    class NetherwartGroup {
        var netherwartHarvestersEnabled = true
        var effectsEnabled = true
        var shouldReplantNetherwart = true
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeHarvestsNetherwart: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.GHOST
        var netherwartHarvesters: MutableList<String> = mutableListOf("ditto")
    }

    class HealingGroup {
        var healersEnabled = true
        var effectsEnabled = true
        var healingMoves: MutableList<String> = mutableListOf(
            "wish", "softboiled", "moonlight", "recover", "roost",
            "healbell", "aromatherapy", "synthesis", "rest", "lifedew"
        )
        var chanseyLineHealsPlayers = true
        var regenDurationSeconds = 20
        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        var regenAmplifier = 0
        var healers: MutableList<String> = mutableListOf("ditto")
    }

    class FuelGroup {
        var fuelGeneratorsEnabled = true
        var effectsEnabled = true
        var fuelGenerationCooldownSeconds: Long = 80
        var burnTimeSeconds = 80
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGeneratesFuel: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.FIRE
        var fuelGenerators: MutableList<String> = mutableListOf("ditto")
    }

    class BrewingStandFuelGroup {
        var fuelGeneratorsEnabled = true
        var effectsEnabled = true
        var fuelGenerationCooldownSeconds: Long = 80
        @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
        var addedFuel = 5
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGeneratesFuel: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.DRAGON
        var fuelGenerators: MutableList<String> = mutableListOf("ditto")
    }

    class ExtinguisherGroup {
        var extinguishersEnabled = true
        var effectsEnabled = true
        var extinguishingRadius = 1
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeExtinguishesFire: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.WATER
        var extinguishers: MutableList<String> = mutableListOf("ditto")
    }

    class ArcheologyGroup {
        var archeologistsEnabled = true
        var effectsEnabled = true
        var archeologyLootingCooldownSeconds: Long = 80
        var lootTables: MutableList<String> = mutableListOf(
            "cobbleworkers:archeology_treasure",
        )
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeDoesArcheology: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.GROUND
        var archeologists: MutableList<String> = mutableListOf("ditto")
    }

    class ScoutGroup {
        var scoutsEnabled = true
        var effectsEnabled = true
        var scoutGenerationCooldownSeconds: Long = 80
        var useAllStructures = false
        var mapNameIsHidden = true
        var structureTags: MutableList<String> = mutableListOf(
            "cobblemon:fishing_boat", "cobblemon:fossil", "cobblemon:gimmi_tower",
            "cobblemon:ruin", "cobblemon:shipwreck_cove", "minecraft:mineshaft",
            "minecraft:ocean_ruin", "minecraft:on_ocean_explorer_maps",
            "minecraft:on_treasure_maps", "minecraft:on_trial_chambers_maps",
            "minecraft:on_woodland_explorer_maps", "minecraft:ruined_portal",
            "minecraft:shipwreck",
        )
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeScouts: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.FLYING
        var scouts: MutableList<String> = mutableListOf("ditto")
    }

    class GuardGroup {
        var guardsEnabled = true
        var effectsEnabled = true
        var guardCooldownSeconds: Long = 30
        @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
        var guardRadius = 10
        @ConfigEntry.BoundedDiscrete(min = 10, max = 500)
        var xpPerRepel = 50
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        var typeGuards: CobbleworkersConfigPokemonType = CobbleworkersConfigPokemonType.FIGHTING
        var guards: MutableList<String> = mutableListOf("ditto")
    }
}
