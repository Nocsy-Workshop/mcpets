package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PetTimer {

    @Getter
    private static HashMap<PetTimer, Integer> runningTimers = new HashMap<>();

    @Getter
    private int cooldown;
    @Getter
    private int remainingTime;
    private long frequency;

    private int task;

    /**
     * Constructor
     * Frequency giving the tick when repeating the task
     * @param cooldown
     * @param frequency
     */
    public PetTimer(int cooldown, long frequency)
    {
        this.cooldown = cooldown;
        this.remainingTime = 0;
        this.frequency = frequency;
    }

    public void launch(Runnable runnable)
    {
        // If it's running then cancel the current scheduler
        if(isRunning())
            stop();
        remainingTime = cooldown;
        task = Bukkit.getScheduler().scheduleAsyncRepeatingTask(MCPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(cooldown != Integer.MAX_VALUE)
                        remainingTime--;
                if(remainingTime <= 0)
                    stop();

                if (runnable != null)
                    runnable.run();
            }
        }, 0L, frequency);
        runningTimers.put(this, task);
    }

    public void stop()
    {
        Bukkit.getScheduler().cancelTask(task);
        runningTimers.remove(this);
        remainingTime = 0;
    }

    public boolean isRunning()
    {
        return remainingTime > 0;
    }

}
