package fr.nocsy.mcpets.mythicmobs;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.mythicmobs.mechanics.GivePetMechanic;
import fr.nocsy.mcpets.mythicmobs.mechanics.PetFollowMechanic;
import fr.nocsy.mcpets.mythicmobs.mechanics.PetNameMechanic;
import fr.nocsy.mcpets.mythicmobs.mechanics.SetPetMechanic;
import fr.nocsy.mcpets.mythicmobs.targeters.TargeterPetOwner;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MythicListener implements Listener {

    @EventHandler
    public void onMythicTargeterLoad(MythicTargeterLoadEvent paramMythicTargeterLoadEvent) {

        String str = paramMythicTargeterLoadEvent.getTargeterName();

        if ("PETOWNER".equalsIgnoreCase(str)) {
            paramMythicTargeterLoadEvent.register(new TargeterPetOwner(paramMythicTargeterLoadEvent.getConfig()));
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
    }

}
