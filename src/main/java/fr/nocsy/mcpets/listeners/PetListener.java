package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;

public class PetListener implements Listener {

    private HashMap<UUID, Pet> reconnectionPets = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEntityEvent e)
    {
        if(!GlobalConfig.getInstance().isRightClickToOpen())
            return;

        Player p = e.getPlayer();

        if(GlobalConfig.getInstance().isSneakMode() && !p.isSneaking())
            return;

        Entity ent = e.getRightClicked();

        Pet pet = Pet.getFromEntity(ent);

        if(pet != null &&
            (pet.getOwner().equals(p.getUniqueId()) || p.isOp()))
        {
            PetInteractionMenu menu = new PetInteractionMenu(pet);
            p.setMetadata("AlmPetInteracted", new FixedMetadataValue(MCPets.getInstance(), pet));
            menu.open(p);
        }
    }

    @EventHandler
    public void interact(EntityDamageByEntityEvent e)
    {
        if(!GlobalConfig.getInstance().isLeftClickToOpen())
            return;

        if(!(e.getDamager() instanceof Player))
            return;

        Player p = (Player)e.getDamager();

        if(GlobalConfig.getInstance().isSneakMode() && !p.isSneaking())
            return;

        Entity ent = e.getEntity();

        Pet pet = Pet.getFromEntity(ent);

        if(pet != null &&
                (pet.getOwner().equals(p.getUniqueId()) || p.isOp()))
        {
            PetInteractionMenu menu = new PetInteractionMenu(pet);
            p.setMetadata("AlmPetInteracted", new FixedMetadataValue(MCPets.getInstance(), pet));
            menu.open(p);
            e.setCancelled(true);
            e.setDamage(0);
        }
    }

    @EventHandler
    public void disconnectPlayer(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if(Pet.getActivePets().containsKey(p.getUniqueId()))
        {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());
            reconnectionPets.put(p.getUniqueId(), pet);
        }
    }

    @EventHandler
    public void reconnectionPlayer(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        if(reconnectionPets.containsKey(p.getUniqueId()))
        {
            Pet pet = reconnectionPets.get(p.getUniqueId());
            pet.spawn(p.getLocation());
            reconnectionPets.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void teleport(PlayerChangedWorldEvent e)
    {
        Player p = e.getPlayer();
        if(Pet.getActivePets().containsKey(p.getUniqueId()))
        {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());
            pet.despawn(PetDespawnReason.TELEPORT);
            pet.spawn(p, p.getLocation());
        }

    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e)
    {
        Player p = e.getPlayer();
        if(Pet.getActivePets().containsKey(p.getUniqueId()))
        {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());
            pet.dismount(p);
        }

    }

    @EventHandler
    public void riding(EntityDamageEvent e)
    {
        if(!GlobalConfig.getInstance().isDismountOnDamaged())
            return;

        if(e.getEntity() instanceof Player)
        {
            Player p = (Player) e.getEntity();
            if(p.isInsideVehicle() && Pet.fromOwner(p.getUniqueId()) != null)
            {
                Pet pet = Pet.fromOwner(p.getUniqueId());
                pet.dismount(p);
            }
            return;
        }

    }

    /**
     * Wtf is this doing seriously ? Makes no sense.
     * @param e
     */
    @EventHandler
    public void damaged(EntityDamageEvent e)
    {
        if(e.getEntity() instanceof Player)
        {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if(pet != null && pet.isInvulnerable())
            {
                e.setDamage(0);
                e.setCancelled(true);
            }
            return;
        }
    }

    @EventHandler
    public void gamemode(PlayerGameModeChangeEvent e)
    {
        UUID uuid = e.getPlayer().getUniqueId();
        if(Pet.getActivePets().containsKey(uuid) && e.getNewGameMode() == GameMode.SPECTATOR)
        {
            Pet pet = Pet.getActivePets().get(uuid);
            pet.despawn(PetDespawnReason.GAMEMODE);
        }
    }

    /**
     * Handle random despawn
     * @param e
     */
    @EventHandler
    public void despawn(MythicMobDespawnEvent e)
    {
        if(e.getEntity() != null)
        {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if(pet != null)
            {
                if(!pet.isRemoved())
                {
                    pet.despawn(PetDespawnReason.MYTHICMOBS);
                    Player owner = Bukkit.getPlayer(pet.getOwner());
                    if(owner != null)
                    {
                        Language.REVOKED.sendMessage(owner);
                    }
                }
            }
        }
    }

    /**
     * Handle death of the pet
     * @param e
     */
    @EventHandler
    public void death(MythicMobDeathEvent e)
    {
        if(e.getEntity() != null)
        {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if(pet != null)
            {
                if(!pet.isRemoved())
                {
                    pet.despawn(PetDespawnReason.DEATH);
                    Player owner = Bukkit.getPlayer(pet.getOwner());
                    if(owner != null)
                    {
                        Language.REVOKED.sendMessage(owner);
                    }
                }
            }
        }
    }

}
