package fr.nocsy.mcpets.mythicmobs.targeters;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.debug.Debugger;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;

@MythicTargeter(
        name = "petFromOwner"
)
public class TargeterPetFromOwner extends IEntitySelector {

    public TargeterPetFromOwner(MythicTargeterLoadEvent event) {
        super(MCPets.getMythicMobs().getSkillManager(), event.getConfig());
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata metadata) {
        AbstractEntity caster = metadata.getCaster().getEntity();
        if (!(caster instanceof BukkitPlayer)) {
            Debugger.send("Targeter @PetFromOwner called with unknown caster type: " + caster.getClass().getName());
            return Collections.emptySet();
        }

        List<Pet> pets = Pet.getActivePetsForOwner(caster.getUniqueId());
        if (pets.isEmpty()) {
            Debugger.send("Targeter @PetFromOwner found no pets for owner " + caster.getUniqueId());
            return Collections.emptySet();
        }

        Set<AbstractEntity> entities = new HashSet<>();

        for (Pet pet : pets) {
            if (pet.getActiveMob() == null || pet.getActiveMob().getEntity() == null) {
                continue;
            }

            entities.add(pet.getActiveMob().getEntity());

            Debugger.send("Targeter @PetFromOwner found pet " + pet.getId() + " for owner " + caster.getUniqueId());
        }

        return entities;
    }

}
