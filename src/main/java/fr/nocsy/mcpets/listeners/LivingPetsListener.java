package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.events.*;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LivingPetsListener implements Listener {

    //--------- HANDLERS TO TRIGGER CUSTOM EVENTS ---------//

    @EventHandler
    // Trigger for the PetDamagedEvent
    public void petDamagedHandler(EntityDamageEvent e)
    {
        Entity entity = e.getEntity();
        Pet pet = Pet.getFromEntity(entity);
        if(pet != null && pet.getPetStats() != null)
        {
            PetDamagedEvent event = new PetDamagedEvent(pet, e.getDamage(), true);
            Utils.callEvent(event);

            e.setCancelled(event.isCancelled());
            e.setDamage(event.getModifiedDamageAmount());
        }
    }

    @EventHandler
    // Trigger the PetDeathEvent
    public void petDeathHandler(EntityDeathEvent e)
    {
        Entity entity = e.getEntity();
        Pet pet = Pet.getFromEntity(entity);

        if(pet != null && pet.getPetStats() != null)
        {
            PetDeathEvent event = new PetDeathEvent(pet);
            Utils.callEvent(event);
        }

    }

    @EventHandler
    // handles the pet tame by player event
    public void petTamedByPlayerEvent(PlayerInteractAtEntityEvent e)
    {
        Player p = e.getPlayer();
        // Check if the player holds pet food
        ItemStack it = p.getInventory().getItemInMainHand();
        PetFood petFood = PetFood.getFromItem(it);
        if(petFood != null)
        {
            // Check if the interacted mob is a pet
            Pet pet = Pet.getFromEntity(e.getRightClicked());
            if(pet != null)
            {
                // The pet has never been tamed, so let's tame it
                // Or it's being tamed by the same player
                if(pet.getOwner() == null || pet.getOwner().equals(p.getUniqueId()))
                {
                    // Set the pet owner to the giver of food
                    pet.setOwner(p.getUniqueId());

                    // Call the pet tamed by player event
                    PetTamedByPlayerEvent event = new PetTamedByPlayerEvent(pet, p, petFood);
                    Utils.callEvent(event);
                }
                // The pet has already been tamed, so let's do nothing
                else
                {
                    Language.PET_ALREADY_TAMED.sendMessage(p);
                }
            }
        }

    }

    //--------- HANDLERS FOR ACTIONS ---------//

    @EventHandler
    public void damagedByPlayer(EntityDamageByEntityEvent e)
    {
        Entity ent = e.getEntity();
        Entity damager = e.getDamager();
        if(damager instanceof Player);
        {
            Player p = ((Player)damager);
            Pet pet = Pet.getFromEntity(ent);
            if(pet != null)
            {
                // If the player is the owner, then it can not damage the pet
                if(pet.getOwner().equals(p.getUniqueId()))
                {
                    e.setDamage(0);
                    e.setCancelled(true);
                    return;
                }

                // If the PvP is disabled in the world, then one can not damage the pet
                if(!ent.getWorld().getPVP())
                {
                    e.setDamage(0);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    // Update health for the pet stats
    public void updateHealth(PetDamagedEvent e)
    {
        Pet pet = e.getPet();

        if(e.isCancelled())
            return;

        PetStats stats = pet.getPetStats();
        stats.updateHealth();
    }

    @EventHandler
    // Handles if the pet can be spawned or not according to the timers
    public void attemptToSpawn(PetSpawnEvent e)
    {
        Pet pet = e.getPet();
        if(pet.getPetStats() != null)
        {
            PetStats stats = pet.getPetStats();

            if(stats.getRespawnTimer().isRunning())
            {
                e.setCancelled(true);
                PetDespawnEvent petDespawnEvent = new PetDespawnEvent(pet, PetDespawnReason.RESPAWN_TIMER);
                Utils.callEvent(petDespawnEvent);

                Player p = Bukkit.getPlayer(pet.getOwner());

                if(p != null)
                    Language.RESPAWN_TIMER_RUNNING.sendMessageFormated(p,
                                                    new FormatArg("timeLeft", Integer.toString(stats.getRespawnTimer().getRemainingTime())),
                                                    new FormatArg("cooldown", Integer.toString(stats.getRespawnTimer().getCooldown())));
            }
            else if(stats.getRevokeTimer().isRunning())
            {
                e.setCancelled(true);
                PetDespawnEvent petDespawnEvent = new PetDespawnEvent(pet, PetDespawnReason.REVOKE_TIMER);
                Utils.callEvent(petDespawnEvent);

                Player p = Bukkit.getPlayer(pet.getOwner());

                if(p != null)
                    Language.REVOKE_TIMER_RUNNING.sendMessageFormated(p,
                            new FormatArg("timeLeft", Integer.toString(stats.getRespawnTimer().getRemainingTime())),
                            new FormatArg("cooldown", Integer.toString(stats.getRespawnTimer().getCooldown())));
            }

        }
    }

    @EventHandler
    // Launch the respawn timer on death
    public void respawnCooldownHandler(PetDeathEvent e)
    {
        Pet pet = e.getPet();
        pet.getPetStats().launchRespawnTimer();
    }

    @EventHandler
    // Revoke cooldown timer
    public void revokeCooldownHandler(PetDespawnEvent e)
    {
        Pet pet = e.getPet();
        if(e.getReason().equals(PetDespawnReason.REVOKE) &&
            pet.getPetStats() != null)
        {
            pet.getPetStats().launchRevokeTimer();
        }
    }

    @EventHandler
    // Handles respawn Health
    public void respawnHealth(PetSpawnedEvent e)
    {
        Pet pet = e.getPet();
        if(pet.getPetStats() != null)
        {
            PetStats stats = pet.getPetStats();
            stats.setPet(e.getPet());

            // Refresh the Max Health value depending on the level of the pet
            stats.refreshMaxHealth();

            // Set the pet's health value
            // Either it's just dead and it's a respawn, hence we put the respawn health value
            // Or it's not a respawn but a follow up hence we put the current health value
            if(stats.isDead())
                stats.setHealth(pet.getPetStats().getRespawnHealth());
            else
                stats.setHealth(stats.getCurrentHealth());

        }
    }

    @EventHandler
    // Handles player giving taming food to its pet
    public void taming(PetTamedByPlayerEvent e)
    {
        Pet pet = e.getPet();
        pet.setTamingProgress(e.getPetFood().getOperator().get(pet.getTamingProgress(), e.getPetFood().getPower()));
    }

}
