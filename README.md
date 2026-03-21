<img src="https://i.ibb.co/7RYYYZb/spigot-mcpets-banner.png" alt="MC Pets logo">

Welcome to MC Pets!

Navigate through the [wiki pages](https://mcpets.gitbook.io/mcpets/) to find whatever information you need about the plugin.
Looking for help ? Join the [Discord](https://discord.com/invite/p7QTm2gUyf) !

<img src="https://i.imgur.com/saPEOAJ.png" alt="Requirements">

✨ Check the [requirements on the Wiki](https://mcpets.gitbook.io/mcpets/common-issues/common-issues/requirements).

<img src="https://i.imgur.com/KadwjCO.png" alt="Features">

⭕ **3D modeled pets** with ANY behavior, skills, effects and more based on MythicMobs and ModelEngine

⭕ Create **mounts** using ModelEngine and implement them easily with MCPets

⭕ Unique **inventory** per pet

⭕ **Pet statistics** with MCPets 3.0.0 : experience, health, damage modifiers for skills and more

⭕ **Taming**, **evolutions** and **pet food** system with MCPets 3.0.0

⭕ Customizable **in-game GUI interface** : summon pets, show their stats, custom names, mount, skins, inventory... Organizable in categories

⭕ **Give orders** to your pets using the Signal Stick

⭕ Permission-based system and **customizable permission for each pet**, showing the pet or not in the GUI depending on the player having the permission for it

⭕ **Flags** to manage pet interactions with WorldGuard

⭕ MySQL support

⭕ **Velocity cross-server pet sync** — active pets follow players seamlessly between servers on a Velocity network *(see [mcpets-velocity/README.md](mcpets-velocity/README.md))*

✨ Need a demo pet to start with ? Check out [Sleepy the Otter](https://mcmodels.net/model/sleepy-the-otter/) !

---

## Velocity Cross-Server Sync

This fork adds a companion Velocity proxy plugin (`mcpets-velocity/`) that keeps a player's active pet consistent across all synced servers in your network.

**How it works:**
- When a player switches to a synced server, their active pet is saved to the shared MySQL database and automatically spawned on the destination server
- Revoking a pet on any server clears the record network-wide — no ghost spawns
- Only servers listed under `synced-servers` in the Velocity config participate; all other servers are unaffected

**Quick setup:**
1. Enable MySQL in MCPets and point all synced servers at the same database
2. Set `Velocity.Enabled: true` in MCPets `config.yml` on each synced server
3. Drop `MCPets-Velocity-<version>.jar` into your Velocity proxy's `plugins/` folder
4. Configure which servers to sync in `plugins/mcpets-velocity/config.yml` on the proxy

See [mcpets-velocity/README.md](mcpets-velocity/README.md) for full setup instructions and configuration options.

---

<img src="https://i.ibb.co/Sn460M4/patreon-advantages.png" alt="Patreon advantages">

⭕ Download some exclusive content and monthly releases on the [Patreon](https://www.patreon.com/tofnocsy_workshop)

⭕ Get involved in the creation progress by picking your favorite model in a monthly selection

⭕ Access a patron-only channel on my [Discord's workshop](https://discord.gg/p7QTm2gUyf)

# ✨ Partner ✨

Download more pets on [MCModels.net](https://mcmodels.net/)

![image](https://cdn.discordapp.com/attachments/884364895108366336/909534639650136064/partnered.png)

