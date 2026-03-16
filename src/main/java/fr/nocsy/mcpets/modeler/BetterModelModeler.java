package fr.nocsy.mcpets.modeler;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.modeler.bone.AbstractNameTag;
import fr.nocsy.mcpets.modeler.bone.BetterModelNameTag;
import fr.nocsy.mcpets.modeler.listeners.BetterModelListeners;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.nms.ModelNametag;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BetterModelModeler implements AbstractModeler {

    private BetterModelListeners listeners;

    @Override
    public boolean mountDriver(UUID petUUID, Entity rider, String mountType) {
        // BetterModel uses Bukkit's vanilla passenger system for mounting
        Entity petEntity = Bukkit.getEntity(petUUID);
        if (petEntity == null)
            return false;

        if (rider.getVehicle() != null)
            rider.getVehicle().eject();

        petEntity.addPassenger(rider);
        return true;
    }

    @Override
    public boolean hasMount(UUID petUUID, Entity rider) {
        Entity petEntity = Bukkit.getEntity(petUUID);
        if (petEntity == null)
            return false;
        return petEntity.getPassengers().contains(rider);
    }

    @Override
    public void dismountRider(UUID petUUID, Entity rider) {
        Entity petEntity = Bukkit.getEntity(petUUID);
        if (petEntity == null)
            return;
        petEntity.removePassenger(rider);
    }

    @Override
    public void dismountAll(UUID petUUID) {
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
        return null;
    }

    @Override
    public boolean supportsMount(String mountType) {
        // BetterModel uses vanilla mounting, all mount types are accepted
        return mountType != null && !mountType.isEmpty();
    }

    @Override
    public void handleVanillaDismount(UUID petUUID, Entity rider) {
        // BetterModel uses vanilla dismount - nothing extra to do
    }

    @Override
    public boolean isFlyingMount(Pet pet, UUID owner) {
        // Check mount type string for "fly" keyword
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
}
