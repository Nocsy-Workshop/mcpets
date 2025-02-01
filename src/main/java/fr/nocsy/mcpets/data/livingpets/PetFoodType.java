package fr.nocsy.mcpets.data.livingpets;

import lombok.Getter;

import java.util.Arrays;

public enum PetFoodType {

    HEALTH("health"),
    TAME("tame"),
    EXP("exp"),
    EVOLUTION("evolution"),
    UNLOCK("unlock"),

    BUFF_DAMAGE("buff_damage"),
    BUFF_RESISTANCE("buff_resistance"),
    BUFF_POWER("buff_power");

    @Getter
    private final String type;

    PetFoodType(String type) {
        this.type = type;
    }

    /**
     * Get the food type corresponding to the given name
     */
    public static PetFoodType get(String type) {
        return Arrays.stream(PetFoodType.values()).filter(petFoodType -> petFoodType.getType().equalsIgnoreCase(type)).findFirst().orElse(null);
    }
}
