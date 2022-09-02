package fr.nocsy.mcpets.mythicmobs.conditions;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.Entity;

@MythicCondition(author="Nocsy",name="petExperience",description="Tests value of the experience of the pet")
public class PetExperienceCondition extends SkillCondition implements IEntityCondition {

    @MythicField(name = "experience", aliases = {"exp"}, description = "The experience value to check")
    private RangedDouble experience;

    public PetExperienceCondition(String line, MythicLineConfig mlc) {
        super(line);

        experience = new RangedDouble(mlc.getString(new String[]{"experience", "exp"}, conditionVar));
    }

    @Override
    public boolean check(AbstractEntity target) {
        Entity e = target.getBukkitEntity();
        Pet pet = Pet.getFromEntity(e);
        if (pet != null && pet.getPetStats() != null) {
            return experience.equals(pet.getPetStats().getExperience());
        }
        return false;
    }
}
