[🇺🇸 English](README_en_US.md) | [🇩🇪 Deutsch](README_de_DE.md) | [🇪🇸 Español](README_es_ES.md) | [🇨🇳 中文](README_zh_CN.md) | [🇯🇵 日本語](README_ja_JP.md)

# IronDiscipline (Disciplina de Hierro)

Plugin de gestión integral y mantenimiento de disciplina para servidores de Minecraft.
Diseñado para servidores de RP Militar/Prisión.

> ⚠️ **Esta versión es solo para PaperSpigot + LuckPerms.** Folia no es compatible.
> Para Folia, utilice [IronDiscipline-dev](https://github.com/kaji11-jp/IronDiscipline-dev).

## Características

- **Sistema de Rangos**: Gestión de permisos por rango, totalmente personalizable en `config.yml`.
- **PTS (Permiso para Hablar)**: Sistema de permisos para que los rangos inferiores hablen.
- **Integración con Discord**:
  - Vinculación de cuentas (`/link`)
  - Sincronización de Roles/Apodos
  - Sistema de notificaciones
  - Visualización del estado del servidor
- **Sistema de Advertencia/Castigo**:
  - `/warn` para acumular advertencias.
  - Auto-aislamiento/Expulsión al alcanzar cierto límite.
  - `/jail` sistema de aislamiento (almacenamiento en BD).
- **Sistema de Exámenes**: Exámenes de ascenso mediante GUI.
- **Gestión de Tiempo de Servicio**: Registro del tiempo en línea.
- **Personalización de Mensajes**: La mayoría de los mensajes del juego se pueden personalizar.

## Requisitos

- Java 17+
- Paper / Spigot 1.18+ (**Folia no compatible**)
- LuckPerms (Requerido)
- MySQL, SQLite o H2 Database (Por defecto)

## Instalación

1. Descargue el último archivo JAR desde [Releases](https://github.com/kaji11-jp/IronDiscipline/releases).
2. Colóquelo en la carpeta `plugins` del servidor.
3. Inicie el servidor.
4. Se generará `plugins/IronDiscipline/config.yml`. Edítelo según sea necesario (Base de datos, integración con Discord, etc.).
5. Reinicie el servidor o ejecute `/iron reload` para aplicar la configuración.

## Configuración

### Configuración de Base de Datos
Por defecto utiliza H2 Database (basada en archivos), pero se recomienda MySQL para servidores grandes.

```yaml
database:
  # Tipo: h2, sqlite, mysql
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: irondiscipline
    username: root
    password: "password"
```

### Configuración de Integración con Discord
Establezca su Token de Bot de Discord en `config.yml`.

```yaml
discord:
  enabled: true
  bot_token: "SU_TOKEN"
  guild_id: "SU_GUILD_ID"
  notification_channel_id: "SU_CHANNEL_ID"
```

## Lista de Comandos

### 🌐 Comandos Generales
| Comando | Descripción | Permiso |
|---|---|---|
| `/link [código]` | Vincular cuenta de Discord | Ninguno |
| `/playtime [top]` | Ver tiempo de servicio (tiempo de juego) | `iron.playtime.view` |
| `/radio <frecuencia>` | Unirse/Salir del canal de radio | `iron.radio.use` |
| `/radiobroadcast <mensaje>` | Transmitir por radio | `iron.radio.use` |
| `/warnings [jugador]` | Ver historial de advertencias | `iron.warn.view` |

### 👮 Comandos de Disciplina/Administración
| Comando | Descripción | Permiso |
|---|---|---|
| `/warn <jugador> <razón>` | Dar una advertencia (se acumulan) | `iron.warn.use` |
| `/unwarn <jugador>` | Eliminar la última advertencia | `iron.warn.admin` |
| `/clearwarnings <jugador>` | Borrar todas las advertencias | `iron.warn.admin` |
| `/jail <jugador> [razón]` | Aísla a un jugador | `iron.jail.use` |
| `/unjail <jugador>` | Libera a un jugador | `iron.jail.use` |
| `/setjail` | Establecer ubicación de la cárcel en la posición actual | `iron.jail.admin` |
| `/grant <jugador> [seg]` | Conceder permiso para hablar (PTS) | `iron.pts.grant` |
| `/promote <jugador>` | Ascender de rango | `iron.rank.promote` |
| `/demote <jugador>` | Degradar de rango | `iron.rank.demote` |
| `/division <set/remove...>` | Gestionar asignación de división | `iron.division.use` |
| `/exam <start/end...>` | Gestionar exámenes de ascenso | `iron.exam.use` |
| `/killlog [jugador] [cant]` | Ver registros detallados de PvP | `iron.killlog.view` |
| `/iron reload` | Recargar configuración | `iron.admin` |

### 🤖 Comandos de Bot de Discord (Slash Commands)
| Comando | Descripción |
|---|---|
| `/link` | Vinculación de cuenta (DM/Servidor) |
| `/settings` | Configuración del Bot / Gestión de roles |
| `/panel` | Colocar panel de gestión de vinculación/roles |
| `/promote, /demote` | Gestionar rangos (desde Discord) |
| `/division` | Gestionar divisiones |
| `/kick, /ban` | Ejecutar castigo |

## Construcción

```bash
mvn clean package
```

## Despliegue

Este proyecto está diseñado para ejecutarse en **Google Cloud Platform (GCP)** o VPS genéricos como **Xserver VPS**.

### 1. GCP (Google Cloud Platform)
Consulte la [Guía de Despliegue en GCP (Docs)](docs/GCP_DEPLOY_es_ES.md). Puede configurarlo en minutos con el script dedicado.

### 2. VPS Genérico (Xserver, ConoHa, etc.)
Consulte la [Guía de Despliegue en VPS (Docs)](docs/VPS_DEPLOY_es_ES.md). Si tiene un entorno Ubuntu, puede configurarlo con un solo script.

### 3. Soporte Bedrock (Móvil/Switch)
Consulte la [Guía de Juego Cruzado (Docs)](docs/CROSS_PLAY_es_ES.md). Logra juego cruzado usando Geyser.

## 🔄 Actualización Automática

Este proyecto soporta compilaciones automáticas a través de **GitHub Actions**.
Cuando se hace push a la rama `main`, la última versión se compila y lanza automáticamente como la etiqueta `latest` en [Releases](https://github.com/kaji11-jp/IronDiscipline/releases).

### Cómo actualizar en el servidor
Ejecute el siguiente comando una vez para completar la actualización a la última versión y reiniciar.

```bash
# Después de conectar por SSH
curl -sL https://raw.githubusercontent.com/kaji11-jp/IronDiscipline/main/scripts/update-server.sh | sudo bash
```

## Licencia

MIT License
