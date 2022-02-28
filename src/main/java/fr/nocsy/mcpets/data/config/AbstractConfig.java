package fr.nocsy.mcpets.data.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
    private File folder;

    @Getter
    private File file;

    @Getter
    private FileConfiguration config;

    public void init(String folderName, String fileName) {
        this.fileName = fileName;
        this.folderName = folderName;

        folder = new File(path + folderName);
        if (!folder.exists())
            folder.mkdirs();

        file = new File(path + folderName + "/" + fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadConfig();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public abstract void reload();

}
