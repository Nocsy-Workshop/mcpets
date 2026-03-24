package fr.nocsy.mcpets.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.nio.file.Path;
import java.util.logging.Logger;

// NOTE: The runtime plugin version is provided by velocity-plugin.json (Maven-filtered to
// ${project.version} at build time). The version below must be kept in sync with pom.xml
// manually, as Maven resource filtering cannot substitute values in Java source annotations.
@Plugin(
    id          = "mcpets-velocity",
    name        = "MCPets-Velocity",
    version     = "4.1.8",
    description = "Velocity companion for MCPets – syncs active pets across synced servers.",
    authors     = {"Nocsy"}
)
public class MCPetsVelocity {

    private static final MinecraftChannelIdentifier CHANNEL =
            MinecraftChannelIdentifier.from("mcpets:sync");

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public MCPetsVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy         = proxy;
        this.logger        = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        // Register the plugin messaging channel so the proxy can send/receive on it
        proxy.getChannelRegistrar().register(CHANNEL);

        // Load config
        VelocityPluginConfig config = new VelocityPluginConfig(dataDirectory, logger);
        config.load();

        // Register the event listener
        proxy.getEventManager().register(this, new SyncListener(proxy, config, logger, CHANNEL));

        logger.info("[MCPets-Velocity] Loaded. Syncing pets on cross-server switches.");
    }
}
