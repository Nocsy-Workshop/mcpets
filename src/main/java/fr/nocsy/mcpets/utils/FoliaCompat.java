package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class FoliaCompat {

    private static final boolean IS_FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
        }
        IS_FOLIA = folia;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public static void runGlobal(Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().execute(MCPets.getInstance(), runnable);
        } else {
            Bukkit.getScheduler().runTask(MCPets.getInstance(), runnable);
        }
    }

    public static void runGlobalLater(Runnable runnable, long delay) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runDelayed(MCPets.getInstance(), task -> runnable.run(), Math.max(1, delay));
        } else {
            Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), runnable, delay);
        }
    }

    public static void runEntity(Entity entity, Runnable runnable) {
        if (IS_FOLIA) {
            entity.getScheduler().execute(MCPets.getInstance(), runnable, null, 1L);
        } else {
            Bukkit.getScheduler().runTask(MCPets.getInstance(), runnable);
        }
    }

    public static void runEntityLater(Entity entity, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            entity.getScheduler().runDelayed(MCPets.getInstance(), task -> runnable.run(), null, Math.max(1, delay));
        } else {
            Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), runnable, delay);
        }
    }

    public static void runLocation(Location location, Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().execute(MCPets.getInstance(), location, runnable);
        } else {
            Bukkit.getScheduler().runTask(MCPets.getInstance(), runnable);
        }
    }

    public static void runLocationLater(Location location, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().runDelayed(MCPets.getInstance(), location, task -> runnable.run(), Math.max(1, delay));
        } else {
            Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), runnable, delay);
        }
    }

    public static void runAsync(Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(MCPets.getInstance(), task -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), runnable);
        }
    }
}
