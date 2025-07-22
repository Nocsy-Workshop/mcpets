package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PetLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Pet pet;
    @Getter
    private final PetLevel petLevel;
    @Getter
    private final PetLevel oldLevel;
    @Getter
    private final UUID owner;

    public PetLevelUpEvent(Pet pet, PetLevel petLevel, PetLevel oldLevel, UUID owner) {
        this.pet = pet;
        this.petLevel = petLevel;
        this.oldLevel = oldLevel;
        this.owner = owner;
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
