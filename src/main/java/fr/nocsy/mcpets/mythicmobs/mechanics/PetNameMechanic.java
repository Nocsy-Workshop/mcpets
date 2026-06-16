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
        name = "petName"
)
public class PetNameMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final String petName;
    private final boolean save;

    public PetNameMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        petName = config.getString(new String[]{"name"}, "No name");
        save = config.getBoolean(new String[]{"save"}, false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity petEntity = BukkitAdapter.adapt(target);

        Pet pet = Pet.getFromEntity(petEntity);
        if (pet == null) {
            return SkillResult.CONDITION_FAILED;
        }

        pet.setDisplayName(petName, save);

        return SkillResult.SUCCESS;
    }

}
