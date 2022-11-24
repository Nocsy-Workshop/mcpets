package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;

import java.util.HashMap;

public class LanguageConfig extends AbstractConfig {

    public static LanguageConfig instance;

    @Getter
    private final HashMap<String, String> map = new HashMap<>();

    public static LanguageConfig getInstance() {

        if (instance == null)
            instance = new LanguageConfig();

        return instance;
    }

    public void init() {
        super.init("", "language.yml");

        for (Language lang : Language.values()) {
            if (getConfig().get(lang.name().toLowerCase()) == null)
                getConfig().set(lang.name().toLowerCase(), lang.getMessage());
        }

        save();
        reload();
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void reload() {

        loadConfig();

        map.clear();

        for (Language lang : Language.values()) {
            if (getConfig().get(lang.name().toLowerCase()) != null)
                map.put(lang.name().toLowerCase(), getConfig().getString(lang.name().toLowerCase()));

            lang.reload();
        }

        if(Language.PET_INVENTORY_TITLE.getMessage().equals(Language.INVENTORY_PETS_MENU_INTERACTIONS.getMessage()))
        {
            MCPets.getLog().severe(MCPets.getLogName() + "Interaction menu and prime menu have the same name, which might lead to unexpected behaviors. Please consider having different names for both menus.");
        }

        MCPets.getLog().info(MCPets.getLogName() + "Language file reloaded.");
    }
}
