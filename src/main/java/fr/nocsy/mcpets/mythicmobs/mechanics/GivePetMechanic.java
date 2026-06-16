package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.Utils;

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
        name = "givePet"
)
public class GivePetMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final String petId;

    public GivePetMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        petId = config.getString(new String[]{"id"}, "");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity player = BukkitAdapter.adapt(target);

        if (player instanceof Player) {
            Pet pet = Pet.getFromId(petId);
            if (pet == null) {
                return SkillResult.CONDITION_FAILED;
            }

            Utils.givePermission(player.getUniqueId(), pet.getPermission());
        }

        return SkillResult.SUCCESS;
    }

}
