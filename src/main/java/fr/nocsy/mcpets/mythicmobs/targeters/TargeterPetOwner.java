package fr.nocsy.mcpets.mythicmobs.targeters;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashSet;

public class TargeterPetOwner extends IEntitySelector {

    public TargeterPetOwner(MythicLineConfig paramMythicLineConfig) {
        super(paramMythicLineConfig);
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
