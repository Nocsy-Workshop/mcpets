package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    @Getter
    private static final ConcurrentHashMap<UUID, PlayerData> registeredData = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private ConcurrentHashMap<String, String> mapOfRegisteredNames = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private ConcurrentHashMap<String, String> mapOfRegisteredInventories = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private ConcurrentHashMap<String, String> mapOfRegisteredPetStats = new ConcurrentHashMap<>();

    @Setter
    @Getter
    private UUID uuid;

    private PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    private PlayerData() {
    }

    public static boolean isRegistered(UUID player)
    {
        return registeredData.containsKey(player);
    }

    public static PlayerData get(UUID owner) {

        if (registeredData.containsKey(owner)) {
            return registeredData.get(owner);
        } else {
            if (!GlobalConfig.getInstance().isDatabaseSupport()) {
                PlayerData data = new PlayerData();
                data.setUuid(owner);
                PlayerDataNoDatabase pdn = PlayerDataNoDatabase.get(owner);
                data.setMapOfRegisteredNames(pdn.mapOfRegisteredNames);
                data.setMapOfRegisteredInventories(pdn.mapOfRegisteredInventories);
                registeredData.put(owner, data);

                return data;
            }

            PlayerData data = new PlayerData(owner);
            registeredData.put(owner, data);
            return data;
        }
    }

    public static PlayerData getEmpty(UUID owner) {
        PlayerData data = new PlayerData();
        data.setUuid(owner);
        return data;
    }

    public static void initializeAllPlayerData() {
        reloadAllPlayerData();
    }

    public static void saveDB() {
        if(GlobalConfig.getInstance().isDatabaseSupport())
            Databases.saveData();
        else
        {
            PlayerDataNoDatabase.getCacheMap().values().forEach(PlayerDataNoDatabase::save);
        }
    }

    public static void reloadAllPlayerData() {
        Databases.loadData();
    }

    /**
     * Register the Pet inventory for future save
     * @param petInventory
     */
    public void setPetInventory(PetInventory petInventory)
    {
        mapOfRegisteredInventories.put(petInventory.getPetId(), petInventory.serialize());
    }

    public void save() {
        if (GlobalConfig.getInstance().isDatabaseSupport())
            saveDB();
        else {
            PlayerDataNoDatabase pdn = PlayerDataNoDatabase.get(uuid);
            pdn.setMapOfRegisteredNames(mapOfRegisteredNames);

            mapOfRegisteredInventories.clear();
            HashMap<String, PetInventory> inventories = PetInventory.getPetInventories().get(this.getUuid());
            if(inventories != null)
            {
                for(String petId : inventories.keySet())
                {
                    mapOfRegisteredInventories.put(petId, inventories.get(petId).serialize());
                }
            }

            pdn.setMapOfRegisteredInventories(mapOfRegisteredInventories);
            pdn.save();
        }
    }

    public static void initAll() {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            Databases.loadData();
        } else {
            PlayerDataNoDatabase.getCacheMap().values().forEach(PlayerDataNoDatabase::reload);
        }
    }

    public static void reloadAll(UUID uuid) {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            Databases.loadData(uuid);
        } else {
            PlayerDataNoDatabase.get(uuid).reload();
        }
    }

    public void reload() {
        if (!Databases.loadData()) {
            PlayerDataNoDatabase.get(uuid).reload();
        }
    }
}
