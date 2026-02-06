[🇺🇸 English](VPS_DEPLOY_en_US.md) | [🇩🇪 Deutsch](VPS_DEPLOY_de_DE.md) | [🇪🇸 Español](VPS_DEPLOY_es_ES.md) | [🇨🇳 中文](VPS_DEPLOY_zh_CN.md) | [🇯🇵 日本語](VPS_DEPLOY_ja_JP.md)

# 通用 VPS 部署指南 (Xserver, ConoHa, Linode 等)

本项目可以在 Google Cloud Platform 以外的通用 VPS (虚拟专用服务器) 上轻松运行。

## 兼容 VPS 示例
- **Xserver VPS** (日本, 快速, 稳定)
- **ConoHa VPS** (日本, 易于使用)
- **Linode / DigitalOcean / Vultr** (国际, 实惠)

## 1. 服务器准备

### 推荐配置
- **操作系统**: Ubuntu 22.04 LTS 或 24.04 LTS
- **CPU**: 2 核或更多
- **内存**: 4 GB 或更多 (推荐 8 GB)

### 步骤
1. 从 VPS 管理面板创建实例（服务器）。
2. 选择 **Ubuntu** 作为操作系统。
3. 设置 `root` 密码或注册 SSH 密钥。

## 2. 执行设置脚本

只需通过 SSH 连接到服务器并运行以下命令即可完成环境设置。

# 1. SSH 连接 (PowerShell / Terminal)
ssh root@<服务器IP地址>

# 2. 下载并执行脚本
curl -O https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/setup-ubuntu.sh
sudo bash setup-ubuntu.sh

# 构建
mvn clean package

# 上传
scp target/IronDiscipline-1.1.0.jar root@<服务器IP>:/opt/minecraft/plugins/

### 使用 FileZilla / WinSCP 时
1. 连接主机: `<服务器IP>`, 用户: `root`, 密码: `(您设置的密码)`。
2. 将 `.jar` 文件拖放到 `/opt/minecraft/plugins/`。

最后，重启服务器以应用更改：
```bash
ssh root@<服务器IP> "systemctl restart minecraft"
```

## 4. 开放端口 (如有必要)

许多 VPS 提供商默认开放所有端口，但像 Xserver VPS 这样的一些提供商需要在管理面板中进行防火墙设置。

**需要开放的端口:**
- TCP: `25565` (Java 版)
- UDP: `19132` (基岩版/手机 - 使用 Geyser 时)

## 5. Discord 集成设置

1. 打开配置
```bash
nano /opt/minecraft/plugins/IronDiscipline/config.yml
```
2. 输入 `bot_token` 等并保存 (`Ctrl+S`, `Ctrl+X`)
3. 重启: `systemctl restart minecraft`
