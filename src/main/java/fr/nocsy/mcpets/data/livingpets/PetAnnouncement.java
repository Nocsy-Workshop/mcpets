package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum PetAnnouncement {

    TITLE("title"),
    CHAT("chat"),
    ACTIONBAR("actionbar");

    private String announcementType;

    PetAnnouncement(String annoucementType)
    {
        this.announcementType = annoucementType;
    }

    public void announce(Player p, String message)
    {
        message = ChatColor.translateAlternateColorCodes('&', message);
        switch(announcementType)
        {
            case "title":
                String[] cut = message.split("\n");
                String title = cut[0];
                String subtitle = cut[1];
                p.sendTitle(title, subtitle);
                break;

            case "chat":
                p.sendMessage(GlobalConfig.getInstance().getPrefix() + message);
                break;
            case "actionbar":
                p.sendActionBar(message);
                break;
            default:
                p.sendMessage(GlobalConfig.getInstance().getPrefix() + message);
        }
    }

}
