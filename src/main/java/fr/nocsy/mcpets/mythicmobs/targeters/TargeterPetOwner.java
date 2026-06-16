package fr.nocsy.mcpets.mythicmobs.targeters;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;

@MythicTargeter(
        name = "petOwner"
)
public class TargeterPetOwner extends IEntitySelector {

    public TargeterPetOwner(MythicTargeterLoadEvent event) {
        super(MCPets.getMythicMobs().getSkillManager(), event.getConfig());
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata meta) {
        AbstractEntity caster = meta.getCaster().getEntity();
        Pet pet = Pet.getFromEntity(caster.getBukkitEntity());

        if (pet != null) {
            Player owner = Bukkit.getPlayer(pet.getOwner());

            if (owner != null) {
                return Collections.singleton(BukkitAdapter.adapt(owner));
            }
        }

        return Collections.emptySet();
    }

}
