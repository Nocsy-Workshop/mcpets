package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Databases {

    @Getter
    @Setter
    public static MySQLDB mySQL;

    private static String table = GlobalConfig.getInstance().getMySQL_Prefix() + "mcpets_player_data";
    private static ConcurrentHashMap<UUID, Object> playerLocks = new ConcurrentHashMap<>();

    public static boolean init() {
        if(GlobalConfig.getInstance().isDisableMySQL())
        {
            MCPets.getInstance().getLogger().info("MySQL is disabled. Flat support will be used.");
            return false;
        }
        Databases.setMySQL(new MySQLDB(GlobalConfig.getInstance().getMySQL_USER(),
                GlobalConfig.getInstance().getMySQL_PASSWORD(),
                GlobalConfig.getInstance().getMySQL_HOST(),
                GlobalConfig.getInstance().getMySQL_PORT(),
                GlobalConfig.getInstance().getMySQL_DB()));
        if (!Databases.getMySQL().init()) {
            MCPets.getInstance().getLogger().info("[Database] Can't initialize MySQL.");
            MCPets.getInstance().getLogger().info("[Database] Will be using YAML support instead (no worry it's not a bug).");
            GlobalConfig.getInstance().setDatabaseSupport(false);
            return false;
        }
        GlobalConfig.getInstance().setDatabaseSupport(true);
        createSQLTables();
        return true;
    }

    public static void createSQLTables() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        getMySQL().query("CREATE TABLE IF NOT EXISTS " + table + " (id INT NOT NULL AUTO_INCREMENT, uuid TEXT, names TEXT, inventories TEXT, data TEXT, primary key (id));");
    }

    public static boolean loadData() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return false;

        ResultSet playerData = getMySQL().query("SELECT * FROM " + table + ";");

        if (playerData == null)
            return true;

        try {
            while (playerData.next()) {
                String uuidStr = playerData.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);

                synchronized (getLockForPlayer(uuid)) {
                    PlayerData pd = PlayerData.getEmpty(uuid);

                    // Unserialize the pet stats first, coz it influences the inventories
                    PetStats.remove(uuid);

                    for (String seria : playerData.getString("data").split(";;;")) {
                        PetStats stats = PetStats.unzerialize(seria);
                        if (stats == null)
                            continue;
                        stats.launchTimers();
                        PetStats.register(stats);
                    }

                    // Unserialize the pet names
                    pd.setMapOfRegisteredNames(unserializeData(playerData, "names"));

                    // Unserialize the pet inventories
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }

        return true;
    }

    private static Object getLockForPlayer(UUID playerUUID) {
        return playerLocks.computeIfAbsent(playerUUID, k -> new Object());
    }

    public static boolean loadData(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return false;

        // Update the SQL query to fetch data for the specific player with the provided UUID
        ResultSet playerData = getMySQL().query("SELECT * FROM " + table + " WHERE uuid='" + playerUUID.toString() + "';");

        if (playerData == null)
            return true;

        try {
            while (playerData.next()) {
                String uuidStr = playerData.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);

                synchronized (getLockForPlayer(uuid)) {
                    PlayerData pd = PlayerData.getEmpty(uuid);

                    // Unserialize the pet stats first, coz it influences the inventories
                    PetStats.remove(uuid);

                    for (String seria : playerData.getString("data").split(";;;")) {
                        PetStats stats = PetStats.unzerialize(seria);
                        if (stats == null)
                            continue;
                        stats.launchTimers();
                        PetStats.register(stats);
                    }
                    // Unserialize the pet names
                    pd.setMapOfRegisteredNames(unserializeData(playerData, "names"));

                    // Unserialize the pet inventories
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    public static void saveData() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        getMySQL().query("TRUNCATE " + table);

        for (UUID uuid : PlayerData.getRegisteredData().keySet()) {
            synchronized (getLockForPlayer(uuid)) {
                PlayerData pd = PlayerData.getRegisteredData().get(uuid);

                String names = buildStringSerialized(pd.getMapOfRegisteredNames());
                String inventories = buildStringSerialized(pd.getMapOfRegisteredInventories());

                StringBuilder data = new StringBuilder();

                for (PetStats stats : PetStats.getPetStats(uuid)) {
                    data.append(stats.serialize()).append(";;;");
                }
                if (data.length() > 0)
                    data = new StringBuilder(data.substring(0, data.length() - 3));

                getMySQL().query("INSERT INTO " + table + " (uuid, names, inventories, data) VALUES ('" + uuid.toString()
                        + "', '" + names
                        + "', '" + inventories
                        + "', '" + data + "')");
            }
        }
    }

    public static void savePlayerData(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        if(!PlayerData.isRegistered(playerUUID))
            return;

        synchronized (getLockForPlayer(playerUUID)) {
            PlayerData pd = PlayerData.getRegisteredData().get(playerUUID);

            String names = buildStringSerialized(pd.getMapOfRegisteredNames());
            String inventories = buildStringSerialized(pd.getMapOfRegisteredInventories());

            StringBuilder data = new StringBuilder();

            for (PetStats stats : PetStats.getPetStats(playerUUID)) {
                data.append(stats.serialize()).append(";;;");
            }
            if (data.length() > 0)
                data = new StringBuilder(data.substring(0, data.length() - 3));

            // First, delete the existing data for the player
            getMySQL().query("DELETE FROM " + table + " WHERE uuid='" + playerUUID.toString() + "'");

            // Then, insert the new data for the player
            getMySQL().query("INSERT INTO " + table + " (uuid, names, inventories, data) VALUES ('" + playerUUID.toString()
                    + "', '" + names
                    + "', '" + inventories
                    + "', '" + data + "')");
        }
    }

    private static String buildStringSerialized(Map<String,String> map)
    {
        String builder = "";
        for(String id : map.keySet())
        {
            String seria = map.get(id);
            String seriaId = id + ";;" + seria;
            if(builder.isBlank())
                builder = seriaId;
            else
                builder = builder + ";;;" + seriaId;
        }
        return builder;
    }

    public static ConcurrentHashMap<String, String> unserializeData(ResultSet resultSet, String targetedColumn) throws SQLException {
        String targetedResults = resultSet.getString(targetedColumn);
        ConcurrentHashMap<String, String> outputMap = new ConcurrentHashMap<>();

        String[] seriaTable = targetedResults.split(";;;");

        for (String seriaContents : seriaTable) {
            // treats the case in which input is empty or wrongly formatted
            if (seriaContents == null || !seriaContents.contains(";;"))
                continue;

            String[] seriaData = seriaContents.split(";;");
            try {
                String pet_id = seriaData[0];
                String content = seriaData[1];
                outputMap.put(pet_id, content);
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                MCPets.getInstance().getLogger().severe("[Database] Index out of bound for (147) : " + seriaContents);
            }
        }

        return outputMap;
    }
}
