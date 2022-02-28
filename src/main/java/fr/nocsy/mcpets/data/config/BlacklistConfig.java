package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;

import java.util.ArrayList;

public class BlacklistConfig extends AbstractConfig {

    public static BlacklistConfig instance;

    @Getter
    private final ArrayList<String> blackListedWords = new ArrayList<>();

    public static BlacklistConfig getInstance() {

        if (instance == null)
            instance = new BlacklistConfig();

        return instance;
    }

    public void init() {
        super.init("", "blacklist.yml");
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

        blackListedWords.clear();
        blackListedWords.addAll(getConfig().getStringList("Blacklist"));

        MCPets.getLog().info(MCPets.getLogName() + "Blacklist file reloaded.");
    }

}
