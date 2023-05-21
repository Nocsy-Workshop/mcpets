package fr.nocsy.mcpets.listeners;

import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.EntityMountPetEvent;
import fr.nocsy.mcpets.events.PetSpawnEvent;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class PetListener implements Listener {

    private final HashMap<UUID, Pet> reconnectionPets = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEntityEvent e) {
        if (!GlobalConfig.getInstance().isRightClickToOpen())
            return;

        Player p = e.getPlayer();

        if (GlobalConfig.getInstance().isSneakMode() && !p.isSneaking())
            return;

        // Do not open the menu if the player has a signal stick
        if(GlobalConfig.getInstance().isDisableInventoryWhileHoldingSignalStick())
        {
            ItemStack it = p.getInventory().getItemInMainHand();
            if(Items.isSignalStick(it))
                return;
        }

        //If it's pet food in the main hand then do not open the menu
        if(PetFood.getFromItem(p.getInventory().getItemInMainHand()) != null)
        {
            return;
        }

        Entity ent = e.getRightClicked();

        Pet pet = Pet.getFromEntity(ent);

        if (pet != null && pet.getOwner() != null &&
                pet.getOwner().equals(p.getUniqueId())) {
            PetInteractionMenu menu = new PetInteractionMenu(pet, p.getUniqueId());
            pet.setLastInteractedWith(p);
            menu.open(p);
        }
        if(pet != null && p.isOp())
        {
            PetInteractionMenu menu = new PetInteractionMenu(pet, p.getUniqueId());
            pet.setLastOpInteracted(p);
            menu.open(p);
        }
    }

    @EventHandler
    public void interact(EntityDamageByEntityEvent e) {
        if (!GlobalConfig.getInstance().isLeftClickToOpen())
            return;

        if (!(e.getDamager() instanceof Player))
            return;

        Player p = (Player) e.getDamager();

        if (GlobalConfig.getInstance().isSneakMode() && !p.isSneaking())
            return;

        Entity ent = e.getEntity();

        Pet pet = Pet.getFromEntity(ent);

        if (pet != null && pet.getOwner() != null &&
                pet.getOwner().equals(p.getUniqueId())) {
            PetInteractionMenu menu = new PetInteractionMenu(pet, p.getUniqueId());
            pet.setLastInteractedWith(p);
            menu.open(p);
            e.setCancelled(true);
            e.setDamage(0);
        }
        if(pet != null && p.isOp())
        {
            PetInteractionMenu menu = new PetInteractionMenu(pet, p.getUniqueId());
            pet.setLastOpInteracted(p);
            menu.open(p);
            e.setCancelled(true);
            e.setDamage(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disconnectPlayer(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Pet.getActivePets().containsKey(p.getUniqueId()) && GlobalConfig.getInstance().isSpawnPetOnReconnect()) {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());

            // Saving the database for bungee support
            if(GlobalConfig.getInstance().isDatabaseSupport()) {
                Databases.savePlayerData(p.getUniqueId());
            }

            // delay before despawning the pet and adding it to reconnectionPets
            Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), () -> {
                pet.despawn(PetDespawnReason.DISCONNECTION);
                reconnectionPets.put(p.getUniqueId(), pet);
            }, 20L);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void reconnectionPlayer(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        // delay before loading the player data from the database
        Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), () -> {
            // Load the player data from the database for bungee support
            if (GlobalConfig.getInstance().isDatabaseSupport()) {
                PlayerData.reloadAll(p.getUniqueId());
            }

            if (reconnectionPets.containsKey(p.getUniqueId())) {
                Pet pet = reconnectionPets.get(p.getUniqueId());
                pet.spawn(p.getLocation(), true);
                reconnectionPets.remove(p.getUniqueId());

                // Save the player data after reconnecting
                if (GlobalConfig.getInstance().isDatabaseSupport()) {
                    PlayerData.saveDB();
                }
            }
        }, 20L);
    }



    @EventHandler
    public void teleport(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (Pet.getActivePets().containsKey(p.getUniqueId())) {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());
            if(pet.getTamingProgress() < 1)
                return;
            pet.despawn(PetDespawnReason.TELEPORT);
            new BukkitRunnable() {
                @Override
                public void run() {
                    pet.spawn(p, p.getLocation());
                }
            }.runTaskLater(MCPets.getInstance(), 20L);
        }

    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (Pet.getActivePets().containsKey(p.getUniqueId())) {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());
            pet.dismount(p);
        }

    }

    @EventHandler
    public void riding(EntityDamageEvent e) {
        if (!GlobalConfig.getInstance().isDismountOnDamaged())
            return;

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Pet pet = Pet.fromOwner(p.getUniqueId());
            if(pet != null && pet.hasRider(p))
            {
                pet.dismount(p);
            }

        }

    }

    /**
     * Wtf is this doing seriously ? Makes no sense.
     *
     * @param e
     */
    @EventHandler
    public void damaged(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if (pet != null && pet.isInvulnerable()) {
                e.setDamage(0);
                e.setCancelled(true);
            }
            return;
        }
    }

    @EventHandler
    public void gamemode(PlayerGameModeChangeEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (Pet.getActivePets().containsKey(uuid) && e.getNewGameMode() == GameMode.SPECTATOR) {
            Pet pet = Pet.getActivePets().get(uuid);
            pet.despawn(PetDespawnReason.GAMEMODE);
        }
    }

    /**
     * Handle random despawn
     *
     * @param e
     */
    @EventHandler
    public void despawn(MythicMobDespawnEvent e) {
        if (e.getEntity() != null) {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if (pet != null) {
                if (!pet.isRemoved()) {
                    pet.despawn(PetDespawnReason.MYTHICMOBS);
                    Player owner = Bukkit.getPlayer(pet.getOwner());
                    if (owner != null) {
                        Language.REVOKED_UNKNOWN.sendMessage(owner);
                    }
                }
            }
        }
    }

    /**
     * Handle death of the pet
     *
     * @param e
     */
    @EventHandler
    public void death(MythicMobDeathEvent e) {
        if (e.getEntity() != null) {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if (pet != null) {
                if (!pet.isRemoved()) {
                    pet.despawn(PetDespawnReason.DEATH);
                    Player owner = Bukkit.getPlayer(pet.getOwner());
                    if (owner != null) {
                        Language.REVOKED.sendMessage(owner);
                    }
                }
            }
        }
    }

    /**
     * Blacklisted world system
     * @param e
     */
    @EventHandler
    public void blacklistedWorld(PetSpawnEvent e)
    {
        if(GlobalConfig.getInstance().hasBlackListedWorld(e.getWhere().getWorld().getName()))
        {
            e.setCancelled(true);
            Player p = Bukkit.getPlayer(e.getPet().getOwner());
            if(p != null)
            {
                Language.BLACKLISTED_WORLD.sendMessage(p);
            }
        }
    }

    @EventHandler
    public void despawnOnDismount(ModelDismountEvent e)
    {
        if(e.getVehicle() == null || e.getVehicle().getBase() == null)
            return;

        // Running this as sync coz we fetch an entity
        new BukkitRunnable() {
            @Override
            public void run() {
                Pet pet = Pet.getFromEntity(Bukkit.getEntity(e.getVehicle().getBase().getUniqueId()));
                if(pet != null && pet.isDespawnOnDismount())
                {
                    pet.despawn(PetDespawnReason.DISMOUNT);
                }
            }
        }.runTask(MCPets.getInstance());

    }

    @EventHandler
    public void cancelDefaultTaming(EntityTameEvent e)
    {
        if(Pet.getFromEntity(e.getEntity()) != null)
        {
            // Cancel the event, so it doesn't give other type of item by default to the anchor
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void mountingPet(EntityMountPetEvent e)
    {
        if(e.getEntity() == null)
            return;

        // if it's not the owner or an admin mounting the pet, then we cancel it
        if(!e.getPet().getOwner().equals(e.getEntity().getUniqueId()) &&
            !e.getEntity().hasPermission(PPermission.ADMIN.getPermission()))
        {
            e.setCancelled(true);
            Debugger.send("§c" + e.getEntity().getName() + " can not mount " + e.getPet().getId() + " as he's not the owner, nor an admin.");
        }
        // If user doesn't have the perm to mount the pet, cancel the event
        if(e.getPet().getMountPermission() != null && !e.getEntity().hasPermission(e.getPet().getMountPermission()))
        {
            e.setCancelled(true);
            Language.CANT_MOUNT_PET_YET.sendMessage(e.getEntity());
        }
    }

    @EventHandler
    public void mountingPet(ModelMountEvent e)
    {
        if(e.getPassenger() == null)
            return;

        if(e.getVehicle() == null || e.getVehicle().getBase() == null)
            return;

        Entity entity = e.getVehicle().getBase().getWorld().getEntity(e.getVehicle().getBase().getUniqueId());
        if(entity == null)
            return;
        Pet pet = Pet.getFromEntity(entity);
        Entity player = e.getPassenger();

        if(pet == null)
            return;

        // if it's not the owner or an admin mounting the pet, then we cancel it
        if(e.getSeat().isDriverBone() &&
                !pet.getOwner().equals(player.getUniqueId()) &&
                !player.hasPermission(PPermission.ADMIN.getPermission()))
        {
            e.setCancelled(true);
            Debugger.send("§c" + player.getName() + " can not mount model of " + pet.getId() + " as he's not the owner, nor an admin.");
        }

        // If user doesn't have the perm to mount the pet, cancel the event
        if(pet.getMountPermission() != null
                && !player.hasPermission(pet.getMountPermission())
                && e.getSeat().isDriverBone())
        {
            e.setCancelled(true);
            Language.CANT_MOUNT_PET_YET.sendMessage(player);
        }
    }

}
