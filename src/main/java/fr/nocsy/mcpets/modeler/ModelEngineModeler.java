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
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.modeler.bone.AbstractNameTag;
import fr.nocsy.mcpets.modeler.bone.ModelEngineNameTag;
import fr.nocsy.mcpets.modeler.listeners.ModelEngineListeners;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class ModelEngineModeler extends AbstractModeler {

    public ModelEngineModeler() {
        super(new ModelEngineListeners());
    }

    @Override
    public void removeModeledEntity(UUID uuid) {
        ModelEngineAPI.removeModeledEntity(uuid);
    }

    @Override
    public void dismountAll(UUID uuid) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(uuid);
        if (model != null && model.getMountData() != null) {
            MountManager mountManager = model.getMountData().getMainMountManager();
            if (mountManager != null) {
                mountManager.dismountAll();
            }
        }
    }

    @Override
    public void dismount(UUID uuid, Entity dismounter) {
        ModeledEntity localModeledEntity = ModelEngineAPI.getModeledEntity(uuid);
        if (localModeledEntity == null || localModeledEntity.getMountData() == null) {
            return;
        }

        var mountManager = localModeledEntity.getMountData().getMainMountManager();
        if (mountManager == null)
            return;
        var driver = mountManager.getDriver();
        if (driver == null) {
            mountManager.dismountDriver();
            return;
        }

        if (driver.getUniqueId().equals(dismounter.getUniqueId()))
            mountManager.dismountAll();
        else
            mountManager.dismountRider(dismounter);
    }

    @Override
    public boolean addPassenger(ActiveMob activeMob, Entity ent, String mountType) {
        UUID petUUID = activeMob.getEntity().getUniqueId();
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null) {
            activeMob.getEntity().getBukkitEntity().addPassenger(ent);
            return false;
        }

        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager == null)
            return false;

        MountControllerType controllerType = (MountControllerType)ModelEngineAPI.getMountControllerTypeRegistry().get(mountType);

        if (ent.getVehicle() != null)
            ent.getVehicle().eject();
        if (mountManager.getDriver() != null)
            mountManager.dismountDriver();
        try {
            mountManager.mountDriver(ent, controllerType);
            mountManager.mountDriver(ent, controllerType, mountController -> {
                mountController.setCanDamageMount(false);
                //mountController.setCanInteractMount(false);
            });
        }
        catch(IllegalStateException ex) {
            Language.ALREADY_MOUNTING.sendMessageFormated(ent);
        }
        return true;
    }

    @Override
    public boolean supportsMount(String mountType) {
        return ModelEngineAPI.getMountControllerTypeRegistry().get(mountType) != null;
    }

    @Override
    public boolean isPassenger(UUID petUUID, Entity entity) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(petUUID);
        if (model == null) {
            return false;
        }
        MountManager mountManager = model.getMountData().getMainMountManager();
        if (mountManager == null)
            return false;

        return mountManager.getDriver() != null && mountManager.getDriver().getUniqueId().equals(entity.getUniqueId());
    }


    @Nullable
    @Override
    public AbstractNameTag getNameTag(UUID uuid) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(uuid);
        if (model == null) {
            return null;
        }
        if (model.getModels().isEmpty()) {
            return null;
        }

        Optional<ActiveModel> opt = model.getModels().values().stream().findFirst();
        ActiveModel activeModel = null;
        if(opt.isPresent())
            activeModel = opt.get();
        else
            return null;

        ModelBone bone = activeModel.getBone("name")
                .stream()
                .filter(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG).orElse(null) != null)
                .findFirst().orElse(null);

        if (bone == null)
            return null;

        NameTag nameTag = bone.getBoneBehavior(BoneBehaviorTypes.NAMETAG).orElse(null);
        if (nameTag == null) return null;
        return new ModelEngineNameTag(nameTag);
    }

    @Override
    public void dismountFlying(Pet pet, UUID owner, Predicate<Location> predicate) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(pet.getActiveMob().getUniqueId());
        if (model == null)
            return;
        MountManager mountManager = model.getMountData().getMainMountManager();
        if (model.getMountData() == null ||
                model.getMountData().getMainMountManager() ==  null ||
                model.getMountData().getMainMountManager().getType() == null)
            return;
        if (!mountManager.hasRiders())
            return;

        try {
            String controllerClass = ModelEngineAPI.getMountPairManager().getController(owner).getClass().getSimpleName();
            String petMountType = pet.getMountType();
            String type = petMountType + " " + model.getMountData().getMainMountManager().getType().getId() + " " + controllerClass;
            if(!type.toUpperCase().contains("FLY"))
                return;
        }
        catch (Exception e) {
            return;
        }

        Player p = Bukkit.getPlayer(owner);
        if (p != null) {
            if (!pet.hasMount(p))
                return;

            boolean hasToBeEjected = predicate.test(p.getLocation());

            if (hasToBeEjected) {
                pet.dismount(p);
                Language.NOT_MOUNTABLE_HERE.sendMessage(p);
            }
        }
    }
}
