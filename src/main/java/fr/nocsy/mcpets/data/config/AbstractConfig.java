package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.utils.Utils;
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
    private FileConfiguration config;

    public void init(String folderName, String fileName) {
        this.fileName = fileName;
        this.folderName = folderName;

        File folder = new File(path + folderName);
        if (!folder.exists())
            folder.mkdirs();

        File file = new File(path + folderName + "/" + fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
                Utils.debug("Creating file " + fileName + " in " + folderName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadConfig();
    }

    public void save() {
        File file = new File(path + folderName + "/" + fileName);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete()
    {
        File file = new File(path + folderName + "/" + fileName);
        return file.delete();
    }

    public void loadConfig() {
        File file = new File(path + folderName + "/" + fileName);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public abstract void reload();

}
