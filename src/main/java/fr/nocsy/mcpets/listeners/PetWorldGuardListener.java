package fr.nocsy.mcpets.listeners;

import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PetWorldGuardListener implements Listener {

    @EventHandler
    public void petSpawnWorldGuard(SpawnEntityEvent e)
    {
        Utils.debug("Entity spawning !");
        Entity ent = e.getEntity();
        Pet pet = Pet.getFromEntity(ent);
        // if it's a pet then we don't want it to be unable to spawn due to a worldguard flag
        // so we prevent the event
        if(pet != null)
        {
            Utils.debug("It's a pet !");
            e.setCancelled(false);
            e.setAllowed(true);
            e.setResult(Event.Result.ALLOW);
        }
    }

}
