package fr.nocsy.mcpets.utils;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity;

public class VolatileAIHandler {

    public static boolean navigateToLocation(AbstractEntity entity, AbstractLocation destination) {
        String version = Bukkit.getServer().getBukkitVersion();
        if (version.equals("1.18.1-R0.1-SNAPSHOT")) {
            if (!entity.isLiving())
                return false;

            EntityInsentient e = (EntityInsentient) ((CraftLivingEntity) BukkitAdapter.adapt(entity)).getHandle();
            e.D().a(destination.getX(), destination.getY(), destination.getZ(), 1.0D);
        } else {
            MythicMobs.inst().getVolatileCodeHandler().getAIHandler().navigateToLocation(entity, destination, Double.POSITIVE_INFINITY);
        }

        return true;
    }

}
