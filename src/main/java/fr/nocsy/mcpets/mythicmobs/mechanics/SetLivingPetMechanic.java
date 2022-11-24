package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class SetLivingPetMechanic implements ITargetedEntitySkill {

    private String petId;
    private boolean followOnTame = true;
    private double tamingProgress = 0;

    public SetLivingPetMechanic(MythicLineConfig config) {
        this.petId = config.getString(new String[]{"id"}, this.petId);
        this.followOnTame = config.getBoolean(new String[]{"followOnTame", "foT"}, this.followOnTame);
        this.tamingProgress = config.getDouble(new String[]{"tamingProgress", "tP"}, this.tamingProgress);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {

        AbstractEntity ent = data.getCaster().getEntity();

        if(Pet.getFromEntity(ent.getBukkitEntity()) != null)
            return SkillResult.CONDITION_FAILED;

        Pet pet = Pet.getFromId(petId);
        if (pet == null)
            return SkillResult.CONDITION_FAILED;

        new BukkitRunnable() {
            @Override
            public void run() {
                // Set the active mob
                Optional<ActiveMob> opt = MCPets.getMythicMobs().getMobManager().getActiveMob(ent.getUniqueId());
                opt.ifPresent(pet::setActiveMob);
                // The taming progress should be set at the given value
                pet.setDefaultTamingValue(tamingProgress);
                pet.setFollowOwner(followOnTame);
            }
        }.runTaskLater(MCPets.getInstance(), 1L);
        return SkillResult.SUCCESS;
    }
}
