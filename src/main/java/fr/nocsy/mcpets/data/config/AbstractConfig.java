package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractConfig {

    @Getter
    private static final String path = "./plugins/MCPets/";

    @Getter
    @Setter
    private String folderName;

    @Getter
    @Setter
    private String fileName;

    @Getter
    private FileConfiguration config;

    public void init(final String folderName, final String fileName) {
        this.fileName = fileName;
        this.folderName = folderName;

        final File folder = new File(path + folderName);
        if (!folder.exists())
            folder.mkdirs();

        final File file = new File(getFullPath());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                MCPets.getLog().log(Level.SEVERE, "Failed to create config file: " + getFullPath(), e);
            }
        }

        loadConfig();
    }

    public String getFullPath() {
        return (path + folderName + "/" + fileName).replace("//", "/");
    }

    public void save() {
        final File file = new File(getFullPath());
        try {
            config.save(file);
        } catch (final IOException e) {
            MCPets.getLog().log(Level.SEVERE, "Failed to save config file: " + getFullPath(), e);
        }
    }

    public boolean delete() {
        final File file = new File(getFullPath());
        return file.delete();
    }

    public void loadConfig() {
        final File file = new File(getFullPath());
        config = YamlConfiguration.loadConfiguration(file);
    }

    protected void reorderKeys(String... keyOrder) {
        Map<String, Object> ordered = new LinkedHashMap<>();
        for (String key : keyOrder) {
            if (config.contains(key)) {
                ordered.put(key, config.get(key));
            }
        }
        for (String key : new ArrayList<>(config.getKeys(false))) {
            if (!ordered.containsKey(key)) {
                ordered.put(key, config.get(key));
            }
        }
        for (String key : new ArrayList<>(config.getKeys(false))) {
            config.set(key, null);
        }
        for (Map.Entry<String, Object> entry : ordered.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }

    public abstract void reload();
}
