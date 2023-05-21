package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.data.config.AbstractConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataNoDatabase extends AbstractConfig {

    @Getter
    private static final ConcurrentHashMap<UUID, PlayerDataNoDatabase> cacheMap = new ConcurrentHashMap<>();
    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    public ConcurrentHashMap<String, String> mapOfRegisteredNames = new ConcurrentHashMap<>();
    @Getter
    @Setter
    public ConcurrentHashMap<String, String> mapOfRegisteredInventories = new ConcurrentHashMap<>();

    private PlayerDataNoDatabase(UUID uuid) {
        this.uuid = uuid;
        init();
        save();
    }

    public static PlayerDataNoDatabase get(UUID owner) {
        if (cacheMap.containsKey(owner))
            return cacheMap.get(owner);
        else {
            PlayerDataNoDatabase pdn = new PlayerDataNoDatabase(owner);
            cacheMap.put(owner, pdn);
            return pdn;
        }
    }

    public void init() {
        super.init("PlayerData", uuid.toString() + ".yml");

        if (getConfig().get("Names") == null)
            getConfig().set("Names", new ArrayList<String>());
        if (getConfig().get("Inventories") == null)
            getConfig().set("Inventories", new ArrayList<String>());
        if (getConfig().get("PetStats") == null)
            getConfig().set("PetStats", new ArrayList<String>());

        reload();
    }

    @Override
    public void save() {

        ArrayList<String> serializedNamesMap = new ArrayList<>();

        for (String id : mapOfRegisteredNames.keySet()) {
            String name = mapOfRegisteredNames.get(id);
            String seria = id + ";" + name;
            serializedNamesMap.add(seria);
        }

        getConfig().set("Names", serializedNamesMap);

        ArrayList<String> serializedInventoriesMap = new ArrayList<>();
        mapOfRegisteredInventories.clear();
        if(PetInventory.getPetInventories().containsKey(uuid))
        {
            HashMap<String, PetInventory> inventories = PetInventory.getPetInventories().get(uuid);
            for(String petId : inventories.keySet())
            {
                mapOfRegisteredInventories.put(petId, inventories.get(petId).serialize());
                String seriaInv = mapOfRegisteredInventories.get(petId);
                String seria = petId + ";" + seriaInv;
                serializedInventoriesMap.add(seria);
            }
        }

        getConfig().set("Inventories", serializedInventoriesMap);


        ArrayList<String> serializedStatsMap = new ArrayList<>();

        for(PetStats stats : PetStats.getPetStats(uuid))
        {
            serializedStatsMap.add(stats.serialize());
        }

        getConfig().set("PetStats", serializedStatsMap);

        super.save();
    }

    @Override
    public void reload() {

        // Unserialize the pet stats first, coz it influences the inventories
        PetStats.remove(uuid);
        for(String seria : getConfig().getStringList("PetStats"))
        {
            PetStats stats = PetStats.unzerialize(seria);
            if(stats == null)
                continue;
            stats.launchTimers();
            PetStats.register(stats);
        }

        // Unserialize the names
        mapOfRegisteredNames.clear();
        for (String seria : getConfig().getStringList("Names")) {
            String[] table = seria.split(";");
            String id = table[0];
            String name = table[1];

            mapOfRegisteredNames.put(id, name);
        }

        // Unserialize the inventories
        mapOfRegisteredInventories.clear();
        for (String seria : getConfig().getStringList("Inventories")) {
            String[] table = seria.split(";");
            String id = table[0];
            String seriaInventory = table[1];

            mapOfRegisteredInventories.put(id, seriaInventory);
            PetInventory.unserialize(seria, uuid);
        }

    }
}

