package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;

import java.util.HashMap;

public class LanguageConfig extends AbstractConfig {

    public static LanguageConfig instance;

    @Getter
    private HashMap<String, String> map = new HashMap<>();

    public static LanguageConfig getInstance()
    {

        if(instance == null)
            instance = new LanguageConfig();

        return instance;
    }

    public void init()
    {
        super.init("", "language.yml");

        for(Language lang : Language.values())
        {
            if(getConfig().get(lang.name().toLowerCase()) == null)
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

        for(Language lang : Language.values())
        {
            if(getConfig().get(lang.name().toLowerCase()) != null)
                map.put(lang.name().toLowerCase(), getConfig().getString(lang.name().toLowerCase()));

            lang.reload();
        }

        MCPets.getLog().info(MCPets.getLogName() + "Language file reloaded.");
    }
}
