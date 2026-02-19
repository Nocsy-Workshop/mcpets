package fr.nocsy.mcpets.modeler.listeners;

import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.flags.DismountPetFlag;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ModelEngineListeners implements Listener, ModelListener {


    @EventHandler
    public void mountingPet(ModelMountEvent e) {
        if (e.getPassenger() == null)
            return;

        if (e.getVehicle() == null || e.getVehicle().getModeledEntity() == null || e.getVehicle().getModeledEntity().getBase() == null)
            return;

        Entity entity;
        try {
            entity = Bukkit.getEntity(e.getVehicle().getModeledEntity().getBase().getUUID());
        }
        catch (Exception ex) {
            entity = null;
        }

        if (entity == null)
            return;
        Pet pet = Pet.getFromEntity(entity);
        Entity player = e.getPassenger();

        if (pet == null)
            return;

        // if it's not the owner or an admin mounting the pet, then we cancel it
        if (e.getSeat().isDriver() &&
                !pet.getOwner().equals(player.getUniqueId()) &&
                !player.hasPermission(PPermission.ADMIN.getPermission())) {
            e.setCancelled(true);
            Debugger.send("[ModelMountEvent] §c" + player.getName() + " can not mount model of " + pet.getId() + " as he's not the owner, nor an admin.");
        }

        if (GlobalConfig.getInstance().isWorldguardsupport() &&
                FlagsManager.getFlag(DismountPetFlag.NAME) != null &&
                FlagsManager.getFlag(DismountPetFlag.NAME).testState(player.getLocation())) {
            e.setCancelled(true);
            Debugger.send("§c" + player.getName() + " can not mount model of " + pet.getId() + " as a region is preventing mounting.");
            Language.NOT_MOUNTABLE_HERE.sendMessage(player);
            if (pet.isDespawnOnDismount())
                pet.despawn(PetDespawnReason.FLAG);
            return;
        }

        // If user doesn't have the perm to mount the pet, cancel the event
        if (pet.getMountPermission() != null
                && !player.hasPermission(pet.getMountPermission())
                && e.getSeat().isDriver()) {
            e.setCancelled(true);
            Language.CANT_MOUNT_PET_YET.sendMessage(player);
        }
    }


    @EventHandler
    public void despawnOnDismount(ModelDismountEvent e) {
        if (e.getVehicle() == null || e.getVehicle().getModeledEntity() == null || e.getVehicle().getModeledEntity().getBase() == null)
            return;

        // Running this as sync coz we fetch an entity
        new BukkitRunnable() {
            @Override
            public void run() {
                Pet pet = Pet.getFromEntity(Bukkit.getEntity(e.getVehicle().getModeledEntity().getBase().getUUID()));
                if (pet != null && pet.isDespawnOnDismount()) {
                    pet.despawn(PetDespawnReason.DISMOUNT);
                }
            }
        }.runTask(MCPets.getInstance());
    }

    @Override
    public void subscribe() {
        Bukkit.getPluginManager().registerEvents(this, MCPets.getInstance());
    }

    @Override
    public void unsubscribe() {
        HandlerList.unregisterAll(this);
    }
}
