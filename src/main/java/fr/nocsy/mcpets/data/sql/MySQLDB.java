package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLDB {

    private Connection sqlCon;
    private String user = null;
    private String pass = null;
    private String ip = null;
    private String port = null;
    private String db = null;

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
            Class.forName("com.mysql.jdbc.Driver");
            String url = urlBuilder();
            this.sqlCon = DriverManager.getConnection(url, this.user, this.pass);
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String urlBuilder() {
        return "jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.db;
    }

    public ResultSet query(String s) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;
        try {
            if (!this.sqlCon.isValid(2)) {
                this.sqlCon.close();
                this.init();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
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
            e.printStackTrace();
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
                    e.printStackTrace();
                }
            }
        }.runTaskLater(MCPets.getInstance(), 5L);

    }
}
