package fr.nocsy.mcpets.events;

import fr.nocsy.mcpets.data.Pet;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PetOwnerInteractEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private boolean isCancelled = false;
	@Getter
	private final Pet pet;

	public PetOwnerInteractEvent(Pet pet){
		this.pet = pet;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
