# MCPets-Velocity

A companion plugin for the Velocity proxy that keeps a player's active pet in sync across all servers in your network. When a player switches servers, their pet is automatically despawned on the source server and respawned on the destination server. Revoking a pet on any server is also reflected network-wide.

---

## Requirements

| | Version |
|---|---|
| Velocity | 3.x |
| MCPets | This fork (installed on each synced server) |
| MySQL | One shared database accessible by all synced servers |
| Java | 11 or higher |

---

## Installation

### Step 1 - Velocity Proxy

1. Drop `MCPets-Velocity-<version>.jar` into your Velocity proxy's `plugins/` folder.
2. Start or restart the proxy. A config file will be created at `plugins/mcpets-velocity/config.yml`.
3. Open the config and list the backend server names that should participate in pet sync:

```yaml
# Names must exactly match the server names defined in velocity.toml under [servers]
synced-servers:
  - skyblock-1
  - skyblock-2
  - skyblock-3
```

Leaving `synced-servers` empty will sync pets across **all** servers on the proxy.

### Step 2 - Each Synced Backend Server

On every server that should participate in pet sync:

1. Make sure MCPets from this fork is installed.
2. Open `plugins/MCPets/config.yml` and configure MySQL to point at your shared database:

```yaml
DisableMySQL: false

MySQL:
  Host: your.db.host
  Port: 3306
  Database: mcpets_db
  User: youruser
  Password: yourpassword
  Prefix: ""
```

3. Enable Velocity sync in the same config file:

```yaml
Velocity:
  Enabled: true
  SwitchWindow: 60
```

4. Restart the server.

> **All synced servers must connect to the same MySQL database.** This is how pet state is shared between them.

---

## Configuration Reference

### Velocity Proxy (`plugins/mcpets-velocity/config.yml`)

| Key | Type | Default | Description |
|---|---|---|---|
| `synced-servers` | list | `[]` | Server names to sync pets between. Leave empty to sync all servers. |

### MCPets Backend (`plugins/MCPets/config.yml`)

| Key | Type | Default | Description |
|---|---|---|---|
| `Velocity.Enabled` | boolean | `false` | Set to `true` on every synced server to enable cross-server pet sync. |
| `Velocity.SwitchWindow` | integer (seconds) | `60` | How long after a player quits their active pet record is considered valid for restore. Increase this if players are timing out during server switches. |

---

## How It Works

When a player switches to a synced server, the source server writes the player's active pet to a shared `mcpets_active_pet` table in MySQL before handing off the connection. The Velocity proxy notifies the destination server that a pet-carrying player is on the way. When the player arrives, the destination server reads the record and spawns the correct pet.

If a player revokes their pet, the database record is cleared immediately. The destination server will find no record and will not spawn anything.

If the Velocity notification does not arrive for any reason, the destination server will fall back to the `SwitchWindow` setting. Any record written within that time window is still treated as a valid switch and the pet will be restored.

---

## Troubleshooting

**Pet does not appear after switching servers**
- Confirm `Velocity.Enabled: true` is set in `config.yml` on the destination server, not just the source.
- Confirm all synced servers are pointing at the same MySQL database and that `DisableMySQL: false`.
- Confirm the server names listed in `synced-servers` exactly match the names defined in `velocity.toml`.
- Check the destination server console for any errors from MCPets when the player joins.

**Pet still appears after being revoked**
- Ensure all synced servers are running the same version of this MCPets fork. Older builds did not clear the database record on revoke.

**The wrong pet is spawned**
- Pet IDs are looked up from each server's own pet config files. Make sure all synced servers have identical pet configuration files so that the same pet ID resolves to the same pet on every server.

---

## Building from Source

From the `mcpets-velocity/` directory:

```bash
mvn clean package
```

The output jar will be at `target/MCPets-Velocity-<version>.jar`.
