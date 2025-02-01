package fr.nocsy.mcpets.listeners;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

import java.util.UUID;

public class VanillaDismountListener implements Listener {
	
	@EventHandler
	public void onDismount(EntityDismountEvent e) {
		var entity = e.getEntity();
		if (!(entity instanceof Player))
			return;

		UUID petUUID = e.getDismounted().getUniqueId();

		ModeledEntity localModeledEntity = ModelEngineAPI.getModeledEntity(petUUID);
		if (localModeledEntity == null || localModeledEntity.getMountData() == null) {
			return;
		}

		var mountManager = localModeledEntity.getMountData().getMainMountManager();
		if (mountManager == null)
			return;
		var driver = mountManager.getDriver();
		if (driver == null) {
			mountManager.dismountDriver();
			return;
		}

		if (driver.getUniqueId().equals(entity.getUniqueId()))
			mountManager.dismountAll();
		else
			mountManager.dismountRider(entity);
	}
}
