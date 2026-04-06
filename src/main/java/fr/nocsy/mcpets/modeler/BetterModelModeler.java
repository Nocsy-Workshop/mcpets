package fr.nocsy.mcpets.modeler;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.modeler.bone.AbstractNameTag;
import fr.nocsy.mcpets.modeler.bone.BetterModelNameTag;
import fr.nocsy.mcpets.modeler.listeners.BetterModelListeners;
import fr.nocsy.mcpets.utils.debug.Debugger;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.bukkit.platform.BukkitEntity;
import kr.toxicity.model.api.mount.MountControllers;
import kr.toxicity.model.api.nms.HitBox;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BetterModelModeler implements AbstractModeler {

    private BetterModelListeners listeners;

    /**
     * Finds the first HitBox from the registry's trackers.
     * Note: canMount() is intentionally NOT checked here because mountDriver()
     * will overwrite the mountController right after finding the HitBox.
     * BBModel files that lack an explicit mount tag default to MountControllers.INVALID,
     * whose canMount() returns false — filtering it out would prevent mounting entirely.
     */
    private HitBox findMountableHitBox(EntityTrackerRegistry registry) {
        for (EntityTracker tracker : registry.trackers()) {
            for (RenderedBone bone : tracker.bones()) {
                HitBox hb = bone.getHitBox();
                if (hb != null) {
                    return hb;
                }
            }
        }
        return null;
    }

    @Override
    public boolean mountDriver(UUID petUUID, Entity rider, String mountType) {
        EntityTrackerRegistry registry = BetterModel.registryOrNull(petUUID);

        if (rider.getVehicle() != null)
            rider.getVehicle().eject();

        if (registry != null) {
            HitBox hitBox = findMountableHitBox(registry);
            if (hitBox != null) {
                // Configure the mount controller based on mount type
                MountControllers base = resolveMountController(mountType);
                hitBox.mountController(base.modifier()
                        .canBeDamagedByRider(false)
                        .build());
                hitBox.mount(new BukkitEntity(rider));
                Debugger.send("§a[BetterModel] Mounted " + rider.getName()
                        + " on pet " + petUUID + " via HitBox (controller: " + base.name() + ")");
                return true;
            }else{
                Debugger.send("§e[BetterModel] No mountable HitBox found for pet " + petUUID + " when trying to mount driver " + rider.getName());
            }
        }else{
            Debugger.send("§e[BetterModel] No registry found for pet " + petUUID + " when trying to mount driver " + rider.getName());
        }

        // Fallback to vanilla passengers if no HitBox is available
        Entity petEntity = Bukkit.getEntity(petUUID);
        if (petEntity == null)
            return false;
        petEntity.addPassenger(rider);
        Debugger.send("§e[BetterModel] Mounted " + rider.getName()
                + " on pet " + petUUID + " via vanilla fallback (no HitBox found)");
        return true;
    }

    @Override
    public boolean hasMount(UUID petUUID, Entity rider) {
        EntityTrackerRegistry registry = BetterModel.registryOrNull(petUUID);
        if (registry != null) {
            return registry.mountedHitBox().containsKey(rider.getUniqueId());
        }
        // Fallback to vanilla check
        Entity petEntity = Bukkit.getEntity(petUUID);
        if (petEntity == null)
            return false;
        return petEntity.getPassengers().contains(rider);
    }

    @Override
    public void dismountRider(UUID petUUID, Entity rider) {
        EntityTrackerRegistry registry = BetterModel.registryOrNull(petUUID);
        if (registry != null) {
            EntityTrackerRegistry.MountedHitBox mounted = registry.mountedHitBox().get(rider.getUniqueId());
            if (mounted != null) {
                mounted.dismount();
                Debugger.send("§a[BetterModel] Dismounted " + rider.getName()
                        + " from pet " + petUUID + " via HitBox");
                return;
            }
        }
        // Fallback to vanilla
        Entity petEntity = Bukkit.getEntity(petUUID);
        if (petEntity == null)
            return;
        petEntity.removePassenger(rider);
    }

    @Override
    public void dismountAll(UUID petUUID) {
        EntityTrackerRegistry registry = BetterModel.registryOrNull(petUUID);
        if (registry != null && registry.hasPassenger()) {
            for (EntityTrackerRegistry.MountedHitBox mounted : registry.mountedHitBox().values()) {
                mounted.dismountAll();
            }
            Debugger.send("§a[BetterModel] Dismounted all riders from pet " + petUUID);
            return;
        }
        // Fallback to vanilla
        Entity petEntity = Bukkit.getEntity(petUUID);
        if (petEntity == null)
            return;
        petEntity.eject();
    }

    @Override
    public void removeModel(UUID petUUID) {
        EntityTrackerRegistry registry = BetterModel.registryOrNull(petUUID);
        if (registry == null)
            return;
        registry.close();
        Debugger.send("§a[BetterModel] Removed model for pet " + petUUID);
    }

    @Override
    public AbstractNameTag getNameTag(UUID petUUID) {
        EntityTrackerRegistry registry = BetterModel.registryOrNull(petUUID);
        if (registry == null)
            return null;

        for (EntityTracker tracker : registry.trackers()) {
            RenderedBone bone = tracker.bone("name");
            if (bone != null && bone.getNametag() != null) {
                return new BetterModelNameTag(bone.getNametag());
            }
        }
        Debugger.send("§e[BetterModel] No nametag bone found for pet " + petUUID);
        return null;
    }

    @Override
    public boolean supportsMount(String mountType) {
        return mountType != null && !mountType.isEmpty();
    }

    @Override
    public void handleVanillaDismount(UUID petUUID, Entity rider) {
        // Intentionally empty: BetterModel's DismountModelEvent handles despawnOnDismount
        // via BetterModelListeners.onDismount(). Adding logic here would cause double triggers.
    }

    @Override
    public boolean isFlyingMount(Pet pet, UUID owner) {
        if (pet.getActiveMob() == null)
            return false;

        UUID uuid = pet.getActiveMob().getUniqueId();
        EntityTrackerRegistry registry = BetterModel.registryOrNull(uuid);
        if (registry == null || !registry.hasPassenger())
            return false;

        String mountType = pet.getMountType();
        return mountType != null && mountType.toUpperCase().contains("FLY");
    }

    @Override
    public void registerListeners(JavaPlugin plugin) {
        listeners = new BetterModelListeners();
        listeners.register();
    }

    @Override
    public void unregisterListeners() {
        if (listeners != null) {
            listeners.unregister();
            listeners = null;
        }
    }

    /**
     * Resolves the BetterModel MountController enum from the pet mount type string.
     */
    private static MountControllers resolveMountController(String mountType) {
        if (mountType == null || mountType.isEmpty())
            return MountControllers.WALK;
        String upper = mountType.toUpperCase();
        if (upper.contains("FLY"))
            return MountControllers.FLY;
        return MountControllers.WALK;
    }
}
