<div align="center">

# AllayNPC

![AllayMC](https://img.shields.io/badge/AllayMC-0.23.0-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**A powerful NPC plugin for AllayMC** 🎮

</div>

## ✨ Features

- 🎭 **Custom Skins** - Support for custom player skins and 4D skins
- 👀 **Look at Player** - NPCs automatically look at each player individually (per-player view)
- 🎬 **Emote Support** - NPCs can play emotes at configurable intervals
- ⚔️ **Equipment System** - Full armor and held item support
- 💬 **Dialog System** - Create interactive dialogs with buttons
- 🎯 **Click Actions** - Execute commands, send messages, or open dialogs on click
- 🌍 **Multi-language** - Built-in i18n support (English & Chinese)
- 📝 **In-game Editor** - Create and edit NPCs with intuitive forms
- 🔄 **Hot Reload** - Reload configurations without server restart
- 🎨 **PlaceholderAPI** - Full placeholder support in messages

## 📦 Installation

1. Download the latest release from [Releases](https://github.com/smartcmd/AllayNPC/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart the server
4. Done! 🎉

## 🎮 Commands

| Command | Description |
|---------|-------------|
| `/anpc create <name>` | Create a new NPC |
| `/anpc edit <name>` | Edit an existing NPC |
| `/anpc delete <name>` | Delete an NPC |
| `/anpc list` | List all NPCs |
| `/anpc tp <name>` | Teleport to an NPC |
| `/anpc spawn <name>` | Spawn an NPC |
| `/anpc remove <name>` | Remove NPC entity (keeps config) |
| `/anpc skins` | List available skins |
| `/anpc reload` | Reload configuration |
| `/anpc help` | Show help |

**Alias:** `/npc`

## 🔐 Permissions

| Permission | Description |
|------------|-------------|
| `allaynpc.command` | Access to all NPC commands |

## 📁 Directory Structure

```
plugins/AllayNPC/
├── 📂 skins/              # Custom skin files
│   ├── steve.png          # Single PNG file
│   ├── alex_slim.png      # Slim arm skin (add _slim suffix)
│   └── 📂 custom4d/       # 4D skin folder
│       ├── skin.png       # or skin_slim.png
│       └── skin.json      # Geometry data (optional)
├── 📂 npcs/               # NPC configurations (.yml)
│   └── shopkeeper.yml
├── 📂 dialogs/            # Dialog configurations (.yml)
│   └── welcome.yml
└── 📂 lang/               # Language files
```

## 🎭 Skin Setup

AllayNPC supports two ways to add custom skins:

### Method 1: Single PNG File
1. Place your skin PNG file in `plugins/AllayNPC/skins/`
2. For slim arm skins, add `_slim` to the filename (e.g., `alex_slim.png`)
3. The skin name will be the filename without extension

### Method 2: Skin Folder (for 4D skins)
1. Create a folder in `plugins/AllayNPC/skins/` with your skin name
2. Add `skin.png` (or `skin_slim.png` for slim arms) inside the folder
3. For 4D skins, add `skin.json` with geometry data

### Supported Formats
- Standard skins: 64x64 or 64x32 pixels
- 4D skins: Various sizes with custom geometry

After adding skins, use `/anpc reload` to load them.

## 📝 NPC Configuration

Example NPC configuration (`npcs/shopkeeper.yml`):

```yaml
# Display name shown above NPC (supports color codes with &)
display_name: "&6Shop Keeper"

# Whether to always show the name tag
always_show_name: true

# Skin name (filename without extension, or folder name)
skin: "custom_skin"

# NPC position
position:
  world: "world"
  x: 100.5
  y: 65.0
  z: 200.5
  yaw: 90.0
  pitch: 0.0

# Item held by the NPC (Minecraft item ID)
held_item: "minecraft:diamond_sword"

# Armor worn by the NPC
armor:
  helmet: "minecraft:diamond_helmet"
  chestplate: "minecraft:diamond_chestplate"
  leggings: "minecraft:diamond_leggings"
  boots: "minecraft:diamond_boots"

# Whether NPC should look at players (each player sees NPC looking at them)
look_at_player: true

# Emote configuration
emote:
  # Emote UUID (leave empty to disable)
  id: "4c8ae710-df2e-47cd-814d-cc7bf21a3d67"
  # Interval between emotes (in ticks, 20 ticks = 1 second)
  interval: 100

# Click cooldown (in ticks)
click_cooldown: 20

# Actions executed when NPC is clicked
actions:
  - type: message
    value: "&aWelcome to the shop, {player_name}!"
  - type: command
    value: "say {player_name} is visiting the shop!"
    as_player: false
  - type: dialog
    value: "shop_menu"
```

### 📋 Configuration Options

| Option             | Type    | Default | Description                                        |
|--------------------|---------|---------|----------------------------------------------------|
| `display_name`     | String  | `"NPC"` | Name shown above NPC (supports `&` color codes)    |
| `always_show_name` | Boolean | `true`  | Always show the name tag                           |
| `skin`             | String  | `""`    | Skin name (filename or folder name)                |
| `look_at_player`   | Boolean | `true`  | NPC looks at each player individually (per-player) |
| `held_item`        | String  | `""`    | Item ID for held item                              |
| `click_cooldown`   | Integer | `20`    | Cooldown between clicks (ticks)                    |

## 💬 Dialog Configuration

Example dialog configuration (`dialogs/shop_menu.yml`):

```yaml
# Dialog title (supports color codes and placeholders)
title: "&6🏪 Item Shop"

# Dialog body text (supports color codes and placeholders)
body: "Hello {player_name}!\nWelcome to the shop.\nWhat would you like to buy?"

# Dialog buttons (up to 6 buttons)
buttons:
  # Button 1 - Buy Item
  - text: "🗡️ Diamond Sword - $100"
    commands:
      - "eco take {player_name} 100"
      - "give {player_name} diamond_sword 1"
    message: "&aThanks for your purchase!"
    as_player: false

  # Button 2 - Buy Armor
  - text: "🛡️ Diamond Armor - $400"
    commands:
      - "eco take {player_name} 400"
      - "give {player_name} diamond_helmet 1"
      - "give {player_name} diamond_chestplate 1"
      - "give {player_name} diamond_leggings 1"
      - "give {player_name} diamond_boots 1"
    message: "&aEnjoy your new armor!"
    as_player: false

  # Button 3 - Close
  - text: "❌ Close"
    message: "&7Come back soon!"
```

## 🎯 Action Types

| Type      | Description              | Example                        |
|-----------|--------------------------|--------------------------------|
| `command` | Execute a command        | `give {player_name} diamond 1` |
| `message` | Send a message to player | `&aWelcome!`                   |
| `dialog`  | Open a dialog            | `shop_menu`                    |

### 📌 Action Options

- **as_player** - Execute command as player (default: `false`)
  - `true`: Player runs the command
  - `false`: Console runs the command

## 🎬 Emotes

NPCs can play emotes at regular intervals. Use the emote UUID from Minecraft Bedrock Edition.

### Popular Emote UUIDs

| Emote       | UUID                                   |
|-------------|----------------------------------------|
| Wave        | `4c8ae710-df2e-47cd-814d-cc7bf21a3d67` |
| Simple Clap | `9a469a61-c83b-4ba9-b507-bdbe64430f72` |
| Over There  | `ce5c0300-7f03-455d-aaf1-352e4927b54d` |

## 🌍 Placeholders

AllayNPC supports [PlaceholderAPI](https://github.com/AzaleeX/PlaceholderAPI) placeholders in messages, dialog text, and commands:

- `{player_name}` - Player's name
- `{x}`, `{y}`, `{z}` - Player's coordinates
- `{online}` - Online player count
- `{max_online}` - Max player count
- And all other PAPI placeholders...

## 🎨 Color Codes

Use `&` for color codes in display names and messages:

| Code | Color       | Code | Color        |
|------|-------------|------|--------------|
| `&0` | Black       | `&8` | Dark Gray    |
| `&1` | Dark Blue   | `&9` | Blue         |
| `&2` | Dark Green  | `&a` | Green        |
| `&3` | Dark Aqua   | `&b` | Aqua         |
| `&4` | Dark Red    | `&c` | Red          |
| `&5` | Dark Purple | `&d` | Light Purple |
| `&6` | Gold        | `&e` | Yellow       |
| `&7` | Gray        | `&f` | White        |

**Formatting:** `&l` Bold, `&o` Italic, `&n` Underline, `&r` Reset

## 🔧 Dependencies

| Plugin                                                      | Required | Description         |
|-------------------------------------------------------------|----------|---------------------|
| [PlaceholderAPI](https://github.com/AzaleeX/PlaceholderAPI) | ✅ Yes    | Placeholder support |

## 🛠️ Building

```bash
# Clone the repository
git clone https://github.com/smartcmd/AllayNPC.git

# Build with Gradle
cd AllayNPC
./gradlew shadowJar

# Output: build/libs/AllayNPC-*-shaded.jar
```

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

Contributions are welcome! Feel free to:

- 🐛 Report bugs
- 💡 Suggest features
- 🔧 Submit pull requests

<div align="center">

**Made with ❤️ by [daoge_cmd](https://github.com/smartcmd)**

🌟 Star this repo if you like it! 🌟

</div>
