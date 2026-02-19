package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.modeler.AbstractModeler;
import org.bukkit.Bukkit;

import java.util.UUID;

public class DismountFlyPetFlag extends AbstractFlag implements StoppableFlag {

    private int task;

    public static String NAME = "mcpets-dismount-flying";

    public DismountFlyPetFlag(MCPets instance) {
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
        }
        else {
            MCPets.getLog().info(MCPets.getLogName() + "Starting flag " + getFlagName() + ".");
        }

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), () -> {
            if (MCPets.getMythicMobs() == null)
                return;

            for (UUID owner : Pet.getActivePets().keySet()) {
                Pet pet = Pet.getActivePets().get(owner);

                if (!pet.isMountable())
                    continue;

                AbstractModeler modeler = MCPets.getModeler();
                modeler.dismountFlying(pet, owner, this::testState);
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(task);
    }
}
