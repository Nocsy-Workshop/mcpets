# MCPets-Velocity

A lightweight Velocity proxy companion plugin for [MCPets](../README.md) that keeps a player's active pet consistent across all synced servers in your network.

---

## How It Works

When a player switches between synced servers:

1. The **source server** writes the player's active pet ID and a timestamp to a shared `mcpets_active_pet` MySQL table — synchronously, before the player's connection is handed off
2. The **Velocity proxy** sends a `PLAYER_SWITCHING` plugin message to the destination server the moment it sees the switch
3. The **destination server** receives the player, reads the DB record, and spawns the correct pet automatically

If a player revokes their pet on any server, the DB record is cleared immediately. The destination server finds no record and spawns nothing — no ghost pets.

The `PLAYER_SWITCHING` message is an optimization hint. Even if it doesn't arrive (e.g. proxy issue), the destination server falls back to a configurable **switch window** (default 60 seconds): any DB record written within that window is treated as a cross-server switch and the pet is restored.

---

## Requirements

| Requirement | Version |
|---|---|
| Velocity | 3.x |
| MCPets (on each synced backend) | This fork |
| MySQL | Shared across all synced servers |
| Java | 11+ |

---

## Installation

### 1. Velocity Proxy

Drop `MCPets-Velocity-<version>.jar` into your Velocity proxy's `plugins/` folder and restart.

A config file will be generated at:
```
plugins/mcpets-velocity/config.yml
```

Edit it to list the backend server names that should participate in pet sync:

```yaml
# Server names must exactly match the names defined in velocity.toml [servers]
synced-servers:
  - skyblock-1
  - skyblock-2
  - skyblock-3
```

Leaving `synced-servers` empty syncs **all** servers on the proxy.

### 2. Each Synced Backend Server

All synced servers must:

- Use the **same MySQL database** (configured under `MySQL` in MCPets `config.yml`)
- Have Velocity sync enabled in MCPets `config.yml`:

```yaml
MySQL:
  Disable: false
  Host: your.db.host
  Port: 3306
  Database: mcpets_db
  User: youruser
  Password: yourpassword

Velocity:
  Enabled: true
  SwitchWindow: 60   # seconds — how long after a quit a DB record counts as a switch
```

> **SwitchWindow** controls how long (in seconds) a DB record is treated as a cross-server switch rather than a full disconnect. The default of 60 seconds is generous enough to cover slow switches. Increase it if your servers take longer to hand off players.

---

## Building from Source

From the `mcpets-velocity/` directory:

```bash
mvn clean package
```

Output jar: `target/MCPets-Velocity-<version>.jar`

Or build the entire project from the repo root (requires a parent `pom.xml`):

```bash
cd mcpets-velocity && mvn clean package
```

---

## Database Schema

On first startup, MCPets automatically creates the required table on the shared database:

```sql
CREATE TABLE IF NOT EXISTS mcpets_active_pet (
    uuid       VARCHAR(36)  NOT NULL,
    pet_id     VARCHAR(255) NOT NULL,
    updated_at BIGINT       NOT NULL,
    PRIMARY KEY (uuid)
);
```

No manual SQL migration is needed. If you are upgrading from an earlier version of this fork that stored the active pet inside the `names` column as `__active__`, those legacy entries are stripped silently on data load — no data loss.

---

## Configuration Reference

### Velocity proxy — `plugins/mcpets-velocity/config.yml`

| Key | Type | Default | Description |
|---|---|---|---|
| `synced-servers` | list | `[]` (all) | Backend server names to sync. Empty = sync all servers. |

### MCPets backend — `plugins/MCPets/config.yml`

| Key | Type | Default | Description |
|---|---|---|---|
| `Velocity.Enabled` | boolean | `false` | Must be `true` on every synced backend server. |
| `Velocity.SwitchWindow` | int (seconds) | `60` | How recent a DB record must be to trigger a pet restore on join. |

---

## Troubleshooting

**Pet doesn't appear on the destination server**
- Confirm `Velocity.Enabled: true` is set in MCPets `config.yml` on the destination server
- Confirm all servers share the same MySQL database and `DisableMySQL: false`
- Check that the server name in `synced-servers` exactly matches the name in `velocity.toml`
- Check the destination server console for any `[MCPets]` errors on player join

**Pet appears even after being revoked**
- Make sure you are running the latest version of MCPets from this fork on all synced servers — older builds did not clear the DB record on revoke

**Wrong pet is spawned**
- Pet IDs are resolved by name from each server's own pet config files. Ensure all synced servers have identical pet config files so that the same ID maps to the same pet everywhere.

---

## Architecture Notes

- No outgoing plugin messages are sent from the backend to the proxy — the save is synchronous JDBC, so no acknowledgement handshake is needed
- The `PLAYER_SWITCHING` message travels **proxy → backend** only, arriving before `PlayerJoinEvent` fires (sent in `ServerPreConnectEvent` at `PostOrder.LAST`)
- `reconnectionPets` (the in-memory same-server reconnect map) is intentionally bypassed when Velocity is enabled; the DB record is always authoritative
