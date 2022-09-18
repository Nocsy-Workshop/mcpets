package fr.nocsy.mcpets.mythicmobs.targeters;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class TargeterPetFromOwner extends IEntitySelector {

    public TargeterPetFromOwner(MythicLineConfig paramMythicLineConfig) {
        super(MCPets.getMythicMobs().getSkillManager(),paramMythicLineConfig);
    }

    public Collection<AbstractEntity> getEntities(SkillMetadata paramSkillMetadata) {
        HashSet<AbstractEntity> hashSet = new HashSet();
        AbstractEntity caster = paramSkillMetadata.getCaster().getEntity();
        if(caster instanceof Player)
        {
            Pet pet = Pet.fromOwner(((Player)caster).getUniqueId());
            if(pet != null)
                hashSet.add(pet.getActiveMob().getEntity());
        }
        return hashSet;
    }
}
