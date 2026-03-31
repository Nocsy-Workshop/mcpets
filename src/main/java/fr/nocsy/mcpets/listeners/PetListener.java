package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.flags.DismountPetFlag;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.inventories.MountInteractionMenu;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.EntityMountPetEvent;
import fr.nocsy.mcpets.velocity.VelocitySyncManager;
import fr.nocsy.mcpets.events.PetOwnerInteractEvent;
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
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PetListener implements Listener {

    private final HashMap<UUID, String> reconnectionPets = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEntityEvent e) {
        if (!GlobalConfig.getInstance().isRightClickToOpen())
            return;

        Player p = e.getPlayer();

        if (GlobalConfig.getInstance().isSneakMode() && !p.isSneaking())
            return;

        // Do not open the menu if the player has a signal stick
        if (GlobalConfig.getInstance().isDisableInventoryWhileHoldingSignalStick()) {
            ItemStack it = p.getInventory().getItemInMainHand();
            if (Items.isSignalStick(it))
                return;
        }

        //If it's pet food in the main hand then do not open the menu
        if (PetFood.getFromItem(p.getInventory().getItemInMainHand()) != null) {
            return;
        }

        Entity ent = e.getRightClicked();

        Pet pet = Pet.getFromEntity(ent);

        if (pet != null && pet.getOwner() != null &&
                pet.getOwner().equals(p.getUniqueId())) {
            PetOwnerInteractEvent event = new PetOwnerInteractEvent(pet);
            Utils.callEvent(event);
            if (event.isCancelled()) return;

            // Check if this is a mount and open the appropriate menu
            if (pet.isMount()) {
                MountInteractionMenu menu = 
                    new MountInteractionMenu(pet, p.getUniqueId());
                pet.setLastInteractedWith(p);
                menu.open(p);
            } else {
                PetInteractionMenu menu = new PetInteractionMenu(pet, p.getUniqueId());
                pet.setLastInteractedWith(p);
                menu.open(p);
            }
        }
        if (pet != null && p.isOp()) {
            // Check if this is a mount and open the appropriate menu
            if (pet.isMount()) {
                MountInteractionMenu menu = 
                    new MountInteractionMenu(pet, pet.getOwner());
                pet.setLastOpInteracted(p);
                menu.open(p);
            } else {
                PetInteractionMenu menu = new PetInteractionMenu(pet, pet.getOwner());
                pet.setLastOpInteracted(p);
                menu.open(p);
            }
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
            if (pet.isMount()) {
                MountInteractionMenu menu = 
                    new MountInteractionMenu(pet, p.getUniqueId());
                pet.setLastInteractedWith(p);
                menu.open(p);
            } else {
                PetInteractionMenu menu = new PetInteractionMenu(pet, p.getUniqueId());
                pet.setLastInteractedWith(p);
                menu.open(p);
            }
            e.setCancelled(true);
            e.setDamage(0);
        }
        if (pet != null && p.isOp()) {
            // Check if this is a mount and open the appropriate menu
            if (pet.isMount()) {
                MountInteractionMenu menu = 
                    new MountInteractionMenu(pet, pet.getOwner());
                pet.setLastOpInteracted(p);
                menu.open(p);
            } else {
                PetInteractionMenu menu = new PetInteractionMenu(pet, pet.getOwner());
                pet.setLastOpInteracted(p);
                menu.open(p);
            }
            e.setCancelled(true);
            e.setDamage(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disconnectPlayer(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        List<Pet> pets = Pet.getActivePetsForOwner(uuid);
        // Create a copy to avoid ConcurrentModificationException when despawning modifies the list
        List<String> activePetIds = new ArrayList<>();
        for (Pet pet : List.copyOf(pets)) {
            pet.despawn(PetDespawnReason.DISCONNECTION);
            if (p.hasPermission(pet.getPermission())) {
                reconnectionPets.put(uuid, pet.getId());
                activePetIds.add(pet.getId());
                if (GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
                    PlayerData pd = PlayerData.get(uuid);
                    pd.setLastActivePet(pet.getId());
                    pd.save();
                }
            }
        }
        // Velocity: persist or clear active pet record so destination server restores correctly
        if (GlobalConfig.getInstance().isVelocityEnabled()
                && GlobalConfig.getInstance().isDatabaseSupport()) {
            if (!activePetIds.isEmpty()) {
                Databases.saveActivePet(uuid, activePetIds);
            } else {
                Databases.clearActivePet(uuid);
            }
        }
        if (pets.isEmpty() && GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
            PlayerData pd = PlayerData.get(uuid);
            pd.setLastActivePet("");
            pd.save();
        }
        // Clean up player caches from memory to prevent memory leak
        PlayerData.remove(p.getUniqueId());
        PetInventory.removePlayer(p.getUniqueId());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void reconnectionPlayer(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), () -> {
            if (GlobalConfig.getInstance().isDatabaseSupport()) {
                PlayerData.reloadAll(uuid);
            }

            if (GlobalConfig.getInstance().isVelocityEnabled()
                    && GlobalConfig.getInstance().isDatabaseSupport()) {

                // When Velocity is enabled the DB is the ONLY source of truth.
                // reconnectionPets on any given server reflects the last time the player
                // disconnected from THAT server's JVM — it goes stale the moment the player
                // visits another server. Never fall through to it when Velocity is on.
                boolean isLiveSwitch = VelocitySyncManager.isPlayerSwitching(uuid);
                VelocitySyncManager.clearSwitchingPlayer(uuid);
                reconnectionPets.remove(uuid); // discard — DB owns the state

                Databases.ActivePetRecord record = Databases.loadActivePet(uuid);
                if (record != null) {
                    // The DB record is the authoritative last-known pet state.
                    // Always restore it regardless of how long ago it was written —
                    // the SwitchWindow staleness check belongs only in isPlayerSwitching()
                    // (proxy-message timing), not here.

                    // Only clear the record on a live switch (one-time consumption).
                    // For reconnect/restart recovery, leave it intact — the quit handler
                    // will overwrite or clear it at the player's next disconnect.
                    if (isLiveSwitch) {
                        Databases.clearActivePet(uuid);
                    }
                    for (String petId : record.getPetIds()) {
                        Pet template = Pet.getFromId(petId);
                        if (template == null) continue;
                        Pet velocityPet = template.copy();
                        velocityPet.setCheckPermission(false);
                        velocityPet.setOwner(uuid);
                        Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), () -> {
                            if (p.isOnline()) velocityPet.spawn(p.getLocation(), true);
                        }, 2L);
                    }
                }
                // No DB record = player had no active pet when they left — don't spawn.
                return; // never fall through to reconnectionPets when Velocity is enabled
            }

            // Velocity disabled: use the local reconnection map (original behaviour).
            if (reconnectionPets.containsKey(uuid)) {
                Pet pet = Pet.getFromId(reconnectionPets.get(uuid));
                if (pet == null)
                    return;
                if (!p.hasPermission(pet.getPermission())) {
                    reconnectionPets.remove(uuid);
                    return;
                }
                pet = pet.copy();
                pet.setCheckPermission(false);
                pet.setOwner(uuid);
                pet.spawn(p.getLocation(), true);
                reconnectionPets.remove(uuid);
            } else if (GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
                PlayerData pd = PlayerData.get(uuid);
                String lastPetId = pd.getLastActivePet();
                if (lastPetId != null && !lastPetId.isEmpty()) {
                    Pet pet = Pet.getFromId(lastPetId);
                    if (pet != null) {
                        pet = pet.copy();
                        pet.setCheckPermission(false);
                        pet.setOwner(uuid);
                        pet.spawn(p.getLocation(), true);
                    }
                }
            }
        }, 20L);
    }

    @EventHandler
    public void teleport(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        List<Pet> pets = Pet.getActivePetsForOwner(p.getUniqueId());
        for (Pet pet : List.copyOf(pets)) {
            if (pet.getTamingProgress() < 1)
                continue;
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
        List<Pet> pets = Pet.getActivePetsForOwner(p.getUniqueId());
        for (Pet pet : List.copyOf(pets)) {
            pet.dismount(p);
        }
    }

    @EventHandler
    public void riding(EntityDamageEvent e) {
        if (!GlobalConfig.getInstance().isDismountOnDamaged())
            return;

        if (e.getEntity() instanceof Player) {
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) e;
                if (edbe.getDamager() instanceof Player) {
                    return;
                }
            }
            
            if (GlobalConfig.getInstance().isDismountOnDamagedExcludePlayers())
                return;
                
            Player p = (Player) e.getEntity();
            Pet pet = Pet.fromOwner(p.getUniqueId());
            if (pet != null && pet.hasMount(p)) {
                pet.dismount(p);
            }
        }
    }

    @EventHandler
    public void damaged(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Pet pet = Pet.getFromEntity(e.getEntity());
            // Cosmetic pets shouldn't be damageable
            if (pet != null && pet.getPetStats() == null) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void gamemode(PlayerGameModeChangeEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (e.getNewGameMode() == GameMode.SPECTATOR) {
            List<Pet> pets = Pet.getActivePetsForOwner(uuid);
            for (Pet pet : pets) {
                pet.despawn(PetDespawnReason.GAMEMODE);
            }
        }
    }

    /**
     * Handle random despawn
     */
    private HashMap<UUID, Integer> repeatRespawn = new HashMap<>();
    @EventHandler
    public void despawn(MythicMobDespawnEvent e) {
        if (e.getEntity() != null) {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if (pet != null) {
                if (!pet.isRemoved()) {
                    pet.despawn(PetDespawnReason.MYTHICMOBS);
                    UUID ownerUUID = pet.getOwner();
                    if (ownerUUID != null) {
                        Player owner = Bukkit.getPlayer(pet.getOwner());
                        if (owner == null)
                            return;
                        if (repeatRespawn.containsKey(ownerUUID) && repeatRespawn.get(ownerUUID) == 3) {
                            Language.REVOKED_UNKNOWN.sendMessage(owner);
                            repeatRespawn.remove(owner.getUniqueId());
                            return;
                        }
                        int value = 1;
                        if (repeatRespawn.containsKey(ownerUUID))
                            value = repeatRespawn.get(ownerUUID);
                        pet.spawn(owner, owner.getLocation());
                        pet.setRecurrent_spawn(false);
                        repeatRespawn.put(owner.getUniqueId(), value + 1);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                repeatRespawn.remove(owner.getUniqueId());
                            }
                        }.runTaskLater(MCPets.getInstance(), 10L);
                    }
                }
            }
        }
    }

    /**
     * Handle death of the pet
     */
    @EventHandler
    public void death(MythicMobDeathEvent e) {
        if (e.getEntity() != null) {
            Pet pet = Pet.getFromEntity(e.getEntity());
            if (pet != null) {
                if (!pet.isRemoved()) {
                    pet.despawn(PetDespawnReason.DEATH);
                    if (pet.getOwner() != null) {
                        Player owner = Bukkit.getPlayer(pet.getOwner());
                        if (owner != null) {
                            Language.REVOKED.sendMessage(owner);
                        }
                    }
                }
            }
        }
    }

    /**
     * Blacklisted world system
     */
    @EventHandler
    public void blacklistedWorld(PetSpawnEvent e) {
        if (GlobalConfig.getInstance().hasBlackListedWorld(e.getWhere().getWorld().getName())) {
            e.setCancelled(true);
            Debugger.send("§cSpawn of §6" + e.getPet().getId() + "§c cancelled: world §6" + e.getWhere().getWorld().getName() + "§c is blacklisted.");
            Player p = Bukkit.getPlayer(e.getPet().getOwner());
            if (p != null) {
                Language.BLACKLISTED_WORLD.sendMessage(p);
            }
        }
    }

    @EventHandler
    public void cancelDefaultTaming(EntityTameEvent e) {
        if (Pet.getFromEntity(e.getEntity()) != null) {
            // Cancel the event, so it doesn't give other type of item by default to the anchor
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void mountingPet(EntityMountPetEvent e) {
        if (e.getEntity() == null)
            return;

        // if it's not the owner or an admin mounting the pet, then we cancel it
        if (!e.getPet().getOwner().equals(e.getEntity().getUniqueId()) && !e.getEntity().hasPermission(PPermission.ADMIN.getPermission())) {
            e.setCancelled(true);
            Debugger.send("§c" + e.getEntity().getName() + " can not mount " + e.getPet().getId() + " as he's not the owner, nor an admin.");
        }
        // If user doesn't have the perm to mount the pet, cancel the event
        if (e.getPet().getMountPermission() != null && !e.getEntity().hasPermission(e.getPet().getMountPermission())) {
            e.setCancelled(true);
            Language.CANT_MOUNT_PET_YET.sendMessage(e.getEntity());
        }
    }

    @EventHandler
    public void checkFlagMount(EntityMountPetEvent e) {
        Pet pet = e.getPet();
        Entity player = e.getEntity();
        if (player instanceof Player &&
                GlobalConfig.getInstance().isWorldguardsupport() &&
                FlagsManager.getFlag(DismountPetFlag.NAME) != null &&
                FlagsManager.getFlag(DismountPetFlag.NAME).testState(player.getLocation())) {
            e.setCancelled(true);
            Debugger.send("[EntityMountPetEvent] §c" + player.getName() + " can not mount model of " + pet.getId() + " as a region is preventing mounting.");
            Language.NOT_MOUNTABLE_HERE.sendMessage(player);
            if (pet.isDespawnOnDismount())
                pet.despawn(PetDespawnReason.FLAG);
        }
    }


    @EventHandler
    public void fastMount(PlayerInteractEntityEvent e){
        // Check if inventories should be opening instead of fast mounting
        if (GlobalConfig.getInstance().isRightClickToOpen())
            return;
        Player p = e.getPlayer();
        if (GlobalConfig.getInstance().isSneakMode() && p.isSneaking())
            return;

        // Do not mount if the player has a signal stick
        if (GlobalConfig.getInstance().isDisableFastMountWhileHoldingSignalStick()) {
            ItemStack it = p.getInventory().getItemInMainHand();
            if (Items.isSignalStick(it))
                return;
        }
        //If it's pet food in the main hand then do not mount
        if (PetFood.getFromItem(p.getInventory().getItemInMainHand()) != null) {
            return;
        }

        if (!GlobalConfig.getInstance().isFastMount())
            return;

        Entity ent = e.getRightClicked();
        Pet pet = Pet.getFromEntity(ent);
        if (pet != null && pet.getOwner() != null &&
                pet.getOwner().equals(p.getUniqueId())) {
            PetOwnerInteractEvent event = new PetOwnerInteractEvent(pet);
            Utils.callEvent(event);
            if (event.isCancelled()) return;

            PetInteractionMenuListener.mount(p, pet);

        }

    }

}
