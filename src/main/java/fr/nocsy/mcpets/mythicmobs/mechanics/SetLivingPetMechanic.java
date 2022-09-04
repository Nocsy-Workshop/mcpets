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

    public SetLivingPetMechanic(MythicLineConfig config) {
        this.petId = config.getString(new String[]{"id"}, this.petId);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {

        AbstractEntity ent = data.getCaster().getEntity();
        Pet pet = Pet.getFromId(petId);
        if (pet == null)
            return SkillResult.CONDITION_FAILED;

        new BukkitRunnable() {
            @Override
            public void run() {
                // Set the active mob
                Optional<ActiveMob> opt = MCPets.getMythicMobs().getMobManager().getActiveMob(ent.getUniqueId());
                opt.ifPresent(pet::setActiveMob);
                // Set the taming progress to 0, coz it has no owner at the moment
                pet.setTamingProgress(0);
            }
        }.runTaskLater(MCPets.getInstance(), 1L);
        return SkillResult.SUCCESS;
    }
}
