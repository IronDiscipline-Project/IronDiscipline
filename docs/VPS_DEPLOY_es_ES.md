[🇺🇸 English](VPS_DEPLOY_en_US.md) | [🇩🇪 Deutsch](VPS_DEPLOY_de_DE.md) | [🇪🇸 Español](VPS_DEPLOY_es_ES.md) | [🇨🇳 中文](VPS_DEPLOY_zh_CN.md) | [🇯🇵 日本語](VPS_DEPLOY_ja_JP.md)

# Guía de Despliegue en VPS Genérico (Xserver, ConoHa, Linode, etc.)

Este proyecto se puede ejecutar fácilmente en VPS (Servidor Privado Virtual) genéricos que no sean Google Cloud Platform.

## Ejemplos de VPS Compatibles
- **Xserver VPS** (Japón, Rápido, Estable)
- **ConoHa VPS** (Japón, Fácil de usar)
- **Linode / DigitalOcean / Vultr** (Internacional, Económico)

## 1. Preparación del Servidor

### Especificaciones Recomendadas
- **OS**: Ubuntu 22.04 LTS o 24.04 LTS
- **CPU**: 2 núcleos o más
- **Memoria**: 4 GB o más (8 GB recomendados)

### Pasos
1. Cree una instancia (servidor) desde el panel de administración del VPS.
2. Seleccione **Ubuntu** como sistema operativo.
3. Establezca la contraseña `root` o registre una clave SSH.

## 2. Ejecutar Script de Configuración

Simplemente conéctese al servidor a través de SSH y ejecute los siguientes comandos para completar la configuración del entorno.

# 1. Conexión SSH (PowerShell / Terminal)
ssh root@<Dirección-IP-del-Servidor>

# 2. Descargar y Ejecutar Script
curl -O https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/setup-ubuntu.sh
sudo bash setup-ubuntu.sh

# Construir
mvn clean package

# Subir
scp target/IronDiscipline-1.1.0.jar root@<IP-del-Servidor>:/opt/minecraft/plugins/

### Al usar FileZilla / WinSCP
1. Conéctese con Host: `<IP-del-Servidor>`, Usuario: `root`, Contraseña: `(La que estableció)`.
2. Arrastre y suelte el archivo `.jar` en `/opt/minecraft/plugins/`.

Finalmente, reinicie el servidor para aplicar los cambios:
```bash
ssh root@<IP-del-Servidor> "systemctl restart minecraft"
```

## 4. Abrir Puertos (Si es necesario)

Muchos proveedores de VPS tienen todos los puertos abiertos por defecto, pero algunos como Xserver VPS requieren configuración de firewall en el panel de administración.

**Puertos para Abrir:**
- TCP: `25565` (Java Edition)
- UDP: `19132` (Bedrock/Móvil - Al usar Geyser)

## 5. Configuración de Integración con Discord

1. Abrir Configuración
```bash
nano /opt/minecraft/plugins/IronDiscipline/config.yml
```
2. Ingrese `bot_token` etc. y guarde (`Ctrl+S`, `Ctrl+X`)
3. Reiniciar: `systemctl restart minecraft`
