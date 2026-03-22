package fr.nocsy.mcpets.modeler;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import com.ticxo.modelengine.api.mount.controller.MountControllerType;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.modeler.bone.AbstractNameTag;
import fr.nocsy.mcpets.modeler.bone.ModelEngineNameTag;
import fr.nocsy.mcpets.modeler.listeners.ModelEngineListeners;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class ModelEngineModeler implements AbstractModeler {

    private ModelEngineListeners listeners;

    @Override
    public boolean mountDriver(UUID petUUID, Entity rider, String mountType) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null)
            return false;

        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager == null)
            return false;

        MountControllerType controllerType = (MountControllerType) ModelEngineAPI.getMountControllerTypeRegistry().get(mountType);

        if (rider.getVehicle() != null)
            rider.getVehicle().eject();
        if (mountManager.getDriver() != null)
            mountManager.dismountDriver();

        mountManager.mountDriver(rider, controllerType, mountController -> {
            mountController.setCanDamageMount(false);
        });
        return true;
    }

    @Override
    public boolean hasMount(UUID petUUID, Entity rider) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null)
            return false;

        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager == null)
            return false;

        return mountManager.getDriver() != null
                && mountManager.getDriver().getUniqueId().equals(rider.getUniqueId());
    }

    @Override
    public void dismountRider(UUID petUUID, Entity rider) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null)
            return;

        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager == null)
            return;

        mountManager.dismountRider(rider);
    }

    @Override
    public void dismountAll(UUID petUUID) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null)
            return;

        if (model.getMountData() == null)
            return;

        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager != null) {
            mountManager.dismountAll();
        }
    }

    @Override
    public void removeModel(UUID petUUID) {
        ModelEngineAPI.removeModeledEntity(petUUID);
    }

    @Override
    public AbstractNameTag getNameTag(UUID petUUID) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null)
            return null;

        if (model.getModels().isEmpty())
            return null;

        Optional<ActiveModel> opt = model.getModels().values().stream().findFirst();
        if (opt.isEmpty())
            return null;

        ActiveModel activeModel = opt.get();
        ModelBone bone = activeModel.getBone("name")
                .stream()
                .filter(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG).orElse(null) != null)
                .findFirst().orElse(null);

        if (bone == null)
            return null;

        NameTag nameTag = bone.getBoneBehavior(BoneBehaviorTypes.NAMETAG).orElse(null);
        if (nameTag == null)
            return null;

        return new ModelEngineNameTag(nameTag);
    }

    @Override
    public boolean supportsMount(String mountType) {
        return ModelEngineAPI.getMountControllerTypeRegistry().get(mountType) != null;
    }

    @Override
    public void handleVanillaDismount(UUID petUUID, Entity rider) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null || model.getMountData() == null)
            return;

        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager == null)
            return;

        var driver = mountManager.getDriver();
        if (driver == null) {
            mountManager.dismountDriver();
            return;
        }

        if (driver.getUniqueId().equals(rider.getUniqueId()))
            mountManager.dismountAll();
        else
            mountManager.dismountRider(rider);
    }

    @Override
    public boolean isFlyingMount(Pet pet, UUID owner) {
        if (pet.getActiveMob() == null)
            return false;

        UUID uuid = pet.getActiveMob().getUniqueId();
        ModeledEntity model = ModelEngineAPI.getModeledEntity(uuid);
        if (model == null || model.getMountData() == null)
            return false;

        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager == null || !mountManager.hasRiders())
            return false;

        String mountType = pet.getMountType();
        return mountType != null && mountType.toUpperCase().contains("FLY");
    }

    @Override
    public void registerListeners(JavaPlugin plugin) {
        listeners = new ModelEngineListeners();
        plugin.getServer().getPluginManager().registerEvents(listeners, plugin);
    }

    @Override
    public void unregisterListeners() {
        if (listeners != null) {
            HandlerList.unregisterAll(listeners);
            listeners = null;
        }
    }
}
