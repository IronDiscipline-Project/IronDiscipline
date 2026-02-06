[🇺🇸 English](GCP_DEPLOY_en_US.md) | [🇩🇪 Deutsch](GCP_DEPLOY_de_DE.md) | [🇪🇸 Español](GCP_DEPLOY_es_ES.md) | [🇨🇳 中文](GCP_DEPLOY_zh_CN.md) | [🇯🇵 日本語](GCP_DEPLOY_ja_JP.md)

# IronDiscipline GCP Bereitstellungsleitfaden

## Voraussetzungen

1. [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) installiert
2. GCP-Projekt erstellt
3. Abrechnung aktiviert

## Methode 1: Einfache Bereitstellung (Empfohlen)

### 1. Plugin bauen

```powershell
mvn clean package
```

### 2. In GCS-Bucket hochladen

```bash
# Bucket erstellen
gsutil mb gs://irondiscipline-server

# JAR hochladen
gsutil cp target/IronDiscipline-latest.jar gs://irondiscipline-server/
gsutil cp plugins/LuckPerms*.jar gs://irondiscipline-server/
```

### 3. GCE-Instanz erstellen

```bash
gcloud compute instances create irondiscipline-mc \
    --zone=asia-northeast1-b \
    --machine-type=e2-medium \
    --image-family=ubuntu-2204-lts \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=30GB \
    --tags=minecraft-server \
    --metadata-from-file startup-script=scripts/gcp-startup.sh
```

### 4. Firewall-Einstellungen

```bash
gcloud compute firewall-rules create minecraft-port \
    --allow tcp:25565,udp:25565 \
    --target-tags=minecraft-server
```

### 5. Verbindung

```bash
# IP überprüfen
gcloud compute instances describe irondiscipline-mc --zone=asia-northeast1-b \
    --format='get(networkInterfaces[0].accessConfigs[0].natIP)'
```

Verbinden Sie sich in Minecraft mit `<IP>:25565`!

---

## Methode 2: Docker (Fortgeschritten)

```bash
# SSH-Verbindung
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b

# Docker installieren
sudo apt-get update && sudo apt-get install -y docker.io docker-compose

# Container starten
docker-compose up -d
```

---

## Geschätzte Kosten (Region Tokio)

| Maschinentyp | RAM | Monatlich (Ca.) |
|-------------|-----|-------------|
| e2-micro | 1GB | Kostenloses Kontingent |
| e2-small | 2GB | ~$15 |
| e2-medium | 4GB | ~$30 |

---

## Discord Bot Einrichtung

1. Nach dem Serverstart config.yml bearbeiten:

```bash
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b
sudo nano /opt/minecraft/plugins/IronDiscipline/config.yml
```

2. Discord-Einstellungen eingeben:

```yaml
discord:
  enabled: true
  bot_token: "IHR_BOT_TOKEN"
  notification_channel_id: "CHANNEL_ID"
  guild_id: "SERVER_ID"
```

3. Server neu starten:

```bash
sudo systemctl restart minecraft
```

---

## Nützliche Befehle

```bash
# Logs überprüfen
sudo journalctl -u minecraft -f

# Server stoppen
sudo systemctl stop minecraft

# Server starten
sudo systemctl start minecraft

# Mit Konsole verbinden
screen -r minecraft
```
