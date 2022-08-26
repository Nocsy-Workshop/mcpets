package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.data.Pet;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class PetStats {

    @Getter
    @Setter
    // Reference to the actual pet
    private Pet pet;

    @Getter
    // Handles the health of the Pet
    private double health;

    @Getter
    // Handles the damage resistance of the pet
    private double resistance;

    @Getter
    // Handles the damage of the pet
    private double damage;

    @Getter
    // Handles the power of the pet
    // Used for the spells for instance
    private double power;

    @Getter
    // Handles the experience of the pet
    private double experience;

    @Getter
    // Handles the levels and their names
    private ArrayList<PetLevel> levels;

    @Getter
    // How long before the pet can be respawned after being dead
    // -1 Indicating permanent death
    private float respawnCooldown;
    @Getter
    // How long before the pet can be respawned after being revoked
    // -1 Indicating deletion of the pet
    private float revokeCooldown;

}
