package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
