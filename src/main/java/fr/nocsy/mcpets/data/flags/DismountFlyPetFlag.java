package fr.nocsy.mcpets.data.flags;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
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
            MCPets.getLog().warning(MCPets.getLogName() + "标志 " + getFlagName() + " 无法启动,因为它为空.请联系Nocsy.");
            return;
        } else {
            MCPets.getLog().info(MCPets.getLogName() + "正在启动标志 " + getFlagName() + ".");
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

                    ModeledEntity model = ModelEngineAPI.getModeledEntity(pet.getActiveMob().getUniqueId());
                    if(model == null)
                        continue;
                    MountManager mountManager = model.getMountData().getMainMountManager();
                    if(model.getMountData() == null ||
                            model.getMountData().getMainMountManager() ==  null ||
                            model.getMountData().getMainMountManager().getType() == null)
                        continue;

                    String name = model.getMountData().getMainMountManager().getType().getId();

                    if(name == null || !name.toUpperCase().contains("FLY"))
                        continue;

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
