package fr.nocsy.mcpets.modeler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.utils.debug.Debugger;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.flags.DismountPetFlag;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import com.ticxo.modelengine.api.events.ModelDismountEvent;

public class ModelEngineListeners implements Listener {

    @EventHandler
    public void despawnOnDismount(ModelDismountEvent e) {
        ActiveModel vehicle = e.getVehicle();
        if (vehicle == null || vehicle.getModeledEntity() == null || vehicle.getModeledEntity().getBase() == null) {
            return;
        }

        if (!(vehicle.getModeledEntity().getBase() instanceof BukkitEntity bukkitEntity)) return;

        Entity entity = bukkitEntity.getOriginal();

        Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
            Pet pet = Pet.getFromEntity(entity);
            if (pet != null && pet.isDespawnOnDismount()) {
                pet.despawn(PetDespawnReason.DISMOUNT);
            }
        });
    }

    @EventHandler
    public void mountingPet(ModelMountEvent e) {
        if (!Bukkit.isPrimaryThread()) return;

        if (e.getPassenger() == null) return;

        ActiveModel vehicle = e.getVehicle();
        if (vehicle == null || vehicle.getModeledEntity() == null || vehicle.getModeledEntity().getBase() == null) {
            return;
        }

        if (!(vehicle.getModeledEntity().getBase() instanceof BukkitEntity bukkitEntity)) return;

        Entity entity = bukkitEntity.getOriginal();

        Pet pet = Pet.getFromEntity(entity);
        Entity player = e.getPassenger();

        if (pet == null) return;

        Mount seat = e.getSeat();

        // if it's not the owner or an admin mounting the pet, then we cancel it
        if (seat.isDriver() &&
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
            if (pet.isDespawnOnDismount()) pet.despawn(PetDespawnReason.FLAG);
            return;
        }

        // If user doesn't have the perm to mount the pet, cancel the event
        if (pet.getMountPermission() != null
                && !player.hasPermission(pet.getMountPermission())
                && seat.isDriver()) {
            e.setCancelled(true);
            Language.CANT_MOUNT_PET_YET.sendMessage(player);
        }

    }

}
