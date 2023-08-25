package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.*;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class LivingPetsListener implements Listener {

    //--------- HANDLERS TO TRIGGER CUSTOM EVENTS ---------//

    @EventHandler(priority = EventPriority.LOWEST)
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

    @EventHandler(priority = EventPriority.LOWEST)
    // Trigger for the PetDamagedEvent
    public void petDamagedHandler(EntityDamageByEntityEvent e)
    {
        Entity entity = e.getEntity();
        Pet pet = Pet.getFromEntity(entity);
        if(pet != null && pet.getPetStats() != null)
        {
            PetDamagedByEntityEvent event = new PetDamagedByEntityEvent(pet, e.getDamager(), e.getDamage(), true);
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
    // handles the pet food events
    public void petFoodPlayerEvent(PlayerInteractAtEntityEvent e)
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
                // Check if the pet food is compatible with that pet
                if(!petFood.isCompatibleWithPet(pet))
                {
                    e.setCancelled(true);
                    Language.PET_DOESNT_EAT.sendMessage(p);
                    return;
                }

                // If it's health food then trigger the healing event
                if(petFood.getType().equals(PetFoodType.HEALTH))
                {
                    // Cancel the interaction event
                    e.setCancelled(true);
                    PetFedByPlayerEvent event = new PetFedByPlayerEvent(pet, p, petFood);
                    Utils.callEvent(event);
                    return;
                }

                if(petFood.getType().equals(PetFoodType.EVOLUTION) &&
                        pet.getOwner() != null &&
                        pet.getOwner().equals(p.getUniqueId()))
                {
                    // Cancel the interaction event
                    e.setCancelled(true);
                    // This may or may not work actually depending on the permissions of the player
                    // We only withdraw the item if the evolution was allowed
                    if(petFood.apply(pet, p))
                        petFood.consume(p);
                    return;
                }

                if(petFood.getType().equals(PetFoodType.EXP) &&
                        pet.getOwner() != null &&
                        pet.getOwner().equals(p.getUniqueId()))
                {
                    // Cancel the interaction event
                    e.setCancelled(true);
                    // This will indirectly create a PetGainExperienceEvent
                    if(petFood.apply(pet, p))
                        petFood.consume(p);
                    return;
                }

                // The pet has never been tamed, so let's tame it
                // Or it's being tamed by the same player
                // Make sure as well the pet is not already fully tamed and receptive to the food
                if(petFood.getType().equals(PetFoodType.TAME) &&
                        (pet.getOwner() == null || pet.getOwner().equals(p.getUniqueId())) &&
                        pet.getTamingProgress() < 1)
                {
                    // Cancel the interaction event
                    e.setCancelled(true);

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
        if(damager instanceof Player)
        {
            Player p = ((Player)damager);
            Pet pet = Pet.getFromEntity(ent);
            if(pet != null && pet.getOwner() != null)
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
        // Must run ASync otherwise it's not updating
        new BukkitRunnable() {
            @Override
            public void run() {
                stats.updateHealth();
            }
        }.runTaskLater(MCPets.getInstance(), 1L);
    }

    @EventHandler
    // Handles if the pet can be spawned or not according to the timers
    public void attemptToSpawn(PetSpawnEvent e)
    {
        Pet pet = e.getPet();

        // If the global timer is enabled, make sure no other pet has a running timer
        if(GlobalConfig.getInstance().isGlobalRespawnCooldown())
        {
            PetStats stats = PetStats.getPetStatsOnRespawnTimerRunning(pet.getOwner());
            if(stats != null)
            {
                e.setCancelled(true);
                PetDespawnEvent petDespawnEvent = new PetDespawnEvent(pet, PetDespawnReason.RESPAWN_TIMER);
                Utils.callEvent(petDespawnEvent);

                Player p = Bukkit.getPlayer(pet.getOwner());

                if(p != null)
                    Language.GLOBAL_RESPAWN_TIMER_RUNNING.sendMessageFormated(p,
                            new FormatArg("%timeLeft%", Integer.toString(stats.getRespawnTimer().getRemainingTime())),
                            new FormatArg("%cooldown%", Integer.toString(stats.getRespawnTimer().getCooldown())));
                return;
            }
        }
        // Check that the pet has no running timer
        else if(pet.getPetStats() != null)
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
                                                    new FormatArg("%timeLeft%", Integer.toString(stats.getRespawnTimer().getRemainingTime())),
                                                    new FormatArg("%cooldown%", Integer.toString(stats.getRespawnTimer().getCooldown())));
            }
            else if(stats.getRevokeTimer().isRunning())
            {
                e.setCancelled(true);
                PetDespawnEvent petDespawnEvent = new PetDespawnEvent(pet, PetDespawnReason.REVOKE_TIMER);
                Utils.callEvent(petDespawnEvent);

                Player p = Bukkit.getPlayer(pet.getOwner());

                if(p != null)
                    Language.REVOKE_TIMER_RUNNING.sendMessageFormated(p,
                            new FormatArg("%timeLeft%", Integer.toString(stats.getRevokeTimer().getRemainingTime())),
                            new FormatArg("%cooldown%", Integer.toString(stats.getRevokeTimer().getCooldown())));
            }

        }
    }

    @EventHandler
    // Launch the respawn timer on death
    public void respawnCooldownHandler(PetDeathEvent e)
    {
        Pet pet = e.getPet();
        // Set the pet as dead
        pet.getPetStats().setDead();
        // Start the respawn timer
        pet.getPetStats().launchRespawnTimer();
    }

    @EventHandler
    // Revoke cooldown timer
    public void revokeCooldownHandler(PetDespawnEvent e)
    {
        Pet pet = e.getPet();
        // make sure it corresponds to a revoke
        if(e.getReason().equals(PetDespawnReason.REVOKE) &&
            pet.getPetStats() != null)
        {
            // Launch the revoke timer
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

            // Launch the regeneration timer
            stats.launchRegenerationTimer();

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
        PetFood petFood = e.getPetFood();

        // We try to apply the pet food but if it failed we stop there
        if(petFood.apply(pet, e.getPlayer()))
            petFood.consume(e.getPlayer());
        else
            return;

        // Announce it to the player
        StringBuilder progressBar = new StringBuilder();
        if(GlobalConfig.getInstance().getTamingBarSize() > 0)
        {
            // Size of the progress bar in the hovering
            int progressBarSize = GlobalConfig.getInstance().getTamingBarSize();

            double ratio = pet.getTamingProgress();
            int indexProgress = Math.min(progressBarSize, (int)(ratio*progressBarSize + 0.5));

            for(int i = 0; i < progressBarSize; i++)
            {
                if(i < indexProgress)
                    progressBar.append(GlobalConfig.getInstance().getTamingColorDone() +
                            GlobalConfig.getInstance().getTamingSymbol() +
                            GlobalConfig.getInstance().getTamingColorLeft());
                else
                    progressBar.append(GlobalConfig.getInstance().getTamingColorLeft() +
                            GlobalConfig.getInstance().getTamingSymbol() +
                            GlobalConfig.getInstance().getTamingColorLeft());
            }
        }

        GlobalConfig.getInstance().getTamingAnnouncementType()
                .announce(e.getPlayer(),
                        Language.PET_TAMING_PROGRESS.getMessageFormatted(
                                new FormatArg("%progress%", Integer.toString((int)(pet.getTamingProgress()*100))),
                                new FormatArg("%progressbar%", progressBar.toString())));
    }

    @EventHandler
    public void feedPet(PetFedByPlayerEvent e)
    {
        Pet pet = e.getPet();
        if(pet.getPetStats() != null)
        {
            // Remove one unit of the item or replace it by void if it was applied
            if(e.getPetFood().apply(pet, e.getPlayer()))
                e.getPetFood().consume(e.getPlayer());
        }
    }

    /**
     * General method to unlock a pet out of the item in the main hand of the player
     * @param p
     */
    public void unlockPet(Player p)
    {
        ItemStack it = p.getInventory().getItemInMainHand();
        PetFood petFood = PetFood.getFromItem(it);

        // Check the petFood and make sure it's an unlock item
        if(petFood != null &&
                petFood.getType() == PetFoodType.UNLOCK)
        {
            // Check that the pet associated to the pet food exists
            Pet pet = Pet.getFromId(petFood.getUnlockedPet());
            if(pet == null)
                return;

            // Apply and if it works consume the item
            if(petFood.apply(pet, p))
                petFood.consume(p);
        }
    }

    @EventHandler
    public void unlockPet(PlayerInteractEvent e)
    {
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        unlockPet(e.getPlayer());
    }
    @EventHandler
    public void unlockPet(PlayerInteractAtEntityEvent e)
    {
        unlockPet(e.getPlayer());
    }

}
