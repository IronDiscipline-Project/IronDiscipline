[🇺🇸 English](GCP_DEPLOY_en_US.md) | [🇩🇪 Deutsch](GCP_DEPLOY_de_DE.md) | [🇪🇸 Español](GCP_DEPLOY_es_ES.md) | [🇨🇳 中文](GCP_DEPLOY_zh_CN.md) | [🇯🇵 日本語](GCP_DEPLOY_ja_JP.md)

# Guía de Despliegue en GCP para IronDiscipline

## Requisitos Previos

1. [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) instalado
2. Proyecto GCP creado
3. Facturación habilitada

## Método 1: Despliegue Fácil (Recomendado)

### 1. Construir el Plugin

```powershell
mvn clean package
```

### 2. Subir al Bucket de GCS

```bash
# Crear bucket
gsutil mb gs://irondiscipline-server

# Subir JAR
gsutil cp target/IronDiscipline-latest.jar gs://irondiscipline-server/
gsutil cp plugins/LuckPerms*.jar gs://irondiscipline-server/
```

### 3. Crear Instancia de GCE

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

### 4. Configuración del Firewall

```bash
gcloud compute firewall-rules create minecraft-port \
    --allow tcp:25565,udp:25565 \
    --target-tags=minecraft-server
```

### 5. Conexión

```bash
# Verificar IP
gcloud compute instances describe irondiscipline-mc --zone=asia-northeast1-b \
    --format='get(networkInterfaces[0].accessConfigs[0].natIP)'
```

¡Conéctese a `<IP>:25565` en Minecraft!

---

## Método 2: Docker (Avanzado)

```bash
# Conexión SSH
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b

# Instalar Docker
sudo apt-get update && sudo apt-get install -y docker.io docker-compose

# Iniciar Contenedor
docker-compose up -d
```

---

## Costo Estimado (Región Tokio)

| Tipo de Máquina | RAM | Mensual (Aprox.) |
|-------------|-----|-------------|
| e2-micro | 1GB | Nivel Gratuito |
| e2-small | 2GB | ~$15 |
| e2-medium | 4GB | ~$30 |

---

## Configuración del Bot de Discord

1. Después de iniciar el servidor, edite config.yml:

```bash
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b
sudo nano /opt/minecraft/plugins/IronDiscipline/config.yml
```

2. Ingrese la configuración de Discord:

```yaml
discord:
  enabled: true
  bot_token: "SU_BOT_TOKEN"
  notification_channel_id: "CHANNEL_ID"
  guild_id: "SERVER_ID"
```

3. Reiniciar Servidor:

```bash
sudo systemctl restart minecraft
```

---

## Comandos Útiles

```bash
# Ver Logs
sudo journalctl -u minecraft -f

# Detener Servidor
sudo systemctl stop minecraft

# Iniciar Servidor
sudo systemctl start minecraft

# Conectar a la Consola
screen -r minecraft
```
