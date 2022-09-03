package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Optional;

public class SetPetMechanic implements ITargetedEntitySkill {

    private String petId;
    private boolean followOwner = true;
    private boolean mustHavePermission;

    public SetPetMechanic(MythicLineConfig config) {
        this.petId = config.getString(new String[]{"id"}, this.petId);
        this.followOwner = config.getBoolean(new String[]{"followOwner", "fo"}, this.followOwner);
        this.mustHavePermission =  config.getBoolean(new String[]{"mustHavePermission","permCheck"}, this.mustHavePermission);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity player = BukkitAdapter.adapt(target);
        if (player instanceof Player) {

            Pet pet = Pet.getFromId(petId);
            if (pet == null)
                return SkillResult.CONDITION_FAILED;

            new BukkitRunnable() {
                @Override
                public void run() {
                    Optional<ActiveMob> opt = MCPets.getMythicMobs().getMobManager().getActiveMob(data.getCaster().getEntity().getUniqueId());
                    opt.ifPresent(activeMob -> pet.changeActiveMobTo(activeMob,
                                                                    ((Player)player).getUniqueId(),
                                                                    followOwner,
                                                                    PetDespawnReason.SETPET_REPLACED));

                }
            }.runTaskLater(MCPets.getInstance(), 1L);
            return SkillResult.SUCCESS;
        }
        return SkillResult.CONDITION_FAILED;
    }
}
