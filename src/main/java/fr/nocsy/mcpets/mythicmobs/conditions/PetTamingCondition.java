package fr.nocsy.mcpets.mythicmobs.conditions;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;

@MythicCondition(
        author = "Nocsy",
        name = "petTaming",
        description = "Tests value of the taming of the pet"
)
public class PetTamingCondition extends SkillCondition implements IEntityCondition {

    @MythicField(
            name = "taming",
            aliases = {"t"},
            description = "The taming value to check"
    )
    private RangedDouble taming;

    public PetTamingCondition(MythicConditionLoadEvent event) {
        super(event.getConfig().getLine());

        MythicLineConfig mlc = event.getConfig();

        taming = new RangedDouble(mlc.getString(new String[]{"taming", "t"}, conditionVar));
    }

    @Override
    public boolean check(AbstractEntity target) {
        Pet pet = Pet.getFromEntity(target.getBukkitEntity());

        return pet != null
                && pet.getPetStats() != null
                && taming.equals(pet.getTamingProgress());
    }

}
