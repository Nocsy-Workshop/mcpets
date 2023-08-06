package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
                if(cut.length == 2)
                {
                    String title = cut[0];
                    String subtitle = cut[1];
                    p.sendTitle(title, subtitle, 10, 40, 10);
                }
                else
                {
                    p.sendTitle(message, "", 10, 40, 10);
                }
                break;

            case "chat":
                p.sendMessage(GlobalConfig.getInstance().getPrefix() + message);
                break;
            case "actionbar":
                Utils.sendActionBar(p, message);
                break;
            default:
                p.sendMessage(GlobalConfig.getInstance().getPrefix() + message);
        }
    }

    /**
     * Get the announcement corresponding to the string name
     * Default is CHAT
     * @param name
     * @return
     */
    public static PetAnnouncement get(String name)
    {
        return Arrays.stream(PetAnnouncement.values()).filter(petAnnouncement -> petAnnouncement.name().equalsIgnoreCase(name)).findFirst().orElse(PetAnnouncement.CHAT);
    }

}
