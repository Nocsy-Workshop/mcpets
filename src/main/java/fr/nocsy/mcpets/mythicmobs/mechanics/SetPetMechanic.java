package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;

@MythicMechanic(
        name = "setPet"
)
public class SetPetMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final String petId;
    private final boolean followOwner;
    private final boolean mustHavePermission;

    public SetPetMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        petId = config.getString(new String[]{"id"}, "");
        followOwner = config.getBoolean(new String[]{"followOwner", "fo"}, true);
        mustHavePermission = config.getBoolean(new String[]{"mustHavePermission", "permCheck"}, false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        if (!(entity instanceof Player player)) {
            return SkillResult.CONDITION_FAILED;
        }

        Pet pet = Pet.getFromId(petId);
        if (pet == null) {
            return SkillResult.CONDITION_FAILED;
        }
        
        if (mustHavePermission && !player.hasPermission(pet.getPermission())) {
            return SkillResult.CONDITION_FAILED;
        }

        Bukkit.getScheduler().runTask(
                MCPets.getInstance(),
                () -> MCPets.getMythicMobs()
                        .getMobManager()
                        .getActiveMob(data.getCaster().getEntity().getUniqueId())
                        .ifPresent(activeMob ->
                                pet.changeActiveMobTo(
                                        activeMob,
                                        player.getUniqueId(),
                                        followOwner,
                                        PetDespawnReason.SETPET_REPLACED
                                )
                        )
        );

        return SkillResult.SUCCESS;
    }

}
