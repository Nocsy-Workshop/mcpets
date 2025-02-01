package fr.nocsy.mcpets.compat;

import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPICompat extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        String defaultOutput = "";
        
        if (player == null)
            return defaultOutput;
        
        Pet pet = MCPetsAPI.getActivePet(player.getUniqueId());
        if (pet == null)
            return defaultOutput;

        switch(identifier.toUpperCase()){
            case "PET_NAME":
                return pet.getCurrentName();
            case "PET_ID":
                return pet.getId();
            case "PET_DISTANCE":
                return String.valueOf(pet.getDistance());
            case "PET_HEALTH":
                if (pet.getPetStats() != null)
                    return String.valueOf(Math.round(pet.getPetStats().getCurrentHealth()));
                else
                    return String.valueOf(Math.round(pet.getActiveMob().getEntity().getHealth()));
            case "PET_MAX_HEALTH":
                if (pet.getPetStats() != null)
                    return String.valueOf(Math.round(pet.getPetStats().getCurrentLevel().getMaxHealth()));
                else
                    return String.valueOf(Math.round(pet.getActiveMob().getEntity().getMaxHealth()));
            case "PET_ICON_NAME":
                if (pet.getIcon() != null)
                    return pet.getIcon().getItemMeta().getDisplayName();
                else
                    return "No icon name found.";
            case "PET_LEVEL_NAME":
                if (pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentLevel().getLevelName());
                else
                    return "Level not found: Pet is not a living pet.";
            case "PET_LEVEL_INDEX":
                if (pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentLevel().getLevelId());
                else
                    return "Level not found: Pet is not a living pet.";
            case "PET_EXP":
                if (pet.getPetStats() != null)
                    return String.valueOf(Math.round(pet.getPetStats().getExperience()));
                else
                    return "Exp not found: Pet is not a living pet.";
            case "PET_OWNER_NAME":
                return Bukkit.getPlayer(pet.getOwner()).getName();
            case "PET_OWNER_UUID":
                return pet.getOwner().toString();
            case "PET_POWER":
                if (pet.getPetStats() != null)
                    return String.valueOf(Math.round(pet.getPetStats().getPower()));
                else
                    return "Power not found: Pet is not a living pet.";
            case "PET_DAMAGE_MODIFIER":
                if (pet.getPetStats() != null)
                    return String.valueOf(Math.round(pet.getPetStats().getDamageModifier()));
                else
                    return "Damage modifier not found: Pet is not a living pet.";
            case "PET_RESISTANCE_MODIFIER":
                if (pet.getPetStats() != null)
                    return String.valueOf(Math.round(pet.getPetStats().getResistanceModifier()));
                else
                    return "Resistance modifier not found: Pet is not a living pet.";
            default:
                return defaultOutput;
        }
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
