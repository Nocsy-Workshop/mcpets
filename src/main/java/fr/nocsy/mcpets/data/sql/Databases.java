package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.serializer.PetStatsSerializer;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Databases {

    @Getter
    @Setter
    public static MySQLDB mySQL;

    private static String table = GlobalConfig.getInstance().getMySQL_Prefix() + "mcpets_player_data";

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
            MCPets.getInstance().getLogger().severe("[Database] Can't initialize MySQL.");
            MCPets.getInstance().getLogger().severe("[Database] Will be using YAML support instead.");
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
                PlayerData pd = PlayerData.getEmpty(uuid);

                // Unserialize the pet stats first, coz it influences the inventories
                PetStats.remove(uuid);

                for(String seria : playerData.getString("data").split(";;;"))
                {
                    PetStats stats = PetStats.unzerialize(seria);
                    if(stats == null)
                        continue;
                    stats.launchTimers();
                    PetStats.register(stats);
                }

                // Unserialize the pet names
                pd.setMapOfRegisteredNames(unserializeData(playerData, "names"));
                // Unserialize the pet inventories
                pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                for(String petId : pd.getMapOfRegisteredInventories().keySet())
                {
                    String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                    PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                }

                PlayerData.getRegisteredData().put(uuid, pd);

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
            PlayerData pd = PlayerData.getRegisteredData().get(uuid);

            String names = buildStringSerialized(pd.getMapOfRegisteredNames());
            String inventories = buildStringSerialized(pd.getMapOfRegisteredInventories());

            StringBuilder data = new StringBuilder();
            for(PetStats stats : PetStats.getPetStats(uuid))
            {
                data.append(stats.serialize()).append(";;;");
            }
            if(data.length() > 0)
                data = new StringBuilder(data.substring(0, data.length() - 3));

            getMySQL().query("INSERT INTO " + table + " (uuid, names, inventories, data) VALUES ('" + uuid.toString()
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

    public static HashMap<String, String> unserializeData(ResultSet resultSet, String targetedColumn) throws SQLException
    {
        String targetedResults = resultSet.getString(targetedColumn);
        HashMap<String, String> outputMap = new HashMap<>();

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
