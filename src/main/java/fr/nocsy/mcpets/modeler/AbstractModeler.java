package fr.nocsy.mcpets.modeler;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.modeler.bone.AbstractNameTag;
import fr.nocsy.mcpets.modeler.listeners.ModelListener;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public abstract class AbstractModeler {

    @Getter
    private final ModelListener listener;

    protected AbstractModeler(ModelListener listener) {
        this.listener = listener;
    }

    public abstract void removeModeledEntity(UUID uuid);

    public abstract void dismountAll(UUID uuid);

    public abstract void dismount(UUID uuid, Entity dismounter);

    public abstract boolean addPassenger(ActiveMob activeMob, Entity mounter, String mountType);

    public abstract boolean supportsMount(String mountType);

    public abstract boolean isPassenger(UUID uuid, Entity entity);

    public abstract @Nullable AbstractNameTag getNameTag(UUID uuid);

    public abstract void dismountFlying(Pet pet, UUID owner, Predicate<Location> predicate);

}
