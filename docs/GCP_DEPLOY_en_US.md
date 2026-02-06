[🇺🇸 English](GCP_DEPLOY_en_US.md) | [🇩🇪 Deutsch](GCP_DEPLOY_de_DE.md) | [🇪🇸 Español](GCP_DEPLOY_es_ES.md) | [🇨🇳 中文](GCP_DEPLOY_zh_CN.md) | [🇯🇵 日本語](GCP_DEPLOY_ja_JP.md)

# IronDiscipline GCP Deployment Guide

## Prerequisites

1. [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) installed
2. GCP project created
3. Billing enabled

## Method 1: Easy Deployment (Recommended)

### 1. Build the Plugin

```powershell
mvn clean package
```

### 2. Upload to GCS Bucket

```bash
# Create bucket
gsutil mb gs://irondiscipline-server

# Upload JAR
gsutil cp target/IronDiscipline-latest.jar gs://irondiscipline-server/
gsutil cp plugins/LuckPerms*.jar gs://irondiscipline-server/
```

### 3. Create GCE Instance

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

### 4. Firewall Settings

```bash
gcloud compute firewall-rules create minecraft-port \
    --allow tcp:25565,udp:25565 \
    --target-tags=minecraft-server
```

### 5. Connection

```bash
# Check IP
gcloud compute instances describe irondiscipline-mc --zone=asia-northeast1-b \
    --format='get(networkInterfaces[0].accessConfigs[0].natIP)'
```

Connect to `<IP>:25565` in Minecraft!

---

## Method 2: Docker (Advanced)

```bash
# SSH Connection
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b

# Install Docker
sudo apt-get update && sudo apt-get install -y docker.io docker-compose

# Start Container
docker-compose up -d
```

---

## Estimated Cost (Tokyo Region)

| Machine Type | RAM | Monthly (Approx.) |
|-------------|-----|-------------|
| e2-micro | 1GB | Free Tier |
| e2-small | 2GB | ~$15 |
| e2-medium | 4GB | ~$30 |

---

## Discord Bot Setup

1. After starting the server, edit config.yml:

```bash
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b
sudo nano /opt/minecraft/plugins/IronDiscipline/config.yml
```

2. Enter Discord settings:

```yaml
discord:
  enabled: true
  bot_token: "YOUR_BOT_TOKEN"
  notification_channel_id: "CHANNEL_ID"
  guild_id: "SERVER_ID"
```

3. Restart Server:

```bash
sudo systemctl restart minecraft
```

---

## Useful Commands

```bash
# Check Logs
sudo journalctl -u minecraft -f

# Stop Server
sudo systemctl stop minecraft

# Start Server
sudo systemctl start minecraft

# Connect to Console
screen -r minecraft
```
