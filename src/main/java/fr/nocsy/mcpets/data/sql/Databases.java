package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PlayerData;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Databases {

    @Getter
    @Setter
    public static MySQLDB mySQL;

    public static boolean init() {
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
        if(!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        getMySQL().query("CREATE TABLE IF NOT EXISTS player_data (id INT NOT NULL AUTO_INCREMENT, uuid TEXT, names TEXT, primary key (id));");
    }

    public static boolean loadData() {
        if(!GlobalConfig.getInstance().isDatabaseSupport())
            return false;

        ResultSet playerData = getMySQL().query("SELECT * FROM player_data;");
        if(playerData == null)
            return true;
        try {
            while (playerData.next()) {
                String uuidStr = playerData.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);
                String names = playerData.getString("names");
                HashMap<String, String> mapName = new HashMap<>();

                String[] namesTable = names.split(";;;");

                for(String seriaPetName : namesTable)
                {
                    // treats the case in which input is empty or wrongly formatted
                    if(seriaPetName == null || seriaPetName.length() == 0 || !seriaPetName.contains(";;"))
                        continue;

                    String[] seriaId_Name = seriaPetName.split(";;");
                    try
                    {
                        String pet_id = seriaId_Name[0];
                        String custom_name = seriaId_Name[1];
                        mapName.put(pet_id, custom_name);
                    }
                    catch(IndexOutOfBoundsException ex)
                    {
                        ex.printStackTrace();
                        MCPets.getInstance().getLogger().severe("[Database] Index out of bound for (65) : " + seriaPetName);
                    }
                }

                PlayerData pd = PlayerData.getEmpty(uuid);
                pd.setMapOfRegisteredNames(mapName);
                PlayerData.getRegisteredData().put(uuid, pd);

            }
        } catch (SQLException e1) {
            return false;
        }

        return true;
    }

    public static void saveData() {
        if(!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        getMySQL().query("TRUNCATE player_data");

        for(PlayerData pd : PlayerData.getRegisteredData().values())
        {
            UUID uuid = pd.getUuid();
            String names = "";

            for(String pet_id : pd.getMapOfRegisteredNames().keySet())
            {
                String custom_name = pd.getMapOfRegisteredNames().get(pet_id);
                String seriaId_name = pet_id + ";;" + custom_name;

                if(names.equals(""))
                    names = seriaId_name;
                else
                    names = names + ";;;" + seriaId_name;
            }

            getMySQL().query("INSERT INTO player_data (uuid, names) VALUES ('" + uuid.toString() + "', '" + names +"')");
        }
    }
}
