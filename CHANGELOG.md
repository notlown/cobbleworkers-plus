# Changelog - Cobbleworkers Plus

All changes compared to the original [Cobbleworkers](https://github.com/Accieo/cobbleworkers) by Accieo.

## [1.8.0-plus.1] - 2026-03-20

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

### 📝 Files Changed

| File | Change |
|------|--------|
| `FishingLootGenerator.kt` | Complete overhaul: near-water detection, active water navigation, loot origin fix, catch effects |
| `README.md` | New documentation for the fork with all jobs listed, build instructions, and project structure |
| `CHANGELOG.md` | New file tracking all changes vs. original |
