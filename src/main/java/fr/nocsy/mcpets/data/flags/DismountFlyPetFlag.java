package fr.nocsy.mcpets.data.flags;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.modeler.AbstractModeler;
import org.bukkit.Bukkit;

import java.util.UUID;

public class DismountFlyPetFlag extends AbstractFlag implements StoppableFlag {

    private WrappedTask task;

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

        task = MCPets.getScheduler().runTimer(() -> {
            if (MCPets.getMythicMobs() == null)
                return;

            for (UUID owner : Pet.getActivePets().keySet()) {
                Pet pet = Pet.getActivePets().get(owner);
                MCPets.getScheduler().runAtEntity(pet.getActiveMob().getEntity().getBukkitEntity(), (t) -> {
                    if (!pet.isMountable())
                        return;

                    AbstractModeler modeler = MCPets.getModeler();
                    modeler.dismountFlying(pet, owner, this::testState);
                });
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        task.cancel();
    }
}
