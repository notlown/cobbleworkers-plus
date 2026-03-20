# Changelog - Cobbleworkers Plus

All changes compared to the original [Cobbleworkers](https://github.com/Accieo/cobbleworkers) by Accieo.

## [1.8.0-plus.1] - 2026-03-20

### 🏷️ Branding & UI

#### Changed
- **Mod name** — Renamed to "Cobbleworkers Plus" in Mod Menu and config screen
- **Config title** — Shows "Cobbleworkers Plus config" instead of "Cobbleworkers config"
- **Mod metadata** — Updated authors (notlown + Accieo), description, and links to fork repo

#### Fixed
- **Config labels** — All config options now show clean, readable names instead of raw translation keys
- **Category names** — Renamed categories to be cleaner: "Fishing" instead of "Fishing loot", "Pick-up" instead of "Pick-up loot", "Furnace Fuel" instead of "Fuel generation", etc.
- **Effect toggles moved into jobs** — Each job category now has its own "Effects (cry, animation, particles)" toggle instead of a separate effects section. Global toggle remains in General settings

### 🎣 Fishing Job - Major Overhaul

#### Fixed
- **Fishing now works on platforms near water** — Original mod required Pokémon to be submerged in water (`isTouchingWater`). Pokémon can now fish while standing next to water blocks (1-block radius detection).
- **Loot table origin fix** — Changed the loot context `ORIGIN` parameter from the Pasture Block position to the Pokémon's actual position. This fixes the issue where the Minecraft fishing loot table returned empty drops because the origin was on land instead of near water.

#### Added
- **Active water navigation** — Pokémon now actively search for and walk to nearby water blocks using the block scanner cache, instead of passively waiting to be placed next to water. Uses the configurable `searchRadius` (default: 10 blocks) to find water.
- **Water block validator** — Added a `blockValidator` to the FishingLootGenerator so the `DeferredBlockScanner` caches water block positions. Pokémon navigate to these cached positions to fish.
- **Catch effects** — When a Pokémon successfully catches something:
  - Pokémon plays its `cry()` animation
  - Water splash particles (`SPLASH`) spawn at the nearest water block
  - Fishing wake particles (`FISHING`) appear on the water surface
  - Bubble particles (`BUBBLE`) rise from underwater

### 🎬 Job Effects System - All Jobs

#### Added
- **New `CobbleworkersJobEffects` utility** — Centralized effect system for all 22 jobs
- **Attack animations on success:**
  - Harvest jobs (Apricorn, Berry, Crop, Mint, Amethyst, Tumblestone, Netherwart, Honey, Irrigation, Ground Items): `physical` attack animation + green sparkle particles
  - Generation jobs (Archeology, Diving, Pick-up, Scouts): `special` attack animation + green sparkle particles
  - Fishing: `special` attack animation + water splash/fishing/bubble particles
  - Fire jobs (Lava, Fuel, Brewing Stand Fuel): `special` attack animation + flame particles
  - Water/Snow generation: `special` attack animation + splash/drip particles
  - Healing: `special` attack animation + heart particles
  - Fire Extinguisher: `special` attack animation + cloud particles
- **Per-job config toggles** — New `jobEffects` config group with:
  - `globalEffectsEnabled` — Master toggle for all effects
  - Individual toggle per job (e.g. `fishingEffects`, `healingEffects`, etc.)
- **Pokémon cry** plays on every successful job action (respects toggle)

### 📝 Files Changed

| File | Change |
|------|--------|
| `FishingLootGenerator.kt` | Complete overhaul: near-water detection, active water navigation, loot origin fix, catch effects |
| `CobbleworkersJobEffects.kt` | New file: centralized effect system with per-job themed effects |
| `CobbleworkersConfig.kt` | Added `JobEffectsGroup` with global + per-job effect toggles |
| All 22 job files | Added effect calls on successful job completion |
| `README.md` | Full fork documentation |
| `CHANGELOG.md` | All changes vs. original |
