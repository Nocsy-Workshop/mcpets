package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.sql.Databases;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    @Getter
    private static HashMap<UUID, PlayerData> registeredData = new HashMap<>();
    @Getter
    @Setter
    private HashMap<String, String> mapOfRegisteredNames = new HashMap<>();

    @Setter
    @Getter
    private UUID uuid;

    private PlayerData(UUID uuid)
    {
        this.uuid = uuid;
        init();
        save();
    }
    private PlayerData() {}

    public static PlayerData get(UUID owner)
    {

        if(registeredData.containsKey(owner))
        {
            return registeredData.get(owner);
        }
        else
        {
            if(!GlobalConfig.getInstance().isDatabaseSupport())
            {
                PlayerData data = new PlayerData();
                data.setUuid(owner);
                PlayerDataNoDatabase pdn = PlayerDataNoDatabase.get(owner);
                data.setMapOfRegisteredNames(pdn.mapOfRegisteredNames);
                registeredData.put(owner, data);

                return data;
            }

            PlayerData data = new PlayerData(owner);
            registeredData.put(owner, data);
            return data;
        }
    }

    public static PlayerData getEmpty(UUID owner)
    {
        PlayerData data = new PlayerData();
        data.setUuid(owner);
        return data;
    }

    public void init()
    {
        reload();
    }

    public static void initAll()
    {
        reloadAll();
    }

    public void save() {
        if(GlobalConfig.getInstance().isDatabaseSupport())
            saveDB();
        else
        {
            PlayerDataNoDatabase pdn = PlayerDataNoDatabase.get(uuid);
            pdn.setMapOfRegisteredNames(mapOfRegisteredNames);
            pdn.save();
        }
    }

    public static void saveDB()
    {
        Databases.saveData();
    }

    public void reload()
    {
        if(!Databases.loadData())
        {
            PlayerDataNoDatabase.get(uuid).reload();
        }
    }

    public static void reloadAll() {
        Databases.loadData();
    }
}
