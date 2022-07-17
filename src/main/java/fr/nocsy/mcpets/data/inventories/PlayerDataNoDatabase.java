package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.config.AbstractConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataNoDatabase extends AbstractConfig {

    @Getter
    private static final HashMap<UUID, PlayerDataNoDatabase> cacheMap = new HashMap<>();
    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    public HashMap<String, String> mapOfRegisteredNames = new HashMap<>();
    @Getter
    @Setter
    public HashMap<String, String> mapOfRegisteredInventories = new HashMap<>();

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

        super.save();
    }

    @Override
    public void reload() {

        mapOfRegisteredNames.clear();
        for (String seria : getConfig().getStringList("Names")) {
            String[] table = seria.split(";");
            String id = table[0];
            String name = table[1];

            mapOfRegisteredNames.put(id, name);
        }

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

