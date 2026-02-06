[🇺🇸 English](README_en_US.md) | [🇩🇪 Deutsch](README_de_DE.md) | [🇪🇸 Español](README_es_ES.md) | [🇨🇳 中文](README_zh_CN.md) | [🇯🇵 日本語](README_ja_JP.md)

# IronDiscipline

Comprehensive management and discipline maintenance plugin for Minecraft servers.
Designed for Military/Prison RP servers.

## Features

- **Rank System**: Permission management by rank, fully customizable via config.yml.
- **PTS (Permission to Speak)**: Permission system for lower ranks to speak.
- **Discord Integration**:
  - Account Linking (`/link`)
  - Role/Nickname Sync
  - Notification System
  - Server Status Display
- **Warning/Punishment System**:
  - Accumulate warnings with `/warn`.
  - Auto-isolation/Kick at a certain threshold.
  - `/jail` isolation system (Database storage).
- **Exam System**: Promotion exams using GUI.
- **Playtime Management**: Records online time.
- **Message Customization**: Most in-game messages can be customized.

## Requirements

- Java 17+
- Paper / Spigot 1.18+
- LuckPerms (Required)
- MySQL, SQLite, or H2 Database (Default)

## Installation

1. Download the latest JAR file from [Releases](https://github.com/kaji11-jp/IronDiscipline/releases).
2. Place it in the server's `plugins` folder.
3. Start the server.
4. `plugins/IronDiscipline/config.yml` will be generated. Edit it as needed (Database, Discord integration, etc.).
5. Restart the server or run `/iron reload` to apply settings.

## Configuration

### Database Configuration
Defaults to H2 Database (file-based), but MySQL is recommended for large servers.

```yaml
database:
  # Type: h2, sqlite, mysql
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: irondiscipline
    username: root
    password: "password"
```

### Discord Integration Configuration
Set your Discord Bot Token in `config.yml`.

```yaml
discord:
  enabled: true
  bot_token: "YOUR_TOKEN"
  guild_id: "YOUR_GUILD_ID"
  notification_channel_id: "YOUR_CHANNEL_ID"
```

## Commands

### 🌐 General Commands
| Command | Description | Permission |
|---|---|---|
| `/link [code]` | Link Discord account | None |
| `/playtime [top]` | View playtime (duty hours) | `iron.playtime.view` |
| `/radio <freq>` | Join/Leave radio channel | `iron.radio.use` |
| `/radiobroadcast <msg>` | Broadcast on radio | `iron.radio.use` |
| `/warnings [player]` | View warning history for self or others | `iron.warn.view` |

### 👮 Discipline/Admin Commands
| Command | Description | Permission |
|---|---|---|
| `/warn <player> <reason>` | Give a warning (accumulates for auto-punishment) | `iron.warn.use` |
| `/unwarn <player>` | Remove the latest warning | `iron.warn.admin` |
| `/clearwarnings <player>` | Clear all warnings | `iron.warn.admin` |
| `/jail <player> [reason]` | Jail a player | `iron.jail.use` |
| `/unjail <player>` | Release a player | `iron.jail.use` |
| `/setjail` | Set jail location to current position | `iron.jail.admin` |
| `/grant <player> [sec]` | Grant permission to speak (PTS) to a lower rank | `iron.pts.grant` |
| `/promote <player>` | Promote rank | `iron.rank.promote` |
| `/demote <player>` | Demote rank | `iron.rank.demote` |
| `/division <set/remove...>` | Manage division assignment | `iron.division.use` |
| `/exam <start/end...>` | Manage promotion exams | `iron.exam.use` |
| `/killlog [player] [count]` | View detailed PvP logs | `iron.killlog.view` |
| `/iron reload` | Reload configuration | `iron.admin` |

### 🤖 Discord Bot Commands (Slash Commands)
| Command | Description |
|---|---|
| `/link` | Account linking (Works in DM/Server) |
| `/settings` | Bot settings / Role linking management |
| `/panel` | Place linking/role management panel |
| `/promote, /demote` | Manage ranks (Can be executed from Discord) |
| `/division` | Manage divisions |
| `/kick, /ban` | Execute punishment |

## Build

```bash
mvn clean package
```

## Deployment

This project is designed to run on **Google Cloud Platform (GCP)** or generic VPS like **Xserver VPS**.

### 1. GCP (Google Cloud Platform)
See [GCP Deploy Guide (Docs)](docs/GCP_DEPLOY_en_US.md). You can set it up in minutes with the dedicated script.

### 2. Generic VPS (Xserver, ConoHa, etc.)
See [VPS Deploy Guide (Docs)](docs/VPS_DEPLOY_en_US.md). If you have an Ubuntu environment, you can set it up with a single script.

### 3. Bedrock (Mobile/Switch) Support
See [Cross Play Guide (Docs)](docs/CROSS_PLAY_en_US.md). Achieves cross-platform play using Geyser.

## 🔄 Auto Update

This project supports automatic builds via **GitHub Actions**.
When pushed to the `main` branch, the latest version is automatically built and released as the `latest` tag in [Releases](https://github.com/kaji11-jp/IronDiscipline/releases).

### How to update on server
Run the following command once to complete the update to the latest version and restart.

```bash
# After SSH connection
curl -sL https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/update-server.sh | sudo bash
```

## License

MIT License
