package fr.nocsy.mcpets.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class SyncListener {

    private static final String MSG_PLAYER_SWITCHING = "PLAYER_SWITCHING";

    private final ProxyServer proxy;
    private final VelocityPluginConfig config;
    private final Logger logger;
    private final MinecraftChannelIdentifier channel;

    public SyncListener(ProxyServer proxy, VelocityPluginConfig config, Logger logger,
                        MinecraftChannelIdentifier channel) {
        this.proxy   = proxy;
        this.config  = config;
        this.logger  = logger;
        this.channel = channel;
    }

    // ------------------------------------------------------------------
    // Player switches servers
    // ------------------------------------------------------------------

    /**
     * Fired BEFORE the player connects to the new server.
     * We send PLAYER_SWITCHING here so the message reaches the destination backend
     * before PlayerJoinEvent fires there — critical for correct timing.
     *
     * PostOrder.LAST ensures we read the final (possibly modified) target server.
     */
    @Subscribe(order = PostOrder.LAST)
    public void onServerPreConnect(ServerPreConnectEvent event) {
        if (!event.getResult().isAllowed()) return;

        Optional<ServerConnection> currentOpt = event.getPlayer().getCurrentServer();
        if (currentOpt.isEmpty()) return; // initial login, not a switch

        String fromServer = currentOpt.get().getServerInfo().getName();
        String toServer   = event.getResult().getServer()
                .map(s -> s.getServerInfo().getName())
                .orElse(null);
        if (toServer == null) return;

        if (config.isSynced(fromServer) && config.isSynced(toServer)) {
            UUID uuid = event.getPlayer().getUniqueId();
            // Send now — the message arrives at the destination BEFORE the player does,
            // ensuring isPlayerSwitching() is true when PlayerJoinEvent fires there.
            sendToServer(toServer, MSG_PLAYER_SWITCHING, uuid);
        }
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private void sendToServer(String serverName, String subChannel, UUID uuid) {
        Optional<RegisteredServer> serverOpt = proxy.getServer(serverName);
        if (serverOpt.isEmpty()) {
            logger.warning("[MCPets-Velocity] Cannot find server '" + serverName + "' to send " + subChannel);
            return;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeUTF(subChannel);
            out.writeUTF(uuid.toString());
            serverOpt.get().sendPluginMessage(channel, baos.toByteArray());
        } catch (IOException e) {
            logger.warning("[MCPets-Velocity] Failed to send " + subChannel + " to " + serverName + ": " + e.getMessage());
        }
    }
}
