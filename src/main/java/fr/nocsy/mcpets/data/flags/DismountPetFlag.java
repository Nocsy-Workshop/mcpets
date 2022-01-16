package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DismountPetFlag extends AbstractFlag implements StoppableFlag {

    public DismountPetFlag(MCPets instance) {
        super("dismountPet", false, instance);
    }

    @Override
    public void register()
    {
        super.register();
        launch();
    }

    private int task;
    private void launch()
    {
        if(getFlag() == null)
        {
            MCPets.getLog().warning(MCPets.getLogName() + "Flag " + getFlagName() + " couldn't not be launched as it's null. Please contact Nocsy.");
            return;
        }
        else
        {
            MCPets.getLog().info(MCPets.getLogName() + "Starting flag " + getFlagName() + ".");
        }

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), new Runnable() {
            @Override
            public void run() {

                for(UUID owner : Pet.getActivePets().keySet())
                {
                    Pet pet = Pet.getActivePets().get(owner);

                    if(!pet.isMountable())
                        continue;

                    Player p = Bukkit.getPlayer(owner);

                    if(p != null)
                    {
                        if(!pet.hasMount(p))
                            continue;

                        boolean hasToBeEjected = testState(p);

                        if(hasToBeEjected)
                        {
                            pet.dismount(p);
                            Language.NOT_MOUNTABLE_HERE.sendMessage(p);
                        }

                    }

                }

            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(task);
    }

}
