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

        // Damage modifier placeholder
        register("pet.damagemodifier", Placeholder.entity((entity, arg) -> {
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "1";
            else
                return Double.toString(pet.getPetStats().getCurrentLevel().getDamageModifier());
        }));

        // Resistance modifier placeholder
        register("pet.resistancemodifier", Placeholder.entity((entity, arg) -> {
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "1";
            else
                return Double.toString(pet.getPetStats().getCurrentLevel().getResistanceModifier());
        }));

        // Experience placeholder
        register("pet.experience", Placeholder.entity((entity, arg) -> {
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "0";
            else
                return Double.toString(pet.getPetStats().getExperience());
        }));

        // Pet Id placeholder
        register("pet.id", Placeholder.entity((entity, arg) -> {
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return null;
            else
                return pet.getId();
        }));
    }

    private static void register(String placeholder, Placeholder function)
    {
        MCPets.getMythicMobs().getPlaceholderManager().register(placeholder, function);
    }

}
