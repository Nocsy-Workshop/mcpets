package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;

import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;

@MythicMechanic(
        name = "petDespawn"
)
public class PetDespawnMechanic extends SkillMechanic implements ITargetedEntitySkill {

    public PetDespawnMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        AbstractEntity entity = data.getCaster().getEntity();

        Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
        if (pet == null) {
            return SkillResult.CONDITION_FAILED;
        }

        pet.despawn(PetDespawnReason.PETDESPAWN_SKILL);

        return SkillResult.SUCCESS;
    }

}
