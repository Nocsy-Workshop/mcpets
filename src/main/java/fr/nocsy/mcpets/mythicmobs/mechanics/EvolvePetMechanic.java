package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.debug.Debugger;

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
        name = "evolvePet"
)
public class EvolvePetMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final String evolutionId;
    private final boolean forceEvolution;

    public EvolvePetMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        evolutionId = config.getString(new String[]{"evolutionId"}, "");
        forceEvolution = config.getBoolean(new String[]{"force"}, false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(data.getCaster().getEntity());

        Pet pet = Pet.getFromEntity(entity);
        Pet evolution = Pet.getFromId(evolutionId);

        Debugger.send("§6[Evolution Mechanic]:");
        if (pet != null) {
            Debugger.send("§7- pet: §a" + pet.getId() + "§7 has pet stats ? §a" + (pet.getPetStats() != null));
        }

        Debugger.send("§7- evolution: §a" + evolutionId + " exists ? §a" + (evolution != null));

        if (evolution != null && pet != null && pet.getPetStats() != null) {
            Bukkit.getScheduler().runTask(
                    MCPets.getInstance(),
                    () -> pet.getPetStats()
                            .getCurrentLevel()
                            .evolveTo(pet.getOwner(), forceEvolution, evolution)
            );

            return SkillResult.SUCCESS;
        }

        return SkillResult.CONDITION_FAILED;
    }

}
