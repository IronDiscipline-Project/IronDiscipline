[🇺🇸 English](VPS_DEPLOY_en_US.md) | [🇩🇪 Deutsch](VPS_DEPLOY_de_DE.md) | [🇪🇸 Español](VPS_DEPLOY_es_ES.md) | [🇨🇳 中文](VPS_DEPLOY_zh_CN.md) | [🇯🇵 日本語](VPS_DEPLOY_ja_JP.md)

# Allgemeiner VPS Bereitstellungsleitfaden (Xserver, ConoHa, Linode usw.)

Dieses Projekt kann einfach auf allgemeinen VPS (Virtual Private Server) ausgeführt werden, die nicht Google Cloud Platform sind.

## Beispiele für kompatible VPS
- **Xserver VPS** (Japan, Schnell, Stabil)
- **ConoHa VPS** (Japan, Einfach zu bedienen)
- **Linode / DigitalOcean / Vultr** (International, Günstig)

## 1. Servervorbereitung

### Empfohlene Spezifikationen
- **OS**: Ubuntu 22.04 LTS oder 24.04 LTS
- **CPU**: 2 Kerne oder mehr
- **Speicher**: 4 GB oder mehr (8 GB empfohlen)

### Schritte
1. Erstellen Sie eine Instanz (Server) über das VPS-Verwaltungspanel.
2. Wählen Sie **Ubuntu** als Betriebssystem.
3. Legen Sie das `root`-Passwort fest oder registrieren Sie einen SSH-Schlüssel.

## 2. Einrichtungsskript ausführen

Verbinden Sie sich einfach per SSH mit dem Server und führen Sie die folgenden Befehle aus, um die Umgebungseinrichtung abzuschließen.

# 1. SSH-Verbindung (PowerShell / Terminal)
ssh root@<Server-IP-Adresse>

# 2. Skript herunterladen und ausführen
curl -O https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/setup-ubuntu.sh
sudo bash setup-ubuntu.sh

# Bauen
mvn clean package

# Hochladen
scp target/IronDiscipline-1.1.0.jar root@<Server-IP>:/opt/minecraft/plugins/

### Bei Verwendung von FileZilla / WinSCP
1. Verbinden Sie mit Host: `<Server-IP>`, Benutzer: `root`, Passwort: `(Das festgelegte)`.
2. Ziehen Sie die `.jar`-Datei per Drag & Drop nach `/opt/minecraft/plugins/`.

Starten Sie den Server schließlich neu, um die Änderungen zu übernehmen:
```bash
ssh root@<Server-IP> "systemctl restart minecraft"
```

## 4. Portfreigabe (Falls erforderlich)

Viele VPS-Anbieter haben standardmäßig alle Ports geöffnet, aber einige wie Xserver VPS erfordern Firewall-Einstellungen im Verwaltungspanel.

**Zu öffnende Ports:**
- TCP: `25565` (Java Edition)
- UDP: `19132` (Bedrock/Mobil - Bei Verwendung von Geyser)

## 5. Discord-Integrations-Einstellungen

1. Konfiguration öffnen
```bash
nano /opt/minecraft/plugins/IronDiscipline/config.yml
```
2. Geben Sie `bot_token` usw. ein und speichern Sie (`Strg+S`, `Strg+X`)
3. Neustart: `systemctl restart minecraft`
