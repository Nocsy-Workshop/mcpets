package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;

public class PetDespawnMechanic implements ITargetedEntitySkill {

    public PetDespawnMechanic(MythicLineConfig config) {}

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {

        AbstractEntity ent = data.getCaster().getEntity();

        Pet pet = Pet.getFromEntity(ent.getBukkitEntity());
        if (pet == null)
            return SkillResult.CONDITION_FAILED;

        pet.despawn(PetDespawnReason.PETDESPAWN_SKILL);
        return SkillResult.SUCCESS;
    }
}
