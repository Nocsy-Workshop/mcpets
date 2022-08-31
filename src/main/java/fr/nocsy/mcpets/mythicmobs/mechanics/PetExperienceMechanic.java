package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

public class PetExperienceMechanic implements ITargetedEntitySkill {

    double experience = 0;

    public PetExperienceMechanic(MythicLineConfig config) {
        this.experience = config.getDouble(new String[]{"exp"}, this.experience);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);

        Pet pet = Pet.getFromEntity(entity);
        if(pet != null && pet.getPetStats() != null)
        {
            pet.getPetStats().addExperience(experience);
            return SkillResult.SUCCESS;
        }
        return SkillResult.CONDITION_FAILED;

    }
}
