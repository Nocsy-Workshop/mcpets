package fr.nocsy.mcpets.mythicmobs.mechanics;

import java.util.Optional;

import org.bukkit.Bukkit;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;

@MythicMechanic(
        name = "setLivingPet"
)
public class SetLivingPetMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final String petId;
    private final boolean followOnTame;
    private final double tamingProgress;

    public SetLivingPetMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        petId = config.getString(new String[]{"id"}, "");
        followOnTame = config.getBoolean(new String[]{"followOnTame", "foT"}, true);
        tamingProgress = config.getDouble(new String[]{"tamingProgress", "tP"}, 0.0D);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        AbstractEntity entity = data.getCaster().getEntity();

        if (Pet.getFromEntity(entity.getBukkitEntity()) != null) {
            return SkillResult.CONDITION_FAILED;
        }

        Pet pet = Pet.getFromId(petId);
        if (pet == null) {
            return SkillResult.CONDITION_FAILED;
        }

        Bukkit.getScheduler().runTask(
                MCPets.getInstance(),
                () -> {
                    Optional<ActiveMob> activeMob = MCPets.getMythicMobs().getMobManager().getActiveMob(entity.getUniqueId());

                    activeMob.ifPresent(pet::setActiveMob);

                    pet.setDefaultTamingValue(tamingProgress);
                    pet.setFollowOwner(followOnTame);
                }
        );

        return SkillResult.SUCCESS;
    }

}
