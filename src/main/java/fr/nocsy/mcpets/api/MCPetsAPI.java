package fr.nocsy.mcpets.api;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.SpawnResult;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public static Pet getObjectPet(String id) {
        return Pet.getFromId(id);
    }

    /**
     * Returns the pet associated to the player if it has one.
     * Returns null if no pet is attached to the player.
     */
    public static Pet getActivePet(UUID playerUUID) {
        return Pet.getActivePets().get(playerUUID);
    }

    /**
     * Returns the map where Key=UuidPlayer | Value=Pet
     */
    public static Map<UUID, Pet> getActivePets() {
        return Pet.getActivePets();
    }

    /**
     * Returns a list of object pet instance | not active pets
     */
    public static List<Pet> getObjectPets() {
        return Pet.getObjectPets();
    }

    /**
     * Get the list of pet that are available to the specified player (permission based)
     */
    public static List<Pet> getAvailablePets(Player p) {
        return Pet.getAvailablePets(p);
    }

    /**
     * Set the active pet of the player
     * Returns a value giving what happened after calling the method
     */
    public static CompletableFuture<SpawnResult> setActivePet(Pet pet, Player p, boolean checkPermission) {
        pet.setCheckPermission(checkPermission);
        return pet.spawn(p.getLocation(), true);
    }
}
