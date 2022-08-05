package fr.nocsy.mcpets.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;

public class VanillaDismountListener implements Listener {
	
	@EventHandler
	public void onDismount(EntityDismountEvent e) {
		var entity = e.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}

		UUID petUUID = e.getDismounted().getUniqueId();

		ModeledEntity localModeledEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(petUUID);
		if (localModeledEntity == null) {
			return;
		}

		var mountManager = localModeledEntity.getMountHandler();
		var driver = mountManager.getDriver();
		if (driver == null) {
			mountManager.removePassenger(entity);
			return;
		}

		if (driver.getUniqueId().equals(entity.getUniqueId())) {
			mountManager.dismountAll();
		} else {
			mountManager.removePassenger(entity);
		}
	}
}
