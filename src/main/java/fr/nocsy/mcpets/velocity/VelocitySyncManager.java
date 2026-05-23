package fr.nocsy.mcpets.velocity;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles incoming plugin messages from the MCPets Velocity companion plugin.
 *
 * Protocol (channel "mcpets:sync"):
 *   Proxy → Bukkit:  PLAYER_SWITCHING <uuid>  – player is on their way here from a synced server
 *
 * Active-pet restoration is driven entirely by the mcpets_active_pet MySQL table;
 * no outgoing messages are needed from the Bukkit side.
 */
public class VelocitySyncManager implements PluginMessageListener {

    public static final String CHANNEL             = "mcpets:sync";
    public static final String MSG_PLAYER_SWITCHING = "PLAYER_SWITCHING";

    private static VelocitySyncManager instance;

    /**
     * UUID → timestamp (ms) for players arriving via a cross-server switch.
     * Used as a fast-path hint: if present we skip the staleness-window check
     * and always restore the pet from DB.
     */
    private static final ConcurrentHashMap<UUID, Long> pendingSwitchPlayers = new ConcurrentHashMap<>();

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    public static void init() {
        if (instance != null) {
            shutdown();
        }
        instance = new VelocitySyncManager();
        Bukkit.getMessenger().registerIncomingPluginChannel(MCPets.getInstance(), CHANNEL, instance);
        Bukkit.getMessenger().registerOutgoingPluginChannel(MCPets.getInstance(), CHANNEL);
    }

    public static void shutdown() {
        if (instance == null) return;
        Bukkit.getMessenger().unregisterIncomingPluginChannel(MCPets.getInstance(), CHANNEL, instance);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(MCPets.getInstance(), CHANNEL);
        instance = null;
    }

    // ------------------------------------------------------------------
    // Incoming messages (from Velocity proxy)
    // ------------------------------------------------------------------

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!CHANNEL.equals(channel)) return;

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = in.readUTF();
            UUID uuid = UUID.fromString(in.readUTF());

            if (MSG_PLAYER_SWITCHING.equals(subChannel)) {
                pendingSwitchPlayers.put(uuid, System.currentTimeMillis());
            }
        } catch (IOException | IllegalArgumentException e) {
            MCPets.getLog().warning("[MCPets/Velocity] Bad plugin message: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Switch-player tracking
    // ------------------------------------------------------------------

    /**
     * @return true if this player arrived via a cross-server switch within the configured SwitchWindow
     */
    public static boolean isPlayerSwitching(UUID uuid) {
        Long ts = pendingSwitchPlayers.get(uuid);
        if (ts == null) return false;
        long windowMs = GlobalConfig.getInstance().getVelocitySwitchWindow() * 1000L;
        if (System.currentTimeMillis() - ts > windowMs) {
            pendingSwitchPlayers.remove(uuid);
            return false;
        }
        return true;
    }

    /** Clear the switch-player marker once the pet has been handled. */
    public static void clearSwitchingPlayer(UUID uuid) {
        pendingSwitchPlayers.remove(uuid);
    }
}
