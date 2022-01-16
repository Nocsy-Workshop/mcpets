package fr.nocsy.mcpets.mythicmobs;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.mythicmobs.mechanics.GivePetMechanic;
import fr.nocsy.mcpets.mythicmobs.targeters.TargeterPetOwner;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillTargeter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MythicListener implements Listener {

    @EventHandler
    public void onMythicTargeterLoad(MythicTargeterLoadEvent paramMythicTargeterLoadEvent) {

        String str = paramMythicTargeterLoadEvent.getTargeterName();

        if ("PETOWNER".equals(str.toUpperCase())) {
            paramMythicTargeterLoadEvent.register((SkillTargeter) new TargeterPetOwner(paramMythicTargeterLoadEvent.getConfig()));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("GivePet")) {
            GivePetMechanic givePetMechanic = new GivePetMechanic(event.getConfig());
            event.register((SkillMechanic) givePetMechanic);

            MCPets.getLog().info("[MCPets] : GivePet Mechanic loaded successfully");

        }
    }

}
