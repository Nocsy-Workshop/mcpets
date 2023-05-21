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

    public DespawnPetFlag(MCPets instance) {
        super(NAME, false, instance);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void launch() {
        if (getFlag() == null) {
            MCPets.getLog().warning(MCPets.getLogName() + "Flag " + getFlagName() + " couldn't not be launched as it's null. Please contact Nocsy.");
            return;
        } else {
            MCPets.getLog().info(MCPets.getLogName() + "Starting flag " + getFlagName() + ".");
        }

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), new Runnable() {
            @Override
            public void run() {

                if(MCPets.getMythicMobs() == null)
                    return;

                try
                {
                    for (UUID owner : Pet.getActivePets().keySet()) {
                        Pet pet = Pet.getActivePets().get(owner);
                        Player p = Bukkit.getPlayer(owner);

                        if (p != null) {
                            boolean hasToBeRemoved = testState(p.getLocation());

                            if (hasToBeRemoved) {
                                pet.despawn(PetDespawnReason.FLAG);
                                Language.CANT_FOLLOW_HERE.sendMessage(p);
                            }

                        }

                    }
                }
                catch (ConcurrentModificationException ignored)
                {}


            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(task);
    }
}
