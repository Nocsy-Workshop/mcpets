package fr.nocsy.mcpets.data.flags;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DismountPetFlag extends AbstractFlag implements StoppableFlag {

    private WrappedTask task;

    public static String NAME = "mcpets-dismount";

    public DismountPetFlag(MCPets instance) {
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

                if (!pet.isMountable())
                    continue;

                Player p = Bukkit.getPlayer(owner);

                if (p != null) {
                    MCPets.getScheduler().runAtEntity(p, (t) -> {
                        if (!pet.hasMount(p))
                            return;

                        boolean hasToBeEjected = testState(p.getLocation());

                        if (hasToBeEjected) {
                            pet.dismount(p);
                            Language.NOT_MOUNTABLE_HERE.sendMessage(p);
                        }
                    });
                }
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        task.cancel();
    }
}
