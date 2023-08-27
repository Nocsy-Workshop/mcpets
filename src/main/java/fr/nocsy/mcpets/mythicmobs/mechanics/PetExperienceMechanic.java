package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class PetExperienceMechanic implements ITargetedEntitySkill {

    PlaceholderDouble experience;

    public PetExperienceMechanic(MythicLineConfig config) {
        this.experience = config.getPlaceholderDouble(new String[]{"exp"}, 0.0D);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);

        Pet pet = Pet.getFromEntity(entity);
        if(pet != null && pet.getPetStats() != null)
        {
            // Call the experience gain on sync so it can trigger events
            final double expValue = experience.get(data);
            Bukkit.getScheduler().runTask(MCPets.getInstance(), new Runnable() {
                @Override
                public void run() {
                    pet.getPetStats().addExperience(expValue);
                }
            });
            return SkillResult.SUCCESS;
        }
        return SkillResult.CONDITION_FAILED;

    }
}
