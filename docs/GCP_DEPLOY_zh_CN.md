[🇺🇸 English](GCP_DEPLOY_en_US.md) | [🇩🇪 Deutsch](GCP_DEPLOY_de_DE.md) | [🇪🇸 Español](GCP_DEPLOY_es_ES.md) | [🇨🇳 中文](GCP_DEPLOY_zh_CN.md) | [🇯🇵 日本語](GCP_DEPLOY_ja_JP.md)

# IronDiscipline GCP 部署指南

## 前提条件

1. 已安装 [Google Cloud SDK](https://cloud.google.com/sdk/docs/install)
2. 已创建 GCP 项目
3. 已启用计费

## 方法 1: 简易部署 (推荐)

### 1. 构建插件

```powershell
mvn clean package
```

### 2. 上传到 GCS 存储桶

```bash
# 创建存储桶
gsutil mb gs://irondiscipline-server

# 上传 JAR
gsutil cp target/IronDiscipline-latest.jar gs://irondiscipline-server/
gsutil cp plugins/LuckPerms*.jar gs://irondiscipline-server/
```

### 3. 创建 GCE 实例

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

### 4. 防火墙设置

```bash
gcloud compute firewall-rules create minecraft-port \
    --allow tcp:25565,udp:25565 \
    --target-tags=minecraft-server
```

### 5. 连接

```bash
# 查看 IP
gcloud compute instances describe irondiscipline-mc --zone=asia-northeast1-b \
    --format='get(networkInterfaces[0].accessConfigs[0].natIP)'
```

在 Minecraft 中连接到 `<IP>:25565`！

---

## 方法 2: Docker (高级)

```bash
# SSH 连接
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b

# 安装 Docker
sudo apt-get update && sudo apt-get install -y docker.io docker-compose

# 启动容器
docker-compose up -d
```

---

## 估算费用 (东京区域)

| 机器类型 | RAM | 每月 (约) |
|-------------|-----|-------------|
| e2-micro | 1GB | 免费层级 |
| e2-small | 2GB | ~$15 |
| e2-medium | 4GB | ~$30 |

---

## Discord 机器人设置

1. 启动服务器后，编辑 config.yml：

```bash
gcloud compute ssh irondiscipline-mc --zone=asia-northeast1-b
sudo nano /opt/minecraft/plugins/IronDiscipline/config.yml
```

2. 输入 Discord 设置：

```yaml
discord:
  enabled: true
  bot_token: "YOUR_BOT_TOKEN"
  notification_channel_id: "CHANNEL_ID"
  guild_id: "SERVER_ID"
```

3. 重启服务器：

```bash
sudo systemctl restart minecraft
```

---

## 常用命令

```bash
# 查看日志
sudo journalctl -u minecraft -f

# 停止服务器
sudo systemctl stop minecraft

# 启动服务器
sudo systemctl start minecraft

# 连接控制台
screen -r minecraft
```
