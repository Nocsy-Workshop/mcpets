package fr.nocsy.mcpets.mythicmobs;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.mythicmobs.mechanics.*;
import fr.nocsy.mcpets.mythicmobs.targeters.TargeterPetOwner;
import fr.nocsy.mcpets.mythicmobs.conditions.PetTamingCondition;
import fr.nocsy.mcpets.mythicmobs.targeters.TargeterPetFromOwner;
import fr.nocsy.mcpets.mythicmobs.conditions.PetExperienceCondition;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.core.skills.CustomComponentRegistry;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;

public class MythicListener implements Listener {

    public static final String PLACEHOLDER_PACKAGE = "fr.nocsy.mcpets.mythicmobs.placeholders";

    @EventHandler
    public void onMythicReload(MythicReloadedEvent e) {
        // load the placeholders
        Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), () ->
                MCPets.getComponentRegistry().registerCustomComponent(
                        CustomComponentRegistry.MythicComponentType.PLACEHOLDER, PLACEHOLDER_PACKAGE), 1L);
    }

    @EventHandler
    public void onMythicEventLoad(MythicConditionLoadEvent e) {
        MythicLineConfig config = e.getConfig();
        switch (e.getConditionName().toLowerCase()) {
            case "petexperience" -> e.register(new PetExperienceCondition(config.getLine(), config));
            case "pettaming" -> e.register(new PetTamingCondition(config.getLine(), config));
        }
    }

    @EventHandler
    public void onMythicTargeterLoad(MythicTargeterLoadEvent e) {
        MythicLineConfig config = e.getConfig();
        switch (e.getTargeterName().toLowerCase()) {
            case "petowner" -> e.register(new TargeterPetOwner(config));
            case "petfromowner" -> e.register(new TargeterPetFromOwner(config));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMythicMechanicLoad(MythicMechanicLoadEvent e) {
        MythicLineConfig config = e.getConfig();
        switch (e.getMechanicName().toLowerCase()) {
            case "givepet" -> e.register(new GivePetMechanic(config));
            case "setpet" -> e.register(new SetPetMechanic(config));
            case "petfollow" -> e.register(new PetFollowMechanic(config));
            case "petname" -> e.register(new PetNameMechanic(config));
            case "petexperience" -> e.register(new PetExperienceMechanic(config));
            case "petdamage" -> e.register(new PetDamageMechanic(config));
            case "petbuff" -> e.register(new PetBuffMechanic(config));
            case "setlivingpet" -> e.register(new SetLivingPetMechanic(config));
            case "petdespawn" -> e.register(new PetDespawnMechanic(config));
            case "evolvepet" -> e.register(new EvolvePetMechanic(config));
            case "droppetinventory" -> e.register(new DropPetInventoryMechanic(config));
            case "droppetitem" -> e.register(new DropPetItemMechanic(config));
        }
    }

}
