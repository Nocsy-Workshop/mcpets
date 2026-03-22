package fr.nocsy.mcpets.data.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

    public void init(String folderName, String fileName) {
        this.fileName = fileName;
        this.folderName = folderName;

        File folder = new File(path + folderName);
        if (!folder.exists())
            folder.mkdirs();

        File file = new File(getFullPath());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Failed to create config file: " + getFullPath(), e);
            }
        }

        loadConfig();
    }

    public String getFullPath() {
        return (path + folderName + "/" + fileName).replace("//", "/");
    }

    public void save() {
        File file = new File(getFullPath());
        try {
            config.save(file);
        } catch (IOException e) {
            org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Failed to save config file: " + getFullPath(), e);
        }
    }

    public boolean delete() {
        File file = new File(getFullPath());
        return file.delete();
    }

    public void loadConfig() {
        File file = new File(getFullPath());
        config = YamlConfiguration.loadConfiguration(file);
    }

    public abstract void reload();
}
