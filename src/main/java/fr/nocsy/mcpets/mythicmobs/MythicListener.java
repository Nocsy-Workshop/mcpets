package fr.nocsy.mcpets.mythicmobs;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.mythicmobs.conditions.PetExperienceCondition;
import fr.nocsy.mcpets.mythicmobs.conditions.PetTamingCondition;
import fr.nocsy.mcpets.mythicmobs.mechanics.*;
import fr.nocsy.mcpets.mythicmobs.placeholders.PetPlaceholdersManager;
import fr.nocsy.mcpets.mythicmobs.targeters.TargeterPetFromOwner;
import fr.nocsy.mcpets.mythicmobs.targeters.TargeterPetOwner;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class MythicListener implements Listener {

    @EventHandler
    public void onMythicReload(MythicReloadedEvent e)
    {
        // load the place holders
        new BukkitRunnable() {
            @Override
            public void run() {
                PetPlaceholdersManager.registerPlaceholders();
            }
        }.runTaskLater(MCPets.getInstance(), 1L);
    }

    @EventHandler
    public void onMythicEventLoad(MythicConditionLoadEvent e)
    {
        if(e.getConditionName().equalsIgnoreCase("petExperience"))
        {
            PetExperienceCondition cond = new PetExperienceCondition(e.getConfig().getLine(), e.getConfig());
            e.register(cond);
        }
        else if(e.getConditionName().equalsIgnoreCase("petTaming"))
        {
            PetTamingCondition cond = new PetTamingCondition(e.getConfig().getLine(), e.getConfig());
            e.register(cond);
        }
    }

    @EventHandler
    public void onMythicTargeterLoad(MythicTargeterLoadEvent paramMythicTargeterLoadEvent) {

        String str = paramMythicTargeterLoadEvent.getTargeterName();

        if (str.equalsIgnoreCase("PETOWNER")) {
            paramMythicTargeterLoadEvent.register(new TargeterPetOwner(paramMythicTargeterLoadEvent.getConfig()));
        }
        else if (str.equalsIgnoreCase("PETFROMOWNER")) {
            paramMythicTargeterLoadEvent.register(new TargeterPetFromOwner(paramMythicTargeterLoadEvent.getConfig()));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("GivePet"))
        {
            GivePetMechanic mechanic = new GivePetMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("SetPet"))
        {
            SetPetMechanic mechanic = new SetPetMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("PetFollow"))
        {
            PetFollowMechanic mechanic = new PetFollowMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("PetName"))
        {
            PetNameMechanic mechanic = new PetNameMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("PetExperience"))
        {
            PetExperienceMechanic mechanic = new PetExperienceMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("PetDamage"))
        {
            PetDamageMechanic mechanic = new PetDamageMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("SetLivingPet"))
        {
            SetLivingPetMechanic mechanic = new SetLivingPetMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("PetDespawn"))
        {
            PetDespawnMechanic mechanic = new PetDespawnMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("EvolvePet"))
        {
            EvolvePetMechanic mechanic = new EvolvePetMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("DropPetInventory"))
        {
            DropPetInventoryMechanic mechanic = new DropPetInventoryMechanic(event.getConfig());
            event.register(mechanic);
        }
        else if (event.getMechanicName().equalsIgnoreCase("DropPetItem"))
        {
            DropPetItemMechanic mechanic = new DropPetItemMechanic(event.getConfig());
            event.register(mechanic);
        }

    }

}
