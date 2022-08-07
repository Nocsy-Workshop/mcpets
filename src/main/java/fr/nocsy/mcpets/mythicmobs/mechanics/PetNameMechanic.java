package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

public class PetNameMechanic implements ITargetedEntitySkill {

    String petName;
    boolean save;

    public PetNameMechanic(MythicLineConfig config) {
        this.petName = config.getString(new String[]{"name"}, "No name");
        this.save =  config.getBoolean(new String[]{"save"}, false);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity petEntity = BukkitAdapter.adapt(target);
        Pet pet = Pet.getFromEntity(petEntity);
        if (pet != null) {
            pet.setDisplayName(petName, save);
        }
        return SkillResult.CONDITION_FAILED;
    }

}
