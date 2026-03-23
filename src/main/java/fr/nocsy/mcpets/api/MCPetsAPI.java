package fr.nocsy.mcpets.api;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MCPetsAPI {

    /**
     * Get plugin instance
     */
    public static MCPets getPluginInstance() {
        return MCPets.getInstance();
    }

    /**
     * Returns pet object instance | not an active pet instance
     */
    public static Pet getObjectPet(final String id) {
        return Pet.getFromId(id);
    }

    /**
     * Returns the first active pet for the player if they have one.
     * Returns null if no pet is attached to the player.
     * @deprecated Use getActivePetsForPlayer() for multiple pets support
     */
    @Deprecated
    public static Pet getActivePet(final UUID playerUUID) {
        final List<Pet> pets = Pet.getActivePets().get(playerUUID);
        return (pets != null && !pets.isEmpty()) ? pets.getFirst() : null;
    }

    /**
     * Returns all active pets for the player.
     */
    public static List<Pet> getActivePetsForPlayer(final UUID playerUUID) {
        return Pet.getActivePetsForOwner(playerUUID);
    }

    /**
     * Returns the map where Key=UuidPlayer | Value=List<Pet>
     * @deprecated API signature changed - returns HashMap<UUID, List<Pet>> now
     */
    @Deprecated
    public static HashMap<UUID, List<Pet>> getActivePets() {
        return Pet.getActivePets();
    }

    /**
     * Returns a list of object pet instance | not active pets
     */
    public static ArrayList<Pet> getObjectPets() {
        return Pet.getObjectPets();
    }

    /**
     * Get the list of pet that are available to the specified player (permission based)
     */
    public static List<Pet> getAvailablePets(final Player p) {
        return Pet.getAvailablePets(p);
    }

    /**
     * Set the active pet of the player
     * Returns a value giving what happened after calling the method
     */
    public static int setActivePet(final Pet pet, final Player p, final boolean checkPermission) {
        pet.setCheckPermission(checkPermission);
        return pet.spawn(p.getLocation(), true);
    }
}
