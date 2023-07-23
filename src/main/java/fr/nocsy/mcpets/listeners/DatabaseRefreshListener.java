package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.sql.Databases;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DatabaseRefreshListener implements Listener {

    @EventHandler
    public void refresh_DB_onjoin(PlayerJoinEvent e)
    {
        // We only do that in the case of the Database support
        if(GlobalConfig.getInstance().isDatabaseSupport())
        {
            // TODO: that is quite a heavy trick, I'm not really sure how it is going to impact the performances
            // Any feedback on it would be highly appreciated
            Databases.loadData(e.getPlayer().getUniqueId());
        }
    }

}
