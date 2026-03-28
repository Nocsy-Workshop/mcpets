package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ConcurrentModificationException;
import java.util.UUID;

public class DespawnPetFlag extends AbstractFlag implements StoppableFlag {

    int task;

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
        }
        else {
            MCPets.getLog().info("Starting flag " + getFlagName() + ".");
        }

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), () -> {
            if (MCPets.getMythicMobs() == null)
                return;

            try {
                for (final UUID owner : Pet.getActivePets().keySet()) {
                    for (final Pet pet : Pet.getActivePetsForOwner(owner)) {
                        final Player p = Bukkit.getPlayer(owner);

                        if (p != null) {
                            final boolean hasToBeRemoved = testState(p.getLocation());

                            if (hasToBeRemoved) {
                                pet.despawn(PetDespawnReason.FLAG);
                                Language.CANT_FOLLOW_HERE.sendMessage(p);
                            }
                        }
                    }
                }
            }
            catch (final ConcurrentModificationException ignored) {}
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(task);
    }
}
