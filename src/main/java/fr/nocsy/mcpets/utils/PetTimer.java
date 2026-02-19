package fr.nocsy.mcpets.utils;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.nocsy.mcpets.MCPets;
import lombok.Getter;

import java.util.HashMap;

public class PetTimer {

    @Getter
    private static HashMap<PetTimer, WrappedTask> runningTimers = new HashMap<>();

    @Getter
    private int cooldown;
    @Getter
    private int remainingTime;
    private long frequency;

    private WrappedTask task;

    private final Runnable endingRunnable;

    /**
     * Constructor
     * Frequency giving the tick when repeating the task
     */
    public PetTimer(int cooldown, long frequency, Runnable endingRunnable) {
        this.cooldown = cooldown;
        this.remainingTime = 0;
        this.frequency = frequency;
        this.endingRunnable = endingRunnable;
    }

    public void launch(Runnable runnable) {
        // If it's running then cancel the current scheduler
        if (isRunning())
            stop(null);
        remainingTime = cooldown;
        task = MCPets.getScheduler().runTimerAsync(() -> {
            if (cooldown != Integer.MAX_VALUE)
                remainingTime--;
            if (remainingTime <= 0)
                stop(endingRunnable);

            if (runnable != null)
                runnable.run();
        }, 0L, frequency);
        runningTimers.put(this, task);
    }

    public void stop(Runnable runnable) {
        task.cancel();
        runningTimers.remove(this);
        remainingTime = 0;
        if (runnable != null)
            runnable.run();
    }

    public boolean isRunning() {
        return remainingTime > 0;
    }
}
