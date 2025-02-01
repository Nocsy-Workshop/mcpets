package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.events.PetDamageEvent;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

public class PetDamageMechanic implements ITargetedEntitySkill {

    private PlaceholderDouble damage;
    private boolean applyStats = true;

    public PetDamageMechanic(MythicLineConfig config) {
        this.damage = config.getPlaceholderDouble(new String[]{"damage"}, this.damage);
        this.applyStats = config.getBoolean(new String[]{"applyStats"}, this.applyStats);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        Entity caster = BukkitAdapter.adapt(data.getCaster().getEntity());
        Pet pet = Pet.getFromEntity(caster);

        if (pet != null && entity instanceof Damageable) {
            PetDamageEvent event = new PetDamageEvent(pet, pet.getPetStats().getModifiedAttackDamages(damage.get(data, target)));
            Utils.callEvent(event);
            if (event.isCancelled())
                return SkillResult.CONDITION_FAILED;

            ((Damageable) entity).damage(event.getDamageAmount(), caster);

            return SkillResult.SUCCESS;
        }

        return SkillResult.CONDITION_FAILED;
    }
}
