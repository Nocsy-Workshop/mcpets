package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.debug.Debugger;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class EvolvePetMechanic implements ITargetedEntitySkill {

    String evolutionId = "";
    boolean forceEvolution = false;

    public EvolvePetMechanic(MythicLineConfig config) {
        this.evolutionId = config.getString(new String[]{"evolutionId"}, this.evolutionId);
        this.forceEvolution = config.getBoolean(new String[]{"force"}, this.forceEvolution);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(data.getCaster().getEntity());

        Pet pet = Pet.getFromEntity(entity);
        Pet evolution = Pet.getFromId(evolutionId);
        Debugger.send("§6[Evolution Mechanic]:");
        if(pet != null)
            Debugger.send("§7- pet: §a" + pet.getId() + "§7 has pet stats ? §a" + (pet.getPetStats() != null));
        Debugger.send("§7- evolution: §a" + evolutionId + " exists ? §a" + (evolution != null));
        if(evolution != null
                && pet != null
                && pet.getPetStats() != null)
        {
            // Call the experience gain on sync so it can trigger events
            Bukkit.getScheduler().runTask(MCPets.getInstance(), new Runnable() {
                @Override
                public void run() {
                    pet.getPetStats().getCurrentLevel().evolveTo(pet.getOwner(), forceEvolution, evolution);
                }
            });
            return SkillResult.SUCCESS;
        }
        return SkillResult.CONDITION_FAILED;

    }
}
