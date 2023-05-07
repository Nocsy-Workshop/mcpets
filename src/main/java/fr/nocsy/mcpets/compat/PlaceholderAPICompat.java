package fr.nocsy.mcpets.compat;

import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderAPICompat extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        Pet pet = MCPetsAPI.getActivePet(player.getUniqueId());
        String defaultOutput = "";
        if(pet == null)
            return defaultOutput;

        switch(identifier.toUpperCase()){
            case "PET_NAME":
                return pet.getCurrentName();
            case "PET_ID":
                return pet.getId();
            case "PET_DISTANCE":
                return String.valueOf(pet.getDistance());
            case "PET_HEALTH":
                if(pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentHealth());
            case "PET_ICON_NAME":
                if(pet.getIcon() != null)
                    return pet.getIcon().getItemMeta().getDisplayName();
            case "PET_LEVEL_NAME":
                if(pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentLevel().getLevelName());
            case "PET_LEVEL_INDEX":
                if(pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentLevel().getLevelId());
            case "PET_EXP":
                if(pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getExperience());
            case "PET_OWNER_NAME":
                return Bukkit.getPlayer(pet.getOwner()).getName();
            case "PET_OWNER_UUID":
                return pet.getOwner().toString();
            case "PET_POWER":
                if(pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentLevel().getPower());
            case "PET_DAMAGE_MODIFIER":
                if(pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentLevel().getDamageModifier());
            case "PET_RESISTANCE_MODIFIER":
                if(pet.getPetStats() != null)
                    return String.valueOf(pet.getPetStats().getCurrentLevel().getResistanceModifier());
            default:
                return defaultOutput;
        }
    }

    @Override
    public String getIdentifier() {
        return "mcpets";
    }

    @Override
    public String getAuthor() {
        return "MCPets";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
