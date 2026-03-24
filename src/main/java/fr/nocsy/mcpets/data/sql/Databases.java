package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Databases {

    @Getter
    @Setter
    public static MySQLDB mySQL;

    private static String table = GlobalConfig.getInstance().getMySQL_Prefix() + "mcpets_player_data";
    private static String activeTable = GlobalConfig.getInstance().getMySQL_Prefix() + "mcpets_active_pet";
    private static ConcurrentHashMap<UUID, Object> playerLocks = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // Active-pet record: remembers the pet a player had active when they quit,
    // used to restore it on the destination server after a Velocity cross-server
    // switch via the dedicated mcpets_active_pet table.
    // -------------------------------------------------------------------------

    private static final String PET_ID_DELIMITER = ",";

    public static class ActivePetRecord {
        private final List<String> petIds;
        private final long updatedAt;

        public ActivePetRecord(List<String> petIds, long updatedAt) {
            this.petIds = petIds;
            this.updatedAt = updatedAt;
        }

        /** All active pet IDs stored in this record (may be multiple). */
        public List<String> getPetIds()  { return petIds; }
        public long         getUpdatedAt() { return updatedAt; }
    }

    public static boolean init() {
        if (GlobalConfig.getInstance().isDisableMySQL()) {
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
        getMySQL().query("CREATE TABLE IF NOT EXISTS " + table + " (id INT NOT NULL AUTO_INCREMENT, uuid TEXT, names TEXT, inventories LONGTEXT, data LONGTEXT, lastActivePet TEXT, primary key (id));");
        getMySQL().query("ALTER TABLE " + table + " MODIFY inventories LONGTEXT, MODIFY data LONGTEXT;");
        ResultSet rs = getMySQL().preparedQuery("SELECT count(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = 'lastActivePet'",
                GlobalConfig.getInstance().getMySQL_DB(), table);
        try {
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                 getMySQL().query("ALTER TABLE " + table + " ADD lastActivePet TEXT;");
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to check lastActivePet column", e);
        }
        createActivePetTable();
    }

    private static void createActivePetTable() {
        getMySQL().query("CREATE TABLE IF NOT EXISTS " + activeTable
                + " (uuid VARCHAR(36) NOT NULL, pet_id VARCHAR(255) NOT NULL,"
                + " updated_at BIGINT NOT NULL, PRIMARY KEY (uuid))");
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

                    // Unserialize the pet names; strip any legacy __active__ key silently
                    ConcurrentHashMap<String, String> names = unserializeData(playerData, "names");
                    names.remove("__active__");
                    pd.setMapOfRegisteredNames(names);

                    // Unserialize the pet inventories
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    try {
                        pd.setLastActivePet(playerData.getString("lastActivePet"));
                    } catch (SQLException e) {
                        // Column might not exist yet
                    }
                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        }
        catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to load player data from database", e1);
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
        ResultSet playerData = getMySQL().preparedQuery("SELECT * FROM " + table + " WHERE uuid=?", playerUUID.toString());

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

                    // Unserialize the pet names; strip any legacy __active__ key silently
                    ConcurrentHashMap<String, String> names = unserializeData(playerData, "names");
                    names.remove("__active__");
                    pd.setMapOfRegisteredNames(names);

                    // Unserialize the pet inventories
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    try {
                        pd.setLastActivePet(playerData.getString("lastActivePet"));
                    } catch (SQLException e) {
                        // Column might not exist yet
                    }
                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        }
        catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to load player data for " + playerUUID, e1);
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
                String lastActivePet = pd.getLastActivePet();
                if (lastActivePet == null) lastActivePet = "";

                StringBuilder data = new StringBuilder();

                for (PetStats stats : PetStats.getPetStats(uuid)) {
                    data.append(stats.serialize()).append(";;;");
                }
                if (data.length() > 0)
                    data = new StringBuilder(data.substring(0, data.length() - 3));

                getMySQL().preparedQuery("INSERT INTO " + table + " (uuid, names, inventories, data, lastActivePet) VALUES (?, ?, ?, ?, ?)",
                        uuid.toString(), names, inventories, data.toString(), lastActivePet);
            }
        }
    }

    public static void savePlayerData(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        if (!PlayerData.isRegistered(playerUUID))
            return;

        synchronized (getLockForPlayer(playerUUID)) {
            PlayerData pd = PlayerData.getRegisteredData().get(playerUUID);

            String names = buildStringSerialized(pd.getMapOfRegisteredNames());
            String inventories = buildStringSerialized(pd.getMapOfRegisteredInventories());
            String lastActivePet = pd.getLastActivePet();
            if (lastActivePet == null) lastActivePet = "";

            StringBuilder data = new StringBuilder();

            for (PetStats stats : PetStats.getPetStats(playerUUID)) {
                data.append(stats.serialize()).append(";;;");
            }
            if (data.length() > 0)
                data = new StringBuilder(data.substring(0, data.length() - 3));

            // First, delete the existing data for the player
            getMySQL().preparedQuery("DELETE FROM " + table + " WHERE uuid=?", playerUUID.toString());

            // Then, insert the new data for the player
            getMySQL().preparedQuery("INSERT INTO " + table + " (uuid, names, inventories, data, lastActivePet) VALUES (?, ?, ?, ?, ?)",
                    playerUUID.toString(), names, inventories, data.toString(), lastActivePet);
        }
    }

    // -------------------------------------------------------------------------
    // Active-pet persistence for Velocity cross-server switching
    // -------------------------------------------------------------------------

    /**
     * Upsert the active pets for a player into the dedicated table.
     * Multiple pet IDs are stored as a comma-delimited list in the pet_id column.
     * Called synchronously on disconnect (HIGHEST priority) so the record is
     * committed before Velocity routes the player to the next server.
     */
    public static void saveActivePet(UUID uuid, List<String> petIds) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return;
        String joined = String.join(PET_ID_DELIMITER, petIds);
        long now = System.currentTimeMillis();
        getMySQL().preparedQuery(
                "INSERT INTO " + activeTable + " (uuid, pet_id, updated_at) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE pet_id=?, updated_at=?",
                uuid.toString(), joined, now, joined, now);
    }

    /**
     * Load the active-pet record for a player, or null if none exists.
     * Called on join to check whether the player arrived via a cross-server switch.
     */
    public static ActivePetRecord loadActivePet(UUID uuid) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return null;
        ResultSet rs = getMySQL().preparedQuery(
                "SELECT pet_id, updated_at FROM " + activeTable + " WHERE uuid=?",
                uuid.toString());
        if (rs == null) return null;
        try {
            if (rs.next()) {
                String raw = rs.getString("pet_id");
                List<String> ids = Arrays.stream(raw.split(PET_ID_DELIMITER))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                return new ActivePetRecord(ids, rs.getLong("updated_at"));
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to load active pet record for " + uuid, e);
        }
        return null;
    }

    /**
     * Delete the active-pet record for a player once it has been consumed
     * (pet spawned on destination) or when it is no longer needed.
     */
    public static void clearActivePet(UUID uuid) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return;
        getMySQL().preparedQuery("DELETE FROM " + activeTable + " WHERE uuid=?", uuid.toString());
    }

    public static void closeConnection() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        mySQL.close();
    }

    private static String buildStringSerialized(Map<String,String> map) {
        String builder = "";
        for (String id : map.keySet()) {
            String seria = map.get(id);
            String seriaId = id + ";;" + seria;
            if (builder.isBlank())
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
            }
            catch (IndexOutOfBoundsException ex) {
                MCPets.getInstance().getLogger().log(Level.SEVERE, "[Database] Index out of bound for deserialization: " + seriaContents, ex);
            }
        }

        return outputMap;
    }
}
