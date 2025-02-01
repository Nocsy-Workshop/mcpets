package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PetDamagedEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    @Getter
    private final Pet pet;
    @Getter
    @Setter
    private double originalDamageAmount;
    @Getter
    @Setter
    private double modifiedDamageAmount;
    private boolean applyPetStats;

    public PetDamagedEvent(Pet pet, double originalDamageAmount, boolean applyPetStats) {
        this.pet = pet;
        this.originalDamageAmount = originalDamageAmount;
        this.applyPetStats = applyPetStats;
        applyResistance();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Set the value of the original amount of damages
     * It also adapts the modified damages if the pet stats are enabled
     */
    public void setOriginalDamages(double value) {
        originalDamageAmount = value;
        applyResistance();
    }

    private void applyResistance() {
        if (applyPetStats) {
            modifiedDamageAmount = pet.getPetStats().getModifiedResistanceDamages(originalDamageAmount);
        }
        else
            modifiedDamageAmount = originalDamageAmount;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
