# Cobbleworkers Plus

[![License: MPL-2.0](https://img.shields.io/badge/License-MPL_2.0-brightgreen.svg?style=flat-square)](https://opensource.org/licenses/MPL-2.0)

> 🔧 Enhanced fork of [Cobbleworkers](https://github.com/Accieo/cobbleworkers) by Accieo

![cobbleworkers-icon](/common/src/main/resources/assets/cobbleworkers/icon.png)

**Cobbleworkers Plus** is an enhanced fork of Cobbleworkers — a companion server-side mod for [Cobblemon](https://cobblemon.com/) that turns the **Pasture Block** into a powerful utility block, giving your Pokémon real jobs.

This fork focuses on improving existing job mechanics, fixing bugs, and adding quality-of-life features that make the mod more fun and reliable.

## ✨ What's Different from the Original?

### 🎣 Fishing Job - Complete Overhaul
- **No more water-standing requirement** — Standing next to water is enough, no submersion needed
- **Active water navigation** — Pokémon walk to water on their own, fish, then return to deposit loot
- **Loot generation fix** — Fixed empty fishing drops caused by wrong origin position
- **3-phase effects** — Working particles while fishing, attack animation + particle burst on catch, cry on success

### 📦 Smarter Chest System
- **Distance-based chest selection** — Each Pokémon goes to its nearest free chest instead of all targeting the same one
- **No more unnecessary item drops** — Retries all chests when they're full instead of dropping items on the ground

### 🎯 Job Assignment GUI
- **Shift+Click** a Pokémon in the Pasture GUI to cycle through available jobs
- **Auto mode** (default) — works all matching jobs like the original
- **Specific job mode** — only performs the assigned job (e.g. Water-type only fishes)
- Shows "Job: Auto" or "Job: Fishing" etc. on each Pokémon slot

### 🛡️ New Job: Guard
- **Wild Pokémon repelling** — Fighting-type Pokémon patrol and chase away wild Pokémon near the Pasture Block
- **XP reward** — Guard earns 50 XP per repel (configurable, half an XS Experience Candy)
- **Level cap aware** — At max level, 20% chance to generate loot instead (Exp Candies, 1% Rare Candy, potions). Full loot table, customizable
- **Attack animations + angry/crit particles** on successful repel

### 📈 Passive XP
- **All pastured Pokémon** slowly gain XP over time (even sleeping/idle ones)
- Default: 125 XP every 60 seconds (~1 level per in-game day at mid-levels)
- Configurable amount and interval, respects level cap

### 😴 Stamina System
- **10 charges** per Pokémon (configurable) — each successful action costs 1 charge
- **Sleep when exhausted** — Pokémon rests for 60 seconds with sleep pose + Zzz particles
- **Wake-up effect** — Cry + particles when stamina is restored
- **Configurable** — toggle on/off, adjust charges and rest duration

### 🎬 Job Effects — All 23 Jobs
- **Attack animations** — Uses Cobblemon's animation fallback chains (e.g. `watergun → bubble → spray → special`) so every Pokémon plays a fitting animation
- **Themed particles** — Splash for fishing, flames for fire, hearts for healing, green sparkles for harvesting, clouds for extinguishing
- **Pokémon cry** on every successful action
- **Per-job toggles** — Enable/disable effects individually or globally via config

> See the full [CHANGELOG.md](CHANGELOG.md) for all technical details.

## 📋 All Available Jobs

| Job | Pokémon Type | Description |
|-----|-------------|-------------|
| 🎣 Fishing | Water | Catches fish and treasure from nearby water |
| 🤿 Diving | Knows "Dive" | Dives for underwater treasure |
| 🌾 Crop Harvesting | Grass | Harvests and replants crops |
| 🫐 Berry Harvesting | Grass | Harvests berries |
| 🌱 Apricorn Harvesting | Bug | Harvests apricorns |
| 💎 Amethyst Harvesting | Rock | Harvests amethyst clusters |
| 🪨 Tumblestone Harvesting | Steel | Harvests and replants tumblestones |
| 🌿 Mint Harvesting | Fairy | Harvests mints |
| 🍯 Honey Collecting | Combee line | Collects and generates honey |
| 🌊 Water Generation | Water | Fills cauldrons with water |
| 🌋 Lava Generation | Fire | Fills cauldrons with lava |
| ❄️ Snow Generation | Ice | Fills cauldrons with powder snow |
| 🔥 Fuel Generation | Fire | Adds fuel to furnaces |
| ⚗️ Brewing Fuel | Dragon | Adds fuel to brewing stands |
| 🧯 Fire Extinguishing | Water | Extinguishes nearby fires |
| 💚 Healing | Chansey line / Healing moves | Gives players regeneration |
| 🎒 Pick-up Loot | Ability: Pickup | Generates random loot |
| 🏺 Archeology | Ground | Generates archeology loot |
| 🗺️ Scouting | Flying | Creates explorer maps |
| 🧹 Item Gathering | Psychic | Picks up items on the ground |
| 🌿 Netherwart Harvesting | Ghost | Harvests and replants netherwart |
| 🛡️ Guard | Fighting | Repels wild Pokémon, earns XP or Exp Candy at level cap |
| 💧 Crop Irrigation | Water | Hydrates farmland |

## ⚙️ Configuration

Each job can be customized via `config/cobbleworkers.json`:
- Enable/disable individual jobs
- Set which Pokémon types can perform each job
- Add specific Pokémon by name
- Adjust cooldowns, search radius, and more
- Toggle job effects (animations, particles, cry) globally or per job — each job has its own effects toggle

Cobbleworkers uses [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) and [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) for easy in-game tweaking on integrated servers.

## 🛠️ Tech Stack

- **Minecraft:** 1.21.1
- **Mod Loader:** Fabric (also supports NeoForge)
- **Language:** Kotlin
- **Dependencies:** Cobblemon 1.7.0+, Fabric API, Fabric Language Kotlin, Cloth Config

## 📦 Building from Source

```bash
git clone https://github.com/notlown/cobbleworkers-plus.git
cd cobbleworkers-plus
./gradlew fabric:build
```

The compiled JAR will be in `fabric/build/libs/`.

> Requires Java 21.

## 🏗️ Project Structure

```
cobbleworkers-plus/
├── common/src/main/kotlin/accieo/cobbleworkers/
│   ├── jobs/              # All job implementations (FishingLootGenerator, DiveLooter, etc.)
│   ├── config/            # Config classes and holders
│   ├── cache/             # Block scanner cache management
│   ├── utilities/         # Navigation, inventory, crop, cauldron utils
│   ├── enums/             # JobType enum
│   ├── interfaces/        # Worker interface
│   └── mixin/             # Pasture block entity mixin
├── fabric/                # Fabric-specific code
├── neoforge/              # NeoForge-specific code
├── CHANGELOG.md           # All changes vs. original
└── README.md
```

## 📜 Credits

- **Original mod:** [Cobbleworkers](https://github.com/Accieo/cobbleworkers) by [Accieo](https://github.com/Accieo)
- **Fork enhancements:** [notlown](https://github.com/notlown)

## 📄 License

Licensed under [MPL-2.0](https://mozilla.org/MPL/2.0/) — same as the original.
