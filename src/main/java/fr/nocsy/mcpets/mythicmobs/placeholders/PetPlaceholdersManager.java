package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.Placeholder;


public class PetPlaceholdersManager {

    /**
     * Register the Placeholders of MCPets into MythicMobs
     */
    public static void registerPlaceholders()
    {
        // Power placeholder
        register("pet.power", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "1";
            else
                return Double.toString(pet.getPetStats().getCurrentLevel().getPower());
        }));

        // Damage modifier placeholder
        register("pet.damagemodifier", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "1";
            else
                return Double.toString(pet.getPetStats().getCurrentLevel().getDamageModifier());
        }));

        // Resistance modifier placeholder
        register("pet.resistancemodifier", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "1";
            else
                return Double.toString(pet.getPetStats().getCurrentLevel().getResistanceModifier());
        }));

        // Experience placeholder
        register("pet.experience", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "0";
            else
                return Double.toString(pet.getPetStats().getExperience());
        }));

        // Pet Taming progress placeholder
        register("pet.tamingprogress", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null)
                return "1";
            else
                return  Double.toString(pet.getTamingProgress());
        }));

        // Pet Id placeholder
        register("pet.id", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null)
                return "null";
            else
                return pet.getId();
        }));
    }

    private static void register(String placeholder, Placeholder function)
    {
        MCPets.getMythicMobs().getPlaceholderManager().register(placeholder, function);
    }

}
