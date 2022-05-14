package fr.nocsy.mcpets.listeners;

import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
import fr.nocsy.mcpets.events.PetSpawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
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

        if(GlobalConfig.getInstance().isDisableInventoryWhileHoldingSignalStick())
        {
            ItemStack it = p.getInventory().getItemInMainHand();
            if(Items.isSignalStick(it))
                return;
        }

        Entity ent = e.getRightClicked();

        Pet pet = Pet.getFromEntity(ent);

        if (pet != null &&
                (pet.getOwner().equals(p.getUniqueId()) || p.isOp())) {
            PetInteractionMenu menu = new PetInteractionMenu(pet);
            p.setMetadata("AlmPetInteracted", new FixedMetadataValue(MCPets.getInstance(), pet));
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

        if (pet != null &&
                (pet.getOwner().equals(p.getUniqueId()) || p.isOp())) {
            PetInteractionMenu menu = new PetInteractionMenu(pet);
            p.setMetadata("AlmPetInteracted", new FixedMetadataValue(MCPets.getInstance(), pet));
            menu.open(p);
            e.setCancelled(true);
            e.setDamage(0);
        }
    }

    @EventHandler
    public void disconnectPlayer(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Pet.getActivePets().containsKey(p.getUniqueId())) {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());
            pet.despawn(PetDespawnReason.DISCONNECTION);
            reconnectionPets.put(p.getUniqueId(), pet);
        }
    }

    @EventHandler
    public void reconnectionPlayer(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (reconnectionPets.containsKey(p.getUniqueId())) {
            Pet pet = reconnectionPets.get(p.getUniqueId());
            pet.spawn(p.getLocation());
            reconnectionPets.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void teleport(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (Pet.getActivePets().containsKey(p.getUniqueId())) {
            Pet pet = Pet.getActivePets().get(p.getUniqueId());
            pet.despawn(PetDespawnReason.TELEPORT);
            new BukkitRunnable() {
                @Override
                public void run() {
                    pet.spawn(p, p.getLocation());
                }
            }.runTaskLater(MCPets.getInstance(), 5L);
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
            if (p.isInsideVehicle() && Pet.fromOwner(p.getUniqueId()) != null) {
                Pet pet = Pet.fromOwner(p.getUniqueId());
                pet.dismount(p);
            }
            return;
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
                        Language.REVOKED.sendMessage(owner);
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
        if(e.getPet() == null
                || e.getPet().getActiveMob() == null
                || e.getPet().getActiveMob().getLocation() == null
                || e.getPet().getActiveMob().getLocation().getWorld() == null)
            return;

        if(GlobalConfig.getInstance().hasBlackListedWorld(e.getPet().getActiveMob().getLocation().getWorld().getName()))
        {
            e.setCancelled(true);
            Player p = Bukkit.getPlayer(e.getPet().getOwner());
            if(p != null)
            {
                Language.BLACKLISTED_WORLD.sendMessage(p);
            }
        }
    }

}
