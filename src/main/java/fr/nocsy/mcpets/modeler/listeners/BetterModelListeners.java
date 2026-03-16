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

public class BetterModelListeners {

    private ModelEventListener mountListener;
    private ModelEventListener dismountListener;

    public void register() {
        mountListener = BetterModel.eventBus().subscribe(
                BetterModelBukkit.platform(),
                MountModelEvent.class,
                this::onMount
        );

        dismountListener = BetterModel.eventBus().subscribe(
                BetterModelBukkit.platform(),
                DismountModelEvent.class,
                this::onDismount
        );
    }

    public void unregister() {
        if (mountListener != null) {
            mountListener.unregister();
            mountListener = null;
        }
        if (dismountListener != null) {
            dismountListener.unregister();
            dismountListener = null;
        }
    }

    private void onMount(MountModelEvent event) {
        if (event.entity() == null || event.tracker() == null)
            return;

        Entity mountEntity;
        Entity playerEntity;
        try {
            mountEntity = ((BukkitEntity) event.tracker().sourceEntity()).source();
            playerEntity = ((BukkitEntity) event.entity()).source();
        } catch (ClassCastException e) {
            MCPets.getLog().warning("[BetterModel] Unexpected entity type in MountModelEvent — permission checks skipped: " + e.getMessage());
            Debugger.send("§c[BetterModel] ClassCastException in onMount: " + e.getMessage());
            return;
        }

        Pet pet = Pet.getFromEntity(mountEntity);
        if (pet == null)
            return;

        boolean isDriver = event.hitbox().mountController() != null
                && event.hitbox().mountController().canControl();

        // Check owner/admin permission
        if (isDriver
                && !pet.getOwner().equals(playerEntity.getUniqueId())
                && !playerEntity.hasPermission(PPermission.ADMIN.getPermission())) {
            event.setCancelled(true);
            Debugger.send("[MountModelEvent] §c" + playerEntity.getName()
                    + " can not mount model of " + pet.getId()
                    + " as he's not the owner, nor an admin.");
            return;
        }

        // Check WorldGuard dismount flag
        if (GlobalConfig.getInstance().isWorldguardsupport()
                && FlagsManager.getFlag(DismountPetFlag.NAME) != null
                && FlagsManager.getFlag(DismountPetFlag.NAME).testState(playerEntity.getLocation())) {
            event.setCancelled(true);
            Debugger.send("§c" + playerEntity.getName()
                    + " can not mount model of " + pet.getId()
                    + " as a region is preventing mounting.");
            Language.NOT_MOUNTABLE_HERE.sendMessage(playerEntity);
            if (pet.isDespawnOnDismount())
                pet.despawn(PetDespawnReason.FLAG);
            return;
        }

        // Check mount permission
        if (pet.getMountPermission() != null
                && !playerEntity.hasPermission(pet.getMountPermission())
                && isDriver) {
            event.setCancelled(true);
            Language.CANT_MOUNT_PET_YET.sendMessage(playerEntity);
        }
    }

    private void onDismount(DismountModelEvent event) {
        if (event.tracker() == null)
            return;

        Entity mountEntity;
        try {
            mountEntity = ((BukkitEntity) event.tracker().sourceEntity()).source();
        } catch (ClassCastException e) {
            Debugger.send("§c[BetterModel] ClassCastException in onDismount: " + e.getMessage());
            return;
        }

        Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
            Pet pet = Pet.getFromEntity(mountEntity);
            if (pet != null && pet.isDespawnOnDismount()) {
                pet.despawn(PetDespawnReason.DISMOUNT);
            }
        });
    }
}
