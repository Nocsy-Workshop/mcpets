package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.FoliaCompat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ConcurrentModificationException;
import java.util.UUID;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;

public class DespawnPetFlag extends AbstractFlag implements StoppableFlag {

    private Object task;

    public static String NAME = "mcpets-despawn";

    public DespawnPetFlag(final MCPets instance) {
        super(NAME, false, instance);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void launch() {
        if (getFlag() == null) {
            MCPets.getLog().warning("Flag " + getFlagName() + " couldn't not be launched as it's null. Please contact Nocsy.");
            return;
        } else {
            MCPets.getLog().info("Starting flag " + getFlagName() + ".");
        }

        Runnable runnable = () -> {
            if (MCPets.getMythicMobs() == null)
                return;

            try {
                for (final UUID owner : Pet.getActivePets().keySet()) {
                    for (final Pet pet : Pet.getActivePetsForOwner(owner)) {
                        final Player p = Bukkit.getPlayer(owner);

                        if (p != null) {
                            FoliaCompat.runEntity(p, () -> {
                                final boolean hasToBeRemoved = testState(p.getLocation());

                                if (hasToBeRemoved) {
                                    pet.despawn(PetDespawnReason.FLAG);
                                    Language.CANT_FOLLOW_HERE.sendMessage(p);
                                }
                            });
                        }
                    }
                }
            } catch (final ConcurrentModificationException ignored) {
            }
        };

        if (FoliaCompat.isFolia()) {
            task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(getMCPetsInstance(), t -> runnable.run(), 1L, 20L);
        } else {
            task = Bukkit.getServer().getScheduler().runTaskTimer(getMCPetsInstance(), runnable, 0L, 20L);
        }
    }

    @Override
    public void stop() {
        if (task != null) {
            if (FoliaCompat.isFolia()) {
                ((ScheduledTask) task).cancel();
            } else {
                ((BukkitTask) task).cancel();
            }
            task = null;
        }
    }
}
