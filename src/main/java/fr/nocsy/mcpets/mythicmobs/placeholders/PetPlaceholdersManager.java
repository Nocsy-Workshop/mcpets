package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import org.bukkit.Bukkit;


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

        // Pet owner UUID placeholder
        register("pet.owner.uuid", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null)
                return "null";
            else
                return pet.getOwner().toString();
        }));

        // Pet owner name placeholder
        register("pet.owner.name", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null)
                return "null";
            else
            {
                if(pet.getOwner() == null)
                    return "null";
                return Bukkit.getOfflinePlayer(pet.getOwner()).getName();
            }

        }));

        // Level index placeholder
        register("pet.level.index", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "0";
            else
                return Integer.toString(pet.getPetStats().getCurrentLevelIndex());
        }));

        // Level name placeholder
        register("pet.level.name", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return "null";
            else
                return pet.getPetStats().getCurrentLevel().getLevelName();
        }));

        // Level name placeholder
        register("pet.hp", Placeholder.meta((meta,arg) -> {
            AbstractEntity entity = meta.getCaster().getEntity();
            Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
            if(pet == null || pet.getPetStats() == null)
                return Double.toString(entity.getHealth());
            else
                return Double.toString(pet.getPetStats().getCurrentHealth());
        }));
    }

    private static void register(String placeholder, Placeholder function)
    {
        MCPets.getMythicMobs().getPlaceholderManager().register(placeholder, function);
    }

}
