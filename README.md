# Cobbleworkers Plus

[![License: MPL-2.0](https://img.shields.io/badge/License-MPL_2.0-brightgreen.svg?style=flat-square)](https://opensource.org/licenses/MPL-2.0)

> 🔧 Enhanced fork of [Cobbleworkers](https://github.com/Accieo/cobbleworkers) by Accieo

![cobbleworkers-icon](/common/src/main/resources/assets/cobbleworkers/icon.png)

**Cobbleworkers Plus** is an enhanced fork of Cobbleworkers — a companion server-side mod for [Cobblemon](https://cobblemon.com/) that turns the **Pasture Block** into a powerful utility block, giving your Pokémon real jobs.

This fork focuses on improving existing job mechanics, fixing bugs, and adding quality-of-life features that make the mod more fun and reliable.

## ✨ What's Different from the Original?

### 🎣 Fishing Job - Complete Overhaul
- **No more water-standing requirement** — Pokémon no longer need to be submerged in water. Standing next to water is enough.
- **Active water navigation** — Pokémon actively walk to nearby water blocks to fish, then return to a chest to deposit loot. Uses the configurable `searchRadius` (default: 10 blocks).
- **Loot generation fix** — Fixed an issue where the fishing loot table returned empty results because the origin position was set to the Pasture Block instead of the Pokémon's position near water.
- **Catch effects** — Pokémon now play their cry and spawn splash/fishing/bubble particles on a successful catch.

### 🎬 Job Effects System — All 22 Jobs
- **Attack animations** — Pokémon play `physical` (harvest jobs) or `special` (generation jobs) attack animations on success
- **Themed particles** — Each job type has unique particles: splash for fishing, flames for fire, hearts for healing, clouds for extinguishing, etc.
- **Pokémon cry** on every successful action
- **Per-job toggles** — Enable/disable effects for each job individually via config, plus a global master toggle

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
