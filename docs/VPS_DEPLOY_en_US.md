[🇺🇸 English](VPS_DEPLOY_en_US.md) | [🇩🇪 Deutsch](VPS_DEPLOY_de_DE.md) | [🇪🇸 Español](VPS_DEPLOY_es_ES.md) | [🇨🇳 中文](VPS_DEPLOY_zh_CN.md) | [🇯🇵 日本語](VPS_DEPLOY_ja_JP.md)

# Generic VPS Deployment Guide (Xserver, ConoHa, Linode, etc.)

This project can be easily run on generic VPS (Virtual Private Server) other than Google Cloud Platform.

## Examples of Compatible VPS
- **Xserver VPS** (Japan, Fast, Stable)
- **ConoHa VPS** (Japan, Easy to use)
- **Linode / DigitalOcean / Vultr** (International, Affordable)

## 1. Server Preparation

### Recommended Specs
- **OS**: Ubuntu 22.04 LTS or 24.04 LTS
- **CPU**: 2 Cores or more
- **Memory**: 4GB or more (8GB recommended)

### Steps
1. Create an instance (server) from the VPS control panel.
2. Select **Ubuntu** as the OS.
3. Set the `root` password or register an SSH key.

## 2. Execute Setup Script

Simply connect to the server via SSH and run the following commands to complete the environment setup.

# 1. SSH Connection (PowerShell / Terminal)
ssh root@<Server-IP-Address>

# 2. Download and Execute Script
curl -O https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/setup-ubuntu.sh
sudo bash setup-ubuntu.sh

# Build
mvn clean package

# Upload
scp target/IronDiscipline-1.1.0.jar root@<Server-IP>:/opt/minecraft/plugins/

### When using FileZilla / WinSCP
1. Connect with Host: `<Server-IP>`, User: `root`, Password: `(The one you set)`.
2. Drag and drop the `.jar` file to `/opt/minecraft/plugins/`.

Finally, restart the server to apply changes:
```bash
ssh root@<Server-IP> "systemctl restart minecraft"
```

## 4. Open Ports (If necessary)

Many VPS providers have all ports open by default, but some like Xserver VPS require firewall settings in the control panel.

**Ports to Open:**
- TCP: `25565` (Java Edition)
- UDP: `19132` (Bedrock/Mobile - When using Geyser)

## 5. Discord Integration Settings

1. Open Config
```bash
nano /opt/minecraft/plugins/IronDiscipline/config.yml
```
2. Enter `bot_token` etc. and save (`Ctrl+S`, `Ctrl+X`)
3. Restart: `systemctl restart minecraft`
