package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetLevelUpEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    @Getter
    private final Pet pet;
    @Getter
    private PetLevel petLevel;

    public PetLevelUpEvent(Pet pet, PetLevel petLevel) {
        this.pet = pet;
        this.petLevel = petLevel;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public void setPetLevel(PetLevel petLevel)
    {
        this.petLevel = petLevel;
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
