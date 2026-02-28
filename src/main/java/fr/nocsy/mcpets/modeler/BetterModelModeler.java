package fr.nocsy.mcpets.modeler;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.modeler.bone.AbstractNameTag;
import fr.nocsy.mcpets.modeler.bone.BetterModelNameTag;
import fr.nocsy.mcpets.modeler.listeners.BetterModelListeners;
import io.lumine.mythic.core.mobs.ActiveMob;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public class BetterModelModeler extends AbstractModeler {

    public BetterModelModeler() {
        super(new BetterModelListeners());
    }

    @Override
    public void removeModeledEntity(UUID uuid) {
        BetterModel.registry(uuid).ifPresent(it -> it.despawn());
    }


    @Override
    public void dismountAll(UUID uuid) {
        BetterModel.registry(uuid).ifPresent(it -> {
            it.mountedHitBox().values().forEach(hitBox -> hitBox.dismountAll());
        });
    }

    @Override
    public void dismount(UUID uuid, Entity dismounter) {
        BetterModel.registry(uuid).ifPresent(it -> {
            EntityTrackerRegistry.MountedHitBox box = it.mountedHitBox().get(dismounter.getUniqueId());
            if (box != null) {
                box.dismount();
            }
        });
    }

    @Override // TODO
    public boolean addPassenger(ActiveMob activeMob, Entity mounter, String mountType) {
        activeMob.getEntity().addPassenger(io.lumine.mythic.bukkit.BukkitAdapter.adapt(mounter));
        return true;
    }

    @Override // TODO
    public boolean supportsMount(String mountType) {
        return true;
    }

    @Override
    public boolean isPassenger(UUID uuid, Entity entity) {
        return BetterModel.registry(uuid)
                .map(registry -> registry.mountedHitBox().containsKey(entity.getUniqueId()))
                .orElse(false);
    }

    @Override
    public @Nullable AbstractNameTag getNameTag(UUID uuid) {
        return BetterModel.registry(uuid)
                .map(registry -> registry.tracker("model"))
                .map(tracker -> tracker.bone("name"))
                .filter(bone -> bone.getNametag() != null)
                .map(bone -> new BetterModelNameTag(bone.getNametag()))
                .orElse(null);
    }

    @Override
    public void dismountFlying(Pet pet, UUID owner, Predicate<Location> predicate) {
        String petMountType = pet.getMountType();
        if (petMountType == null || !petMountType.toUpperCase().contains("FLY")) {
            return;
        }

        BetterModel.registry(pet.getActiveMob().getUniqueId()).ifPresent(registry -> {
            if (registry.mountedHitBox().isEmpty()) {
                return;
            }

            Player p = Bukkit.getPlayer(owner);
            if (p == null || !pet.hasMount(p)) {
                return;
            }

            if (predicate.test(p.getLocation())) {
                pet.dismount(p);
                Language.NOT_MOUNTABLE_HERE.sendMessage(p);
            }
        });
    }
}
