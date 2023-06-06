package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    @Getter
    private static final HashMap<UUID, PlayerData> registeredData = new HashMap<>();

    @Getter
    @Setter
    private HashMap<String, String> mapOfRegisteredNames = new HashMap<>();
    @Getter
    @Setter
    private HashMap<String, String> mapOfRegisteredInventories = new HashMap<>();
    @Getter
    @Setter
    private HashMap<String, String> mapOfRegisteredPetStats = new HashMap<>();

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
        }
        else {
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

    public static void saveDB(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () -> {
            if (GlobalConfig.getInstance().isDatabaseSupport())
                Databases.save(uuid);
            else
                PlayerDataNoDatabase.getCacheMap().values().forEach(PlayerDataNoDatabase::save);
        });


    }

    public static void reloadAll() {
        Databases.loadAll();
    }

    public void init() {
        reload();
    }

    /**
     * Register the Pet inventory for future save
     *
     * @param petInventory
     */
    public void setPetInventory(PetInventory petInventory) {
        mapOfRegisteredInventories.put(petInventory.getPetId(), petInventory.serialize());
    }

    public void save() {
        if (GlobalConfig.getInstance().isDatabaseSupport())
            saveDB(uuid);
        else {
            PlayerDataNoDatabase pdn = PlayerDataNoDatabase.get(uuid);
            pdn.setMapOfRegisteredNames(mapOfRegisteredNames);

            mapOfRegisteredInventories.clear();
            HashMap<String, PetInventory> inventories = PetInventory.getPetInventories().get(this.getUuid());
            if (inventories != null) {
                for (String petId : inventories.keySet()) {
                    mapOfRegisteredInventories.put(petId, inventories.get(petId).serialize());
                }
            }

            pdn.setMapOfRegisteredInventories(mapOfRegisteredInventories);
            pdn.save();
        }
    }

    public void reload() {
        if (!Databases.load(uuid)) {
            PlayerDataNoDatabase.get(uuid).reload();
        }
    }
}
