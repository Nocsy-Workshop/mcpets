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

@MythicCondition(author="Nocsy",name="petExperience",description="测试宠物的经验值")
public class PetExperienceCondition extends SkillCondition implements IEntityCondition {

    @MythicField(name = "experience", aliases = {"exp"}, description = "需要检查的经验值")
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
