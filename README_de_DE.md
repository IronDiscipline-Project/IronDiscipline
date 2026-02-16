[🇺🇸 English](README_en_US.md) | [🇩🇪 Deutsch](README_de_DE.md) | [🇪🇸 Español](README_es_ES.md) | [🇨🇳 中文](README_zh_CN.md) | [🇯🇵 日本語](README_ja_JP.md)

# IronDiscipline (Eiserne Disziplin)

Umfassendes Verwaltungs- und Disziplin-Plugin für Minecraft-Server.
Entwickelt für Militär-/Gefängnis-RP-Server.

> ⚠️ **Diese Version ist nur für PaperSpigot + LuckPerms.** Folia wird nicht unterstützt.
> Für Folia verwenden Sie bitte [IronDiscipline-dev](https://github.com/kaji11-jp/IronDiscipline-dev).

## Funktionen

- **Rangsystem**: Berechtigungsverwaltung nach Rang, vollständig anpassbar über `config.yml`.
- **PTS (Sprecherlaubnis)**: System zur Erteilung von Sprecherlaubnis für niedrigere Ränge.
- **Discord-Integration**:
  - Kontoverknüpfung (`/link`)
  - Rollen-/Nickname-Synchronisation
  - Benachrichtigungssystem
  - Serverstatus-Anzeige
- **Warn-/Bestrafungssystem**:
  - `/warn` zum Ansammeln von Verwarnungen.
  - Automatische Isolierung/Kick bei Erreichen eines bestimmten Grenzwerts.
  - `/jail` Isolierungssystem (Datenbankspeicherung).
- **Prüfungssystem**: Beförderungsprüfungen über GUI.
- **Dienstzeitverwaltung**: Erfassung der Online-Zeit.
- **Nachrichtenanpassung**: Die meisten In-Game-Nachrichten können angepasst werden.

## Anforderungen

- Java 17+
- Paper / Spigot 1.18+ (**Folia nicht unterstützt**)
- LuckPerms (Erforderlich)
- MySQL, SQLite oder H2 Database (Standard)

## Installation

1. Laden Sie die neueste JAR-Datei von [Releases](https://github.com/kaji11-jp/IronDiscipline/releases) herunter.
2. Legen Sie sie in den `plugins`-Ordner des Servers.
3. Starten Sie den Server.
4. `plugins/IronDiscipline/config.yml` wird erstellt. Bearbeiten Sie sie nach Bedarf (Datenbank, Discord-Integration usw.).
5. Starten Sie den Server neu oder führen Sie `/iron reload` aus, um die Einstellungen zu übernehmen.

## Konfiguration

### Datenbankkonfiguration
Standardmäßig wird H2 Database (dateibasiert) verwendet, aber für große Server wird MySQL empfohlen.

```yaml
database:
  # Typ: h2, sqlite, mysql
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: irondiscipline
    username: root
    password: "password"
```

### Discord-Integrationskonfiguration
Legen Sie Ihr Discord Bot Token in der `config.yml` fest.

```yaml
discord:
  enabled: true
  bot_token: "IHR_TOKEN"
  guild_id: "IHRE_GUILD_ID"
  notification_channel_id: "IHRE_CHANNEL_ID"
```

## Befehlsliste

### 🌐 Allgemeine Befehle
| Befehl | Beschreibung | Berechtigung |
|---|---|---|
| `/link [Code]` | Discord-Konto verknüpfen | Keine |
| `/playtime [top]` | Dienstzeit (Spielzeit) anzeigen | `iron.playtime.view` |
| `/radio <Freq>` | Funkkanal beitreten/verlassen | `iron.radio.use` |
| `/radiobroadcast <Nachricht>` | Rundfunk senden | `iron.radio.use` |
| `/warnings [Spieler]` | Warnhistorie anzeigen | `iron.warn.view` |

### 👮 Disziplin-/Verwaltungsbefehle
| Befehl | Beschreibung | Berechtigung |
|---|---|---|
| `/warn <Spieler> <Grund>` | Verwarnung erteilen (sammelt sich an) | `iron.warn.use` |
| `/unwarn <Spieler>` | Letzte Verwarnung entfernen | `iron.warn.admin` |
| `/clearwarnings <Spieler>` | Alle Verwarnungen löschen | `iron.warn.admin` |
| `/jail <Spieler> [Grund]` | Spieler isolieren | `iron.jail.use` |
| `/unjail <Spieler>` | Spieler freilassen | `iron.jail.use` |
| `/setjail` | Isolierungsort auf aktuelle Position setzen | `iron.jail.admin` |
| `/grant <Spieler> [Sek]` | Sprecherlaubnis (PTS) erteilen | `iron.pts.grant` |
| `/promote <Spieler>` | Rang befördern | `iron.rank.promote` |
| `/demote <Spieler>` | Rang degradieren | `iron.rank.demote` |
| `/division <set/remove...>` | Abteilungszuweisung verwalten | `iron.division.use` |
| `/exam <start/end...>` | Beförderungsprüfungen verwalten | `iron.exam.use` |
| `/killlog [Spieler] [Anzahl]` | Detaillierte PvP-Logs anzeigen | `iron.killlog.view` |
| `/iron reload` | Konfiguration neu laden | `iron.admin` |

### 🤖 Discord Bot Befehle (Slash Commands)
| Befehl | Beschreibung |
|---|---|
| `/link` | Kontoverknüpfung (DM/Server) |
| `/settings` | Bot-Einstellungen / Rollenverwaltung |
| `/panel` | Verknüpfungs-/Rollenverwaltungspanel platzieren |
| `/promote, /demote` | Ränge verwalten (über Discord) |
| `/division` | Abteilungen verwalten |
| `/kick, /ban` | Bestrafung ausführen |

## Build

```bash
mvn clean package
```

## Bereitstellung

Dieses Projekt ist für den Betrieb auf **Google Cloud Platform (GCP)** oder generischen VPS wie **Xserver VPS** konzipiert.

### 1. GCP (Google Cloud Platform)
Siehe [GCP Bereitstellungsleitfaden (Docs)](docs/GCP_DEPLOY_de_DE.md). Mit dem dedizierten Skript in wenigen Minuten eingerichtet.

### 2. Generischer VPS (Xserver, ConoHa, etc.)
Siehe [VPS Bereitstellungsleitfaden (Docs)](docs/VPS_DEPLOY_de_DE.md). Bei Ubuntu-Umgebung mit einem einzigen Skript einrichtbar.

### 3. Bedrock (Mobile/Switch) Unterstützung
Siehe [Cross-Play Leitfaden (Docs)](docs/CROSS_PLAY_de_DE.md). Ermöglicht Cross-Plattform-Spiel mittels Geyser.

## 🔄 Automatische Updates

Dieses Projekt unterstützt automatische Builds über **GitHub Actions**.
Bei Push in den `main`-Branch wird automatisch die neueste Version gebaut und als `latest`-Tag in [Releases](https://github.com/kaji11-jp/IronDiscipline/releases) veröffentlicht.

### Update-Methode auf dem Server
Führen Sie den folgenden Befehl einmal aus, um das Update auf die neueste Version abzuschließen und neu zu starten.

```bash
# Nach SSH-Verbindung
curl -sL https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/update-server.sh | sudo bash
```

## Lizenz

MIT License
