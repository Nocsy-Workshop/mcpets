package fr.nocsy.mcpets.listeners;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.utils.debug.Debugger;
import fr.nocsy.mcpets.events.PetSpawnEvent;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.events.EntityMountPetEvent;
import fr.nocsy.mcpets.data.flags.DismountPetFlag;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.velocity.VelocitySyncManager;
import fr.nocsy.mcpets.events.PetOwnerInteractEvent;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
import fr.nocsy.mcpets.data.inventories.MountInteractionMenu;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;

public class PetListener implements Listener {

    private final Map<UUID, String> reconnectionPets = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEntityEvent e) {
        if (!GlobalConfig.getInstance().isRightClickToOpen()) return;

        Player p = e.getPlayer();

        if (GlobalConfig.getInstance().isSneakMode() && !p.isSneaking()) return;

        // Do not open the menu if the player has a signal stick
        if (GlobalConfig.getInstance().isDisableInventoryWhileHoldingSignalStick()) {
            ItemStack it = p.getInventory().getItemInMainHand();
            if (Items.isSignalStick(it)) return;
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
                MountInteractionMenu menu = new MountInteractionMenu(pet, p.getUniqueId());
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
                MountInteractionMenu menu = new MountInteractionMenu(pet, pet.getOwner());
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
        if (!GlobalConfig.getInstance().isLeftClickToOpen()) return;

        if (!(e.getDamager() instanceof Player p)) return;

        if (GlobalConfig.getInstance().isSneakMode() && !p.isSneaking()) return;

        Entity ent = e.getEntity();

        Pet pet = Pet.getFromEntity(ent);

        if (pet != null && pet.getOwner() != null && pet.getOwner().equals(p.getUniqueId())) {
            if (pet.isMount()) {
                MountInteractionMenu menu = new MountInteractionMenu(pet, p.getUniqueId());
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
                MountInteractionMenu menu = new MountInteractionMenu(pet, pet.getOwner());
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

        List<String> activePetIds = new ArrayList<>();
        Map<String, String> activeSkinIds = new HashMap<>();

        // Create a copy to avoid ConcurrentModificationException when despawning modifies the list
        List<Pet> pets = Pet.getActivePetsForOwner(uuid);
        for (Pet pet : new ArrayList<>(pets)) {
            // Capture skin data before despawn clears it
            PetSkin activeSkin = pet.getActiveSkin();
            pet.despawn(PetDespawnReason.DISCONNECTION);
            if (p.hasPermission(pet.getPermission())) {
                String encoded = PlayerData.encodeActivePet(pet.getId(),
                        activeSkin != null ? activeSkin.getPathId() : null);
                reconnectionPets.putIfAbsent(uuid, encoded);
                activePetIds.add(pet.getId());
                if (activeSkin != null) {
                    activeSkinIds.put(pet.getId(), activeSkin.getPathId());
                }
                if (GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
                    PlayerData pd = PlayerData.get(uuid);
                    if (pd != null) {
                        pd.setLastActivePet(encoded);
                        pd.save();
                    }
                }
            }
        }
        // Velocity: persist or clear active pet record so destination server restores correctly
        if (GlobalConfig.getInstance().isVelocityEnabled()
                && GlobalConfig.getInstance().isDatabaseSupport()) {
            if (!activePetIds.isEmpty()) {
                final List<String> petIdsToSave = new ArrayList<>(activePetIds);
                final Map<String, String> skinIdsToSave = new HashMap<>(activeSkinIds);
                Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(),
                        () -> Databases.saveActivePet(uuid, petIdsToSave, skinIdsToSave));
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(),
                        () -> Databases.clearActivePet(uuid));
            }
        }
        if (pets.isEmpty() && GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
            PlayerData pd = PlayerData.get(uuid);
            if (pd != null) {
                pd.setLastActivePet("");
                pd.save();
            }
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
                boolean isLiveSwitch = VelocitySyncManager.isPlayerSwitching(uuid);
                VelocitySyncManager.clearSwitchingPlayer(uuid);
                reconnectionPets.remove(uuid); // discard — DB owns the state

                // Load from DB asynchronously to avoid blocking the main thread
                Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () -> {
                    Databases.ActivePetRecord record = Databases.loadActivePet(uuid);
                    if (record == null) return;
                    if (isLiveSwitch) {
                        Databases.clearActivePet(uuid);
                    }
                    // Return to main thread to spawn pets (skin restoration uses static maps)
                    Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
                        if (!p.isOnline()) return;
                        for (String petId : record.getPetIds()) {
                            Pet template = Pet.getFromId(petId);
                            if (template == null) continue;
                            Pet velocityPet = template.copy();
                            velocityPet.setCheckPermission(false);
                            velocityPet.setOwner(uuid);
                            // Restore active skin if one was saved
                            restoreSkin(p, velocityPet, record.getSkinId(petId));
                            velocityPet.spawn(p.getLocation(), true);
                        }
                    });
                });
                return; // never fall through to reconnectionPets when Velocity is enabled
            }

            // Velocity disabled: use the local reconnection map (original behavior).
            if (reconnectionPets.containsKey(uuid)) {
                String stored = reconnectionPets.get(uuid);
                Pet pet = Pet.getFromId(PlayerData.decodeActivePetId(stored));
                if (pet == null) return;

                if (!p.hasPermission(pet.getPermission())) {
                    reconnectionPets.remove(uuid);
                    return;
                }
                pet = pet.copy();
                pet.setCheckPermission(false);
                pet.setOwner(uuid);
                restoreSkin(p, pet, PlayerData.decodeActiveSkinId(stored));
                pet.spawn(p.getLocation(), true);
                reconnectionPets.remove(uuid);
            } else if (GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
                PlayerData pd = PlayerData.get(uuid);
                String stored = pd.getLastActivePet();
                String lastPetId = PlayerData.decodeActivePetId(stored);
                if (lastPetId != null && !lastPetId.isEmpty()) {
                    Pet pet = Pet.getFromId(lastPetId);
                    if (pet != null) {
                        pet = pet.copy();
                        pet.setCheckPermission(false);
                        pet.setOwner(uuid);
                        restoreSkin(p, pet, PlayerData.decodeActiveSkinId(stored));
                        pet.spawn(p.getLocation(), true);
                    }
                }
            }
        }, 20L);
    }

    private void restoreSkin(Player p, Pet pet, String skinPathId) {
        if (skinPathId == null || skinPathId.isEmpty()) return;
        for (PetSkin skin : PetSkin.getSkins(pet)) {
            if (skinPathId.equals(skin.getPathId())) {
                String perm = skin.getPermission();
                if (perm == null || perm.isEmpty() || p.hasPermission(perm)) {
                    pet.setActiveSkin(skin);
                }
                return;
            }
        }
    }

    @EventHandler
    public void teleport(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        for (Pet pet : new ArrayList<>(Pet.getActivePetsForOwner(p.getUniqueId()))) {
            if (pet.getTamingProgress() < 1) continue;
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
        for (Pet pet : Pet.getActivePetsForOwner(p.getUniqueId())) {
            pet.dismount(p);
        }
    }

    @EventHandler
    public void riding(EntityDamageEvent e) {
        if (!GlobalConfig.getInstance().isDismountOnDamaged()) return;

        if (e.getEntity() instanceof Player p) {
            if (e instanceof EntityDamageByEntityEvent damageEvent) {
                if (damageEvent.getDamager() instanceof Player) {
                    return;
                }
            }
            
            if (GlobalConfig.getInstance().isDismountOnDamagedExcludePlayers()) return;

            Pet pet = Pet.fromOwner(p.getUniqueId());
            if (pet != null && pet.hasMount(p)) {
                pet.dismount(p);
            }
        }
    }

    @EventHandler
    public void damaged(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Pet pet = Pet.getFromEntity(e.getEntity());
        // Cosmetic pets shouldn't be damageable
        if (pet != null && pet.getPetStats() == null) {
            e.setDamage(0);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void gamemode(PlayerGameModeChangeEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (e.getNewGameMode() != GameMode.SPECTATOR) return;
        for (Pet pet : new ArrayList<>(Pet.getActivePetsForOwner(uuid))) {
            pet.despawn(PetDespawnReason.GAMEMODE);
        }
    }

    /**
     * Handle random despawn
     */
    private final Map<UUID, Integer> repeatRespawn = new HashMap<>();
    @EventHandler
    public void despawn(MythicMobDespawnEvent e) {
        if (e.getEntity() == null) return;

        Pet pet = Pet.getFromEntity(e.getEntity());
        if (pet == null) return;
        if (pet.isRemoved()) return;

        pet.despawn(PetDespawnReason.MYTHICMOBS);

        UUID ownerUUID = pet.getOwner();
        if (ownerUUID == null) return;

        Player owner = Bukkit.getPlayer(pet.getOwner());
        if (owner == null) return;
        if (repeatRespawn.containsKey(ownerUUID) && repeatRespawn.get(ownerUUID) == 3) {
            Language.REVOKED_UNKNOWN.sendMessage(owner);
            repeatRespawn.remove(owner.getUniqueId());
            return;
        }
        int value = 1;
        if (repeatRespawn.containsKey(ownerUUID)) value = repeatRespawn.get(ownerUUID);
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

    /**
     * Handle death of the pet
     */
    @EventHandler
    public void death(MythicMobDeathEvent e) {
        if (e.getEntity() == null) return;

        Pet pet = Pet.getFromEntity(e.getEntity());
        if (pet == null) return;
        if (pet.isRemoved()) return;

        pet.despawn(PetDespawnReason.DEATH);

        if (pet.getOwner() == null) return;

        Player owner = Bukkit.getPlayer(pet.getOwner());
        if (owner != null) {
            Language.REVOKED.sendMessage(owner);
        }
    }

    /**
     * Blacklisted world system
     */
    @EventHandler
    public void blacklistedWorld(PetSpawnEvent e) {
        if (!GlobalConfig.getInstance().hasBlackListedWorld(e.getWhere().getWorld().getName())) return;

        e.setCancelled(true);
        Debugger.send("§cSpawn of §6" + e.getPet().getId() + "§c cancelled: world §6" + e.getWhere().getWorld().getName() + "§c is blacklisted.");

        Player p = Bukkit.getPlayer(e.getPet().getOwner());
        if (p != null) {
            Language.BLACKLISTED_WORLD.sendMessage(p);
        }
    }

    @EventHandler
    public void cancelDefaultTaming(EntityTameEvent e) {
        if (Pet.getFromEntity(e.getEntity()) == null) return;

        // Cancel the event, so it doesn't give other type of item by default to the anchor
        e.setCancelled(true);
    }

    @EventHandler
    public void mountingPet(EntityMountPetEvent e) {
        if (e.getEntity() == null) return;

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
            if (pet.isDespawnOnDismount()) pet.despawn(PetDespawnReason.FLAG);
        }
    }


    @EventHandler
    public void fastMount(PlayerInteractEntityEvent e){
        // Check if inventories should be opening instead of fast mounting
        if (GlobalConfig.getInstance().isRightClickToOpen()) return;

        Player p = e.getPlayer();
        if (GlobalConfig.getInstance().isSneakMode() && p.isSneaking()) return;

        // Do not mount if the player has a signal stick
        if (GlobalConfig.getInstance().isDisableFastMountWhileHoldingSignalStick()) {
            ItemStack it = p.getInventory().getItemInMainHand();
            if (Items.isSignalStick(it)) return;
        }
        //If it's pet food in the main hand then do not mount
        if (PetFood.getFromItem(p.getInventory().getItemInMainHand()) != null) {
            return;
        }

        if (!GlobalConfig.getInstance().isFastMount()) return;

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
