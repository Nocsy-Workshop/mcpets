package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetGainExperienceEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    @Getter
    private final Pet pet;
    @Getter
    @Setter
    private double experience;

    public PetGainExperienceEvent(Pet pet, double experience) {
        this.pet = pet;
        this.experience = experience;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
