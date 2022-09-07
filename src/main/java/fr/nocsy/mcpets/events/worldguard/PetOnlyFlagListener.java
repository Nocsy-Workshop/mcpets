package fr.nocsy.mcpets.events.worldguard;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.flags.AbstractFlag;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.flags.PetOnlyFlag;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PetOnlyFlagListener implements Listener {

    @EventHandler
    public void spawn(EntitySpawnEvent e)
    {
        Entity ent = e.getEntity();

        Location loc = e.getLocation();

        AbstractFlag flag = FlagsManager.getFlag(PetOnlyFlag.getName());

        if(flag == null)
            return;
        // If we can only spawn pets, then we cancel the spawn of ones who are not pets
        if(flag.testState(loc))
        {
            new BukkitRunnable() {
                @Override
                public void run() {
                    boolean isMythicMob = MCPets.getMythicMobs().getMobManager().isActiveMob(BukkitAdapter.adapt(ent));
                    if(isMythicMob)
                    {
                        ent.remove();
                    }
                }
            }.runTaskLater(MCPets.getInstance(), 1L);

        }

    }

}
