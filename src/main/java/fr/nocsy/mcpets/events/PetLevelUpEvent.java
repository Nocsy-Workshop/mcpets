package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PetLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Pet pet;
    @Getter
    private final PetLevel petLevel;
    @Getter
    private final PetLevel oldLevel;

    public PetLevelUpEvent(Pet pet, PetLevel petLevel, PetLevel oldLevel) {
        this.pet = pet;
        this.petLevel = petLevel;
        this.oldLevel = oldLevel;
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
