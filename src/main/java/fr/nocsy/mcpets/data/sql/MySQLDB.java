package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.logging.Level;

public class MySQLDB {

    private Connection sqlCon;
    private String user;
    private String pass;
    private String ip;
    private String port;
    private String db;

    /** Timestamp of the last successful connection validation. */
    private long lastValidationTime = 0;
    /** Minimum interval (ms) between connection validations. */
    private static final long VALIDATION_INTERVAL_MS = 5000;

    public MySQLDB(String user, String pass, String ip, String port, String db) {
        this.user = user;
        this.pass = pass;
        this.ip = ip;
        this.port = port;
        this.db = db;
    }

    public boolean init() {
        if (this.user == null || this.pass == null || this.ip == null || this.port == null || this.db == null) {
            MCPets.getInstance().getLogger().severe("Missing SQL parameter.");
            MCPets.getInstance().getLogger().severe("User : " + user);
            MCPets.getInstance().getLogger().severe("Pass : " + pass);
            MCPets.getInstance().getLogger().severe("Host : " + ip);
            MCPets.getInstance().getLogger().severe("Port : " + port);
            MCPets.getInstance().getLogger().severe("DB : " + db);
            return false;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = urlBuilder();
            this.sqlCon = DriverManager.getConnection(url, this.user, this.pass);
        }
        catch (Exception e) {
            MCPets.getInstance().getLogger().severe("Could not reach SQL database. Please configure your database parameters.");
            return false;
        }
        return true;
    }

    public void close() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        try {
            this.sqlCon.close();
        }
        catch (Exception e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to close SQL connection", e);
        }
    }

    public String urlBuilder() {
        return "jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.db;
    }

    private void ensureConnection() throws SQLException {
        long now = System.currentTimeMillis();
        if (now - lastValidationTime < VALIDATION_INTERVAL_MS) {
            return;
        }
        if (!this.sqlCon.isValid(1)) {
            this.sqlCon.close();
            this.init();
        }
        lastValidationTime = now;
    }

    public ResultSet query(String s) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;
        try {
            ensureConnection();
        } catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to validate SQL connection", e1);
        }
        ResultSet set = null;
        try {
            Statement stat = this.sqlCon.createStatement();
            if (s.toLowerCase().startsWith("select")) {
                set = stat.executeQuery(s);
                closeStat(stat);
            } else {
                stat.executeUpdate(s);
                stat.close();
            }

        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL query failed: " + s, e);
        }
        return set;
    }

    public ResultSet preparedQuery(String sql, Object... params) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;
        try {
            ensureConnection();
        } catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to validate SQL connection", e1);
        }
        ResultSet set = null;
        try {
            PreparedStatement pstmt = this.sqlCon.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            if (sql.trim().toLowerCase().startsWith("select")) {
                set = pstmt.executeQuery();
                closeStat(pstmt);
            } else {
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL prepared query failed: " + sql, e);
        }
        return set;
    }

    private void closeStat(final Statement stat) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    stat.close();
                } catch (SQLException e) {
                    MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to close SQL statement", e);
                }
            }
        }.runTaskLater(MCPets.getInstance(), 5L);

    }
}
