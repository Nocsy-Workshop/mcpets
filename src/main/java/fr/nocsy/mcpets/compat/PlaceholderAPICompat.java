package fr.nocsy.mcpets.compat;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.livingpets.PetStats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPICompat extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){
        String defaultOutput = "";
        
        if (player == null) return defaultOutput;
        
        Pet pet = MCPetsAPI.getActivePet(player.getUniqueId());
        if (pet == null) return defaultOutput;

        PetStats stats = pet.getPetStats();

        return switch(identifier.toUpperCase()){
            case "PET_NAME" -> pet.getCurrentName();
            case "PET_ID" -> pet.getId();
            case "PET_DISTANCE" -> String.valueOf(pet.getDistance());
            case "PET_HEALTH" -> String.valueOf(Math.round(stats != null
                    ? stats.getCurrentHealth() : pet.getActiveMob().getEntity().getHealth()
            ));
            case "PET_MAX_HEALTH" -> String.valueOf(Math.round(stats != null
                    ? stats.getCurrentLevel().getMaxHealth() : pet.getActiveMob().getEntity().getMaxHealth()
            ));
            case "PET_ICON_NAME" -> pet.getIcon() != null
                    ? pet.getIcon().getItemMeta().getDisplayName()
                    : "No icon name found.";
            case "PET_LEVEL_NAME" -> stats != null
                    ? stats.getCurrentLevel().getLevelName()
                    : "Level not found: Pet is not a living pet.";
            case "PET_LEVEL_INDEX" -> stats != null
                    ? String.valueOf(stats.getCurrentLevel().getLevelId())
                    : "Level not found: Pet is not a living pet.";
            case "PET_EXP" -> stats != null
                    ? String.valueOf(Math.round(stats.getExperience()))
                    : "Exp not found: Pet is not a living pet.";
            case "PET_OWNER_NAME" -> Bukkit.getPlayer(pet.getOwner()).getName();
            case "PET_OWNER_UUID" -> pet.getOwner().toString();
            case "PET_POWER" -> stats != null
                    ? String.valueOf(Math.round(stats.getPower()))
                    : "Power not found: Pet is not a living pet.";
            case "PET_DAMAGE_MODIFIER" -> stats != null
                    ? String.valueOf(Math.round(stats.getDamageModifier()))
                    : "Damage modifier not found: Pet is not a living pet.";
            case "PET_RESISTANCE_MODIFIER" -> stats != null
                    ? String.valueOf(Math.round(stats.getResistanceModifier()))
                    : "Resistance modifier not found: Pet is not a living pet.";
            default -> defaultOutput;
        };
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "mcpets";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "MCPets";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

}
