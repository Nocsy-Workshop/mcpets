package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Pet pet;
    @Getter
    private final PetLevel petLevel;

    public PetLevelUpEvent(Pet pet, PetLevel petLevel) {
        this.pet = pet;
        this.petLevel = petLevel;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
