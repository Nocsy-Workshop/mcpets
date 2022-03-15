package fr.nocsy.mcpets.mythicmobs.targeters;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashSet;

public class TargeterPetOwner extends IEntitySelector {

    public TargeterPetOwner(MythicLineConfig paramMythicLineConfig) {
        super(MCPets.getMythicMobs().getSkillManager(),paramMythicLineConfig);
    }

    public Collection<AbstractEntity> getEntities(SkillMetadata paramSkillMetadata) {
        HashSet<AbstractEntity> hashSet = new HashSet();
        AbstractEntity abstractEntity = paramSkillMetadata.getCaster().getEntity();
        Pet pet = Pet.getFromEntity(abstractEntity.getBukkitEntity());
        if (pet != null && Bukkit.getPlayer(pet.getOwner()) != null)
            hashSet.add(BukkitAdapter.adapt(Bukkit.getPlayer(pet.getOwner())));
        return hashSet;
    }


}
