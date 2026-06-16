package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.events.PetDamageEvent;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;

@MythicMechanic(
        name = "petDamage"
)
public class PetDamageMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final PlaceholderDouble damage;
    private final boolean applyStats;

    public PetDamageMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        damage = config.getPlaceholderDouble(new String[]{"damage"}, 0);
        applyStats = config.getBoolean(new String[]{"applyStats"}, true);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        Entity caster = BukkitAdapter.adapt(data.getCaster().getEntity());

        Pet pet = Pet.getFromEntity(caster);
        if (pet == null || !(entity instanceof Damageable damageable)) {
            return SkillResult.CONDITION_FAILED;
        }

        PlaceholderContext context = PlaceholderContext.builder()
                .meta(data)
                .entity(target)
                .build();

        double damageAmount = damage.get(context);

        if (applyStats) {
            damageAmount = pet.getPetStats().getModifiedAttackDamages(damageAmount);
        }

        PetDamageEvent event = new PetDamageEvent(pet, damageAmount);

        Utils.callEvent(event);

        if (event.isCancelled()) {
            return SkillResult.CONDITION_FAILED;
        }

        damageable.damage(event.getDamageAmount(), caster);

        return SkillResult.SUCCESS;
    }

}
