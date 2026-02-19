package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
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

		MCPets.getModeler().dismount(petUUID, entity);
	}
}
