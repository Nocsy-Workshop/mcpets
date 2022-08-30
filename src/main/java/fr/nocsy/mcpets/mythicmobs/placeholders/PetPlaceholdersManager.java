package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.core.skills.placeholders.Placeholder;

public class PetPlaceholdersManager {

    /**
     * Register the Placeholders of MCPets into MythicMobs
     */
    public static void registerPlaceholders()
    {
        // Power placeholder
        register("pet.power", Placeholder.entity((entity, arg) -> {
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "1";
            else
                return Double.toString(pet.getPetStats().getCurrentLevel().getPower());
        }));
    }

    private static void register(String placeholder, Placeholder function)
    {
        MCPets.getMythicMobs().getPlaceholderManager().register(placeholder, function);
    }

}
