package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GivePetMechanic extends SkillMechanic implements ITargetedEntitySkill {

    String petId;

    public GivePetMechanic(MythicLineConfig config) {
        super(config.getLine(), config);
        setAsyncSafe(false);
        this.petId = config.getString(new String[] { "id" }, this.petId);
    }

    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity player = BukkitAdapter.adapt(target);
        if (player instanceof Player) {

            Pet pet = Pet.getFromId(petId);
            if(pet == null)
                return false;
            player.addAttachment(MCPets.getInstance(), pet.getPermission(), true);
        }
        return true;
    }
}
