package fr.nocsy.mcpets.mythicmobs.conditions;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
        author = "Nocsy",
        name = "petExperience",
        description = "Tests value of the experience of the pet"
)
public class PetExperienceCondition extends SkillCondition implements IEntityCondition {

    @MythicField(
            name = "experience",
            aliases = {"exp"},
            description = "The experience value to check"
    )
    private RangedDouble experience;

    public PetExperienceCondition(MythicConditionLoadEvent event) {
        super(event.getConfig().getLine());

        MythicLineConfig mlc = event.getConfig();

        experience = new RangedDouble(mlc.getString(new String[]{"experience", "exp"}, conditionVar));
    }

    @Override
    public boolean check(AbstractEntity target) {
        Pet pet = Pet.getFromEntity(target.getBukkitEntity());

        return pet != null
                && pet.getPetStats() != null
                && experience.equals(pet.getPetStats().getExperience());
    }
}
