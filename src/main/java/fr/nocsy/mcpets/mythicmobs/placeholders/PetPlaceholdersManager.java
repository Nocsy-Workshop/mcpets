package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.MCPets;
import io.lumine.mythic.core.skills.CustomComponentRegistry;

public class PetPlaceholdersManager {

    private static boolean registered = false;

    /**
     * Register the Placeholders of MCPets into MythicMobs
     */
    public static void registerPlaceholders() {
        if (registered) {
            return;
        }

        new CustomComponentRegistry(MCPets.getInstance(), "fr.nocsy.mcpets.mythicmobs.placeholders");
        registered = true;
    }
}
