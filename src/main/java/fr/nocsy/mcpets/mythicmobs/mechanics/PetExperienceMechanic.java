package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import fr.nocsy.mcpets.MCPets;
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
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;

@MythicMechanic(
        name = "petExperience"
)
public class PetExperienceMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final PlaceholderDouble experience;

    public PetExperienceMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        experience = config.getPlaceholderDouble(new String[]{"exp"}, 0.0D);
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

        final double expValue = experience.get(context);

        Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> pet.getPetStats().addExperience(expValue));

        return SkillResult.SUCCESS;
    }

}
