package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetFoodBuff;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.utils.PetMath;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class PetBuffMechanic implements ITargetedEntitySkill {

    private PlaceholderInt duration;
    private PlaceholderFloat power;
    private String operator;
    private String type;

    public PetBuffMechanic(MythicLineConfig config) {
        this.duration = config.getPlaceholderInteger(new String[]{"duration"}, 0);
        this.power = config.getPlaceholderFloat(new String[]{"power"}, 0);
        this.operator = config.getString(new String[]{"operator"}, "ADD");
        this.type = config.getString(new String[]{"type"}, "BUFF_DAMAGE");
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);

        Pet pet = Pet.getFromEntity(entity);
        if (pet != null && pet.getPetStats() != null) {
            // Call the experience gain on sync so it can trigger events
            final long durationValue = duration.get(data);
            final float powerValue = power.get(data);
            PetFoodType buffType = PetFoodType.get(type);
            PetMath mathOperator = PetMath.get(operator);
            Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
                PetFoodBuff buff = new PetFoodBuff(pet, buffType, powerValue, mathOperator, durationValue);
                buff.apply();
            });
            return SkillResult.SUCCESS;
        }
        return SkillResult.CONDITION_FAILED;
    }
}
