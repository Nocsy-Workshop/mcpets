package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.data.livingpets.PetFoodBuff;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;

@MythicMechanic(
        name = "petBuff"
)
public class PetBuffMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final PlaceholderInt duration;
    private final PlaceholderFloat power;

    private final String operator;
    private final String type;

    public PetBuffMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        duration = config.getPlaceholderInteger(new String[]{"duration"}, 0);
        power = config.getPlaceholderFloat(new String[]{"power"}, 0);

        operator = config.getString(new String[]{"operator"}, "ADD");
        type = config.getString(new String[]{"type"}, "BUFF_DAMAGE");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);

        Pet pet = Pet.getFromEntity(entity);
        if (pet == null || pet.getPetStats() == null) {
            return SkillResult.CONDITION_FAILED;
        }

        PlaceholderContext context = PlaceholderContext.builder()
                .meta(data)
                .entity(target)
                .build();

        final long durationValue = duration.get(context);
        final float powerValue = power.get(context);

        PetFoodType buffType = PetFoodType.get(type);
        PetMath mathOperator = PetMath.get(operator);

        Bukkit.getScheduler().runTask(
                MCPets.getInstance(),
                () -> {
                    PetFoodBuff buff = new PetFoodBuff(pet, buffType, powerValue, mathOperator, durationValue);
                    buff.apply();
                }
        );

        return SkillResult.SUCCESS;
    }

}
