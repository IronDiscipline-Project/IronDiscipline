[🇺🇸 English](README_en_US.md) | [🇩🇪 Deutsch](README_de_DE.md) | [🇪🇸 Español](README_es_ES.md) | [🇨🇳 中文](README_zh_CN.md) | [🇯🇵 日本語](README_ja_JP.md)

# IronDiscipline (铁的纪律)

Minecraft 服务器综合管理与纪律维护插件。
专为军队/监狱角色扮演服务器设计。

## 功能

- **军衔系统**: 基于军衔的权限管理，可通过 `config.yml` 完全自定义。
- **PTS (发言许可)**: 下级发言许可系统。
- **Discord 集成**:
  - 账号绑定 (`/link`)
  - 角色/昵称同步
  - 通知系统
  - 服务器状态显示
- **警告/惩罚系统**:
  - `/warn` 累积警告。
  - 达到一定阈值自动隔离/踢出。
  - `/jail` 隔离系统（数据库存储）。
- **考试系统**: 使用 GUI 进行晋升考试。
- **执勤时间管理**: 记录在线时间。
- **消息自定义**: 大多数游戏内消息均可自定义。

## 需求

- Java 17+
- Paper / Spigot 1.18+
- LuckPerms (必需)
- MySQL, SQLite 或 H2 Database (默认)

## 安装

1. 从 [Releases](https://github.com/kaji11-jp/IronDiscipline/releases) 下载最新的 JAR 文件。
2. 将其放入服务器的 `plugins` 文件夹中。
3. 启动服务器。
4. 将生成 `plugins/IronDiscipline/config.yml`。根据需要进行编辑（数据库、Discord 集成等）。
5. 重启服务器或运行 `/iron reload` 以应用设置。

## 配置

### 数据库配置
默认为 H2 Database (基于文件)，但对于大型服务器建议使用 MySQL。

```yaml
database:
  # 类型: h2, sqlite, mysql
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: irondiscipline
    username: root
    password: "password"
```

### Discord 集成配置
在 `config.yml` 中设置您的 Discord Bot Token。

```yaml
discord:
  enabled: true
  bot_token: "YOUR_TOKEN"
  guild_id: "YOUR_GUILD_ID"
  notification_channel_id: "YOUR_CHANNEL_ID"
```

## 命令列表

### 🌐 通用命令
| 命令 | 说明 | 权限 |
|---|---|---|
| `/link [代码]` | 绑定 Discord 账号 | 无 |
| `/playtime [top]` | 查看执勤时间（游戏时间） | `iron.playtime.view` |
| `/radio <频率>` | 加入/退出无线电频道 | `iron.radio.use` |
| `/radiobroadcast <消息>` | 无线电广播 | `iron.radio.use` |
| `/warnings [玩家]` | 查看警告记录 | `iron.warn.view` |

### 👮 纪律/管理命令
| 命令 | 说明 | 权限 |
|---|---|---|
| `/warn <玩家> <理由>` | 给予警告（累积自动惩罚） | `iron.warn.use` |
| `/unwarn <玩家>` | 移除最新的警告 | `iron.warn.admin` |
| `/clearwarnings <玩家>` | 清除所有警告 | `iron.warn.admin` |
| `/jail <玩家> [理由]` | 隔离玩家 | `iron.jail.use` |
| `/unjail <玩家>` | 释放玩家 | `iron.jail.use` |
| `/setjail` | 将隔离点设置为当前位置 | `iron.jail.admin` |
| `/grant <玩家> [秒]` | 授予发言许可 (PTS) | `iron.pts.grant` |
| `/promote <玩家>` | 晋升军衔 | `iron.rank.promote` |
| `/demote <玩家>` | 降级军衔 | `iron.rank.demote` |
| `/division <set/remove...>` | 管理部门分配 | `iron.division.use` |
| `/exam <start/end...>` | 管理晋升考试 | `iron.exam.use` |
| `/killlog [玩家] [数量]` | 查看详细 PvP 日志 | `iron.killlog.view` |
| `/iron reload` | 重载配置 | `iron.admin` |

### 🤖 Discord 机器人命令 (Slash Commands)
| 命令 | 说明 |
|---|---|
| `/link` | 账号绑定（支持 DM/服务器） |
| `/settings` | 机器人设置 / 角色绑定管理 |
| `/panel` | 放置绑定/角色管理面板 |
| `/promote, /demote` | 管理军衔（可从 Discord 执行） |
| `/division` | 管理部门 |
| `/kick, /ban` | 执行惩罚 |

## 构建

```bash
mvn clean package
```

## 部署

本项目设计用于在 **Google Cloud Platform (GCP)** 或通用 VPS（如 **Xserver VPS**）上运行。

### 1. GCP (Google Cloud Platform)
请参阅 [GCP 部署指南 (Docs)](docs/GCP_DEPLOY_zh_CN.md)。使用专用脚本可在几分钟内完成设置。

### 2. 通用 VPS (Xserver, ConoHa 等)
请参阅 [VPS 部署指南 (Docs)](docs/VPS_DEPLOY_zh_CN.md)。如果是 Ubuntu 环境，可以通过一个脚本完成设置。

### 3. 基岩版 (手机/Switch) 支持
请参阅 [跨平台游戏指南 (Docs)](docs/CROSS_PLAY_zh_CN.md)。使用 Geyser 实现跨平台游戏。

## 🔄 自动更新

本项目支持通过 **GitHub Actions** 进行自动构建。
推送到 `main` 分支时，会自动构建最新版本并作为 `latest` 标签发布在 [Releases](https://github.com/kaji11-jp/IronDiscipline/releases) 中。

### 服务器更新方法
只需运行以下命令一次，即可完成更新到最新版本并重启。

```bash
# SSH 连接后
curl -sL https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/update-server.sh | sudo bash
```

## 许可证

MIT License
