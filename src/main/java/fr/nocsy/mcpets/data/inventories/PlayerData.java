package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.sql.Databases;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerData {

    @Getter
    private static final HashMap<UUID, PlayerData> registeredData = new HashMap<>();

    @Getter
    @Setter
    private HashMap<String, String> mapOfRegisteredNames = new HashMap<>();
    @Getter
    @Setter
    private HashMap<String, String> mapOfRegisteredInventories = new HashMap<>();

    @Setter
    @Getter
    private UUID uuid;

    private PlayerData(UUID uuid) {
        this.uuid = uuid;
        init();
        save();
    }

    private PlayerData() {
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

    public static void initAll() {
        reloadAll();
    }

    public static void saveDB() {
        Databases.saveData();
    }

    public static void reloadAll() {
        Databases.loadData();
    }

    public void init() {
        reload();
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

    public void reload() {
        if (!Databases.loadData()) {
            PlayerDataNoDatabase.get(uuid).reload();
        }
    }
}
