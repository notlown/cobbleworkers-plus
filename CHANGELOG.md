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
- **Effect toggles in each job** — Each job category has its own "Effects (cry, animation, particles)" toggle. Global master toggle in General settings
- **Pokemon lists renamed** — All per-job Pokémon lists consistently named "Additional Pokémon (exceptions)" and moved to the bottom of each category

### 🎣 Fishing Job - Major Overhaul

#### Fixed
- **Fishing now works on platforms near water** — Original mod required Pokémon to be submerged (`isTouchingWater`). Now works standing next to water (1-block radius)
- **Loot table origin fix** — Changed `ORIGIN` from Pasture Block to Pokémon position, fixing empty fishing drops

#### Added
- **Active water navigation** — Pokémon walk to water blocks on their own using the block scanner cache and configurable `searchRadius`
- **Water block validator** — `DeferredBlockScanner` now caches water positions for fishing navigation
- **3-phase fishing effects:**
  1. Working particles (fishing + bubbles) every second while waiting for cooldown
  2. Attack animation (watergun/bubble/spray/special fallback) + particle burst on catch
  3. Cry 1 second after catch, then 2-second pause before walking to chest

### 📦 Chest/Inventory System - Bug Fixes

#### Fixed
- **Chests now sorted by distance to Pokémon** — Original sorted by distance to Pasture Block, causing all Pokémon to target the same nearest chest. Now each Pokémon goes to its own nearest free chest
- **No more unnecessary item drops** — When all nearby chests are full, the mod now clears the "tried" list and retries instead of immediately dropping items on the ground. Items only drop as absolute last resort when zero chests exist

### 🎬 Job Effects System - All 22 Jobs

#### Added
- **`CobbleworkersJobEffects` utility** — Centralized 3-phase effect system:
  - **Phase 1 (Success):** Attack animation using Cobblemon's fallback chain system (e.g. `watergun → bubble → spray → beam → special` for water jobs, `tackle → scratch → pound → physical` for harvest jobs) + themed particle burst
  - **Phase 2 (Working):** Periodic particles while on cooldown as "busy" indicator
  - **Phase 3 (Complete):** Cry sound on successful action
- **Server-side animation packets** — Uses `PlayPosableAnimationPacket` via `CobblemonNetwork` (same as battle system) to send animations from server to clients. Bypasses the client-only check in `cry()` and `playAnimation()`
- **Themed particles per job type:**
  - Fishing: splash + fishing wake + bubble pop
  - Fire jobs: flame + lava + smoke
  - Water/Snow: splash + dripping water
  - Healing: hearts + happy villager
  - Harvest: happy villager + composter
  - Extinguisher: cloud
- **Per-job config toggles** — `effectsEnabled` in each job category + `globalJobEffectsEnabled` master toggle
- **Animation fallback chains** — Only ~8% of Pokémon have battle animations. System sends multiple animation names; Pokémon plays the first one it has. Cry works for ~81% of Pokémon. Particles work for all

### 🛡️ New Job: Guard

#### Added
- **Guard job** — Fighting-type Pokémon patrol the area around the Pasture Block, chase and repel wild Pokémon
- **XP reward** — Guard earns 50 XP per wild Pokémon repelled (half an XS Experience Candy, configurable)
- **Level cap respect** — When a Pokémon is at max level (`canLevelUpFurther() == false`), it has a 20% chance (configurable) to generate loot from the `guard_loot` loot table instead of gaining XP. Loot is deposited into the nearest chest
- **Guard loot table** (`data/cobbleworkers/loot_table/guard_loot.json`):
  - XS Exp Candy: 40% (1-2x)
  - S Exp Candy: 25%
  - M Exp Candy: 15%
  - L Exp Candy: 8%
  - XL Exp Candy: 3%
  - Rare Candy: 1%
  - Bonus pool (15% chance): Revive, Potion, Super Potion, Ether
- **Config options:** enabled, cooldown (120s default), patrol radius (10 blocks), XP per repel, candy drop chance (%), Pokémon type (FIGHTING default)
- **Custom ExperienceSource** (`GuardExperienceSource`) marked as sidemod for compatibility
- **Effects:** Angry villager particles while patrolling, attack animation + crit particles + smoke on repel

### 🎯 Job Assignment GUI

#### Added
- **Job assignment per Pokémon** — Shift+Click a Pokémon in the Pasture GUI to cycle through available jobs
- **"Job: Auto" label** on each Pokémon slot showing current assignment
- **"[Shift+Click to change]" hint** when hovering a slot
- **Auto mode** (default) — Pokémon works all matching jobs like the original
- **Specific job mode** — Pokémon only performs the assigned job (e.g. a Water-type only fishes, doesn't also extinguish fires)
- **Network packet** (`JobAssignmentC2SPacket`) — Client sends job selection to server
- **WorkerDispatcher integration** — Jobs check `JobAssignmentManager.isJobAllowed()` before running
- **Internal job types** (Generic, CauldronGenerator) automatically skipped in the cycle

### 📈 Passive XP

#### Added
- **Passive XP** — All Pokémon in the Pasture slowly gain XP over time, even while sleeping or idle
- Default: **125 XP every 60 seconds** (~1 level per in-game day at mid-levels)
- Respects level cap (`canLevelUpFurther()`)
- **Config:** `passiveXpEnabled`, `passiveXpAmount` (1-100), `passiveXpIntervalSeconds`

### 😴 Stamina System

#### Added
- **Stamina charges** — Each Pokémon has 10 charges (configurable). Each successful job action costs 1 charge
- **Rest phase** — At 0 charges, Pokémon stops working and goes to sleep for 60 seconds (configurable)
- **Sleep visualization:**
  - Sleep animation/pose on the Pokémon
  - Note + cloud particles every 1.5 seconds (Zzz effect)
- **Wake-up effect** — Cry + happy villager particle burst when stamina is restored
- **Config:** `staminaEnabled`, `staminaCharges` (1-50), `restDurationSeconds` in General settings
- **All jobs affected** — Stamina is checked in WorkerDispatcher before any job tick runs

### 📝 Files Changed

| File | Change |
|------|--------|
| `FishingLootGenerator.kt` | Complete overhaul: near-water detection, active navigation, 3-phase effects, loot origin fix |
| `CobbleworkersJobEffects.kt` | New: centralized effect system with fallback animations, themed particles, server-side packets |
| `CobbleworkersInventoryUtils.kt` | Fixed chest sorting (by Pokemon distance), retry logic instead of dropping items |
| `CobbleworkersConfig.kt` | Added `effectsEnabled` per job, `globalJobEffectsEnabled`, reordered fields |
| `ApricornHarvester.kt` | Migrated to 3-phase effect system (attack + cry on harvest) |
| `CobbleworkersPassiveXp.kt` | New: passive XP system for all pastured Pokemon |
| `Guard.kt` | New: Guard job with wild Pokemon repelling and XP rewards |
| `CobbleworkersStamina.kt` | New: Stamina system with charges, rest phase, sleep visualization |
| `WorkerDispatcher.kt` | Added Guard to worker registry, stamina check before job ticks |
| All other 20 job files | Added legacy effect calls on success |
| `en_us.json` | Complete rewrite: clean labels, consistent naming, all new config entries |
| `fabric.mod.json` | Renamed to Cobbleworkers Plus, updated metadata |
| `README.md` | Full fork documentation |
| `CHANGELOG.md` | All changes vs. original |
