package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;

public class PetTimer {

    @Getter
    private static HashMap<PetTimer, Object> runningTimers = new HashMap<>();

    @Getter
    private int cooldown;
    @Getter
    private int remainingTime;
    private long frequency;

    private final Runnable endingRunnable;

    public PetTimer(int cooldown, long frequency, Runnable endingRunnable) {
        this.cooldown = cooldown;
        this.remainingTime = 0;
        this.frequency = frequency;
        this.endingRunnable = endingRunnable;
    }

    public void launch(Runnable runnable) {
        if (isRunning())
            stop(null);
        remainingTime = cooldown;

        Runnable taskLogic = () -> {
            if (cooldown != Integer.MAX_VALUE)
                remainingTime--;
            if (remainingTime <= 0)
                stop(endingRunnable);

            if (runnable != null)
                runnable.run();
        };

        if (FoliaCompat.isFolia()) {
            ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(MCPets.getInstance(), t -> taskLogic.run(), 1L, frequency);
            runningTimers.put(this, task);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(MCPets.getInstance(), taskLogic, 0L, frequency);
            runningTimers.put(this, task);
        }
    }

    public void stop(Runnable runnable) {
        Object task = runningTimers.get(this);
        if (task != null) {
            if (FoliaCompat.isFolia()) {
                ((ScheduledTask) task).cancel();
            } else {
                ((BukkitTask) task).cancel();
            }
        }
        runningTimers.remove(this);
        remainingTime = 0;
        if (runnable != null)
            runnable.run();
    }

    public boolean isRunning() {
        return remainingTime > 0;
    }
}
