package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Detects players ending up clipped inside a solid block right after mounting
 * a pet (mount wall-clip exploit) and rolls them back to the location they
 * were at when they triggered the mount.
 */
public class MountWallClipListener implements Listener {

    @EventHandler
    public void onMount(final EntityMountEvent event) {
        if (!(event.getEntity() instanceof final Player player))
            return;

        final Pet pet = Pet.getFromEntity(event.getMount());
        if (pet == null)
            return;

        final Location safeLoc = player.getLocation().clone();

        // If the player was already inside a solid block before mounting,
        // don't trust this position as a rollback target.
        if (!Utils.isLocationClearForMount(safeLoc))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead())
                    return;
                if (Utils.isLocationClearForMount(player.getLocation()))
                    return;

                pet.dismount(player);
                player.teleport(safeLoc);
                Language.NOT_MOUNTABLE_HERE.sendMessage(player);
            }
        }.runTaskLater(MCPets.getInstance(), 2L);
    }
}
