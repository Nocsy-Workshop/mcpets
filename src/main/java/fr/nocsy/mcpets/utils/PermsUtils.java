package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PermsUtils {

    /**
     * Give permission to a player (based on LuckPerms)
     * Return false if we are unable to give the permission on a long term basis
     */
    protected static boolean givePermission(UUID uuid, String permission) {
        if (MCPets.getLuckPerms() != null) {
            MCPets.getLuckPerms().getUserManager().modifyUser(uuid, user -> user.data().add(Node.builder(permission).build()));
            return true;
        }

        if (Bukkit.getPlayer(uuid) != null) {
            // This is not saved in any file, just in the MCPets instance so it's not a viable solution
            // Hence we return false
            Bukkit.getPlayer(uuid).addAttachment(MCPets.getInstance(), permission, true);
            return false;
        }
        return false;
    }

    /**
     * Give permission async, returning a future that completes when LuckPerms has applied the change.
     */
    protected static CompletableFuture<Void> givePermissionAsync(UUID uuid, String permission) {
        if (MCPets.getLuckPerms() != null) {
            return MCPets.getLuckPerms().getUserManager().modifyUser(uuid, user -> user.data().add(Node.builder(permission).build()));
        }

        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).addAttachment(MCPets.getInstance(), permission, true);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Remove permission async, returning a future that completes when LuckPerms has applied the change.
     */
    protected static CompletableFuture<Void> removePermissionAsync(UUID uuid, String permission) {
        if (MCPets.getLuckPerms() != null) {
            return MCPets.getLuckPerms().getUserManager().modifyUser(uuid, user -> user.data().remove(Node.builder(permission).build()));
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Remove permission to the player
     */
    protected static boolean removePermission(UUID uuid, String permission) {
        if (MCPets.getLuckPerms() != null) {
            MCPets.getLuckPerms().getUserManager().modifyUser(uuid, user -> user.data().remove(Node.builder(permission).build()));
            return true;
        }

        return false;
    }

    /**
     * Check if the player has the permission
     */
    protected static boolean hasPermission(@NotNull UUID uuid, String permission) {
        if (MCPets.getLuckPerms() != null) {
            User user = MCPets.getLuckPerms().getUserManager().getUser(uuid);
            if (user != null) {
                return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
            }
        }

        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            return p.hasPermission(permission);
        }

        return false;
    }
}
