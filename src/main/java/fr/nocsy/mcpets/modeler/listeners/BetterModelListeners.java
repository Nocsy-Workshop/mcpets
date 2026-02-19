package fr.nocsy.mcpets.modeler.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.flags.DismountPetFlag;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.utils.debug.Debugger;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.BetterModelBukkit;
import kr.toxicity.model.api.bukkit.platform.BukkitEntity;
import kr.toxicity.model.api.event.DismountModelEvent;
import kr.toxicity.model.api.event.ModelEventListener;
import kr.toxicity.model.api.event.MountModelEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class BetterModelListeners implements ModelListener {

    private ModelEventListener mountListener;
    private ModelEventListener dismountListener;

    @Override
    public void subscribe() {
        BetterModelBukkit platform = BetterModelBukkit.platform();

        this.mountListener = BetterModel.eventBus().subscribe(platform, MountModelEvent.class, event -> {
            Entity mount = ((BukkitEntity) event.tracker().sourceEntity()).source();
            Entity player = ((BukkitEntity) event.entity()).source();


            boolean isDriverSeat = event.hitbox().mountController().canControl();

            Pet pet = Pet.getFromEntity(mount);
            if (pet == null) return;

            if (isDriverSeat &&
                    !pet.getOwner().equals(player.getUniqueId()) &&
                    !player.hasPermission(PPermission.ADMIN.getPermission())) {
                event.setCancelled(true);
            }

            if (GlobalConfig.getInstance().isWorldguardsupport() &&
                    FlagsManager.getFlag(DismountPetFlag.NAME) != null &&
                    FlagsManager.getFlag(DismountPetFlag.NAME).testState(player.getLocation())) {
                event.setCancelled(true);
                Debugger.send("§c" + player.getName() + " can not mount model of " + pet.getId() + " as a region is preventing mounting.");
                Language.NOT_MOUNTABLE_HERE.sendMessage(player);
                if (pet.isDespawnOnDismount())
                    pet.despawn(PetDespawnReason.FLAG);
                return;
            }

            // If user doesn't have the perm to mount the pet, cancel the event
            if (pet.getMountPermission() != null
                    && !player.hasPermission(pet.getMountPermission())
                    && isDriverSeat) {
                event.setCancelled(true);
                Language.CANT_MOUNT_PET_YET.sendMessage(player);
            }
        });


        this.dismountListener = BetterModel.eventBus().subscribe(platform, DismountModelEvent.class, event -> {
            Entity baseEntity = ((BukkitEntity) event.tracker().sourceEntity()).source();

            Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
                Pet pet = Pet.getFromEntity(baseEntity);
                if (pet != null && pet.isDespawnOnDismount()) {
                    pet.despawn(PetDespawnReason.DISMOUNT);
                }
            });
        });
    }

    @Override
    public void unsubscribe() {
        if (this.mountListener != null) {
            this.mountListener.unregister();
        }

        if (this.dismountListener != null) {
            this.dismountListener.unregister();
        }
    }
}
