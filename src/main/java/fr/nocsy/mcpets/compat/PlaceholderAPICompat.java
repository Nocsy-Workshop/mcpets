package fr.nocsy.mcpets.compat;

import fr.nocsy.mcpets.api.MCPetsAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPICompat extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        switch(identifier.toUpperCase()){
            // %mcpets_current_name%
            case "PET_NAME":
                return MCPetsAPI.getActivePet(player.getUniqueId()).getCurrentName();
            // %mcpets_pet_id%
            case "PET_ID":
                return String.valueOf(MCPetsAPI.getActivePet(player.getUniqueId()).getId());
            // %mcpets_pet_distance%
            case "PET_DISTANCE":
                return String.valueOf(MCPetsAPI.getActivePet(player.getUniqueId()).getDistance());
            default:
                return "NPE";
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
