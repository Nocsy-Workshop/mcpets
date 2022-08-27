package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.events.PetDamagedEvent;
import fr.nocsy.mcpets.events.PetGainExperienceEvent;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class LivingPetsListener implements Listener {

    @EventHandler
    public void damaged(EntityDamageEvent e)
    {
        Entity entity = e.getEntity();
        Pet pet = Pet.getFromEntity(entity);
        if(pet != null)
        {
            PetDamagedEvent event = new PetDamagedEvent(pet, e.getDamage(), true);
            Utils.callEvent(event);

            e.setCancelled(event.isCancelled());
            e.setDamage(event.getModifiedDamageAmount());
        }
    }

    @EventHandler
    public void gainExperience(EntityDeathEvent e)
    {


    }

}
