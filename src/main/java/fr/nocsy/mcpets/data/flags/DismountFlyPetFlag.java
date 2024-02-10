package fr.nocsy.mcpets.data.flags;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        } else {
            MCPets.getLog().info(MCPets.getLogName() + "Starting flag " + getFlagName() + ".");
        }

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), new Runnable() {
            @Override
            public void run() {

                if(MCPets.getMythicMobs() == null)
                    return;

                for (UUID owner : Pet.getActivePets().keySet()) {
                    Pet pet = Pet.getActivePets().get(owner);

                    if (!pet.isMountable())
                        continue;

                    UUID uuid = pet.getActiveMob().getUniqueId();
                    ModeledEntity model = ModelEngineAPI.getModeledEntity(uuid);
                    if(model == null)
                        continue;
                    MountManager mountManager = model.getMountData().getMainMountManager();
                    if(model.getMountData() == null ||
                            model.getMountData().getMainMountManager() ==  null ||
                            model.getMountData().getMainMountManager().getType() == null)
                        continue;
                    if(!mountManager.hasRiders())
                        continue;

                    try
                    {
                        String controllerClass = ModelEngineAPI.getMountPairManager().getController(owner).getClass().getSimpleName();
                        String petMountType = pet.getMountType();
                        String type = petMountType + " " + model.getMountData().getMainMountManager().getType().getId() + " " + controllerClass;
                        if(!type.toUpperCase().contains("FLY"))
                            continue;
                    }
                    catch (Exception e)
                    {
                        continue;
                    }

                    Player p = Bukkit.getPlayer(owner);

                    if (p != null) {
                        if (!pet.hasMount(p))
                            continue;

                        boolean hasToBeEjected = testState(p.getLocation());

                        if (hasToBeEjected) {
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
