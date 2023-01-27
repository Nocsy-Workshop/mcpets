package fr.nocsy.mcpets.compat;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPICompat extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        switch(identifier.toUpperCase()){
            case "TEST":
                return "test";
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
