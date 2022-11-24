package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetSpawnedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Pet pet;

    public PetSpawnedEvent(Pet pet) {
        this.pet = pet;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
