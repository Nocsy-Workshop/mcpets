package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

public class PetDeathEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Pet pet;

    public PetDeathEvent(Pet pet) {
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
