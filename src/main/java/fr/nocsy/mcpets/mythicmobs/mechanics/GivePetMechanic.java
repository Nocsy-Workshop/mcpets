package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GivePetMechanic implements ITargetedEntitySkill {

    String petId;

    public GivePetMechanic(MythicLineConfig config) {
        this.petId = config.getString(new String[]{"id"}, this.petId);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity player = BukkitAdapter.adapt(target);
        if (player instanceof Player) {

            Pet pet = Pet.getFromId(petId);
            if (pet == null)
                return SkillResult.CONDITION_FAILED;
            Utils.givePermission(player.getUniqueId(), pet.getPermission());
        }
        return SkillResult.SUCCESS;
    }
}
