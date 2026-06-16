package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.entity.Entity;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;

@MythicMechanic(
        name = "petFollow"
)
public class PetFollowMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final boolean follow;

    public PetFollowMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        follow = config.getBoolean(new String[]{"follow"}, true);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);

        Pet pet = Pet.getFromEntity(entity);
        if (pet == null) {
            return SkillResult.CONDITION_FAILED;
        }

        pet.setFollowOwner(follow);

        return SkillResult.SUCCESS;
    }

}
