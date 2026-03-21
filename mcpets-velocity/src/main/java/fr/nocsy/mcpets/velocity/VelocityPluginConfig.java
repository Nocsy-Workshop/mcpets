package fr.nocsy.mcpets.velocity;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class VelocityPluginConfig {

    private final Path dataDirectory;
    private final Logger logger;

    private List<String> syncedServers = Collections.emptyList();

    public VelocityPluginConfig(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    public void load() {
        Path configFile = dataDirectory.resolve("config.yml");

        // Copy default config if absent
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(dataDirectory);
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    if (in != null) Files.copy(in, configFile);
                }
            } catch (IOException e) {
                logger.warning("[MCPets-Velocity] Could not save default config: " + e.getMessage());
            }
        }

        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(configFile)
                    .build();
            ConfigurationNode root = loader.load();
            syncedServers = root.node("synced-servers").getList(String.class, Collections.emptyList());
            logger.info("[MCPets-Velocity] Synced servers: " + syncedServers);
        } catch (IOException e) {
            logger.warning("[MCPets-Velocity] Could not load config: " + e.getMessage());
        }
    }

    /** @return true if the given server name is in the synced list (or if the list is empty = all servers). */
    public boolean isSynced(String serverName) {
        return syncedServers.isEmpty() || syncedServers.contains(serverName);
    }
}
