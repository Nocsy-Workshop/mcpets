package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;

public enum PetAnnouncement {

    TITLE("title"),
    CHAT("chat"),
    ACTIONBAR("actionbar");

    private final String announcementType;

    PetAnnouncement(String annoucementType) {
        this.announcementType = annoucementType;
    }

    public void announce(Player p, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        Audience audience = (Audience) p;

        switch(announcementType) {
            case "title":
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500));
                String[] cut = message.split("\n");
                if (cut.length == 2) {
                    Component title = Utils.toComponent(cut[0]);
                    Component subtitle = Utils.toComponent(cut[1]);
                    audience.showTitle(Title.title(title, subtitle, times));
                }
                else {
                    audience.showTitle(Title.title(Utils.toComponent(message), Component.empty(), times));
                }
                break;

            case "chat":
                audience.sendMessage(Utils.toComponent(GlobalConfig.getInstance().getPrefix() + message));
                break;
            case "actionbar":
                Utils.sendActionBar(p, message);
                break;
            default:
                audience.sendMessage(Utils.toComponent(GlobalConfig.getInstance().getPrefix() + message));
        }
    }

    /**
     * Get the announcement corresponding to the string name
     * Default is CHAT
     */
    public static PetAnnouncement get(String name) {
        return Arrays.stream(PetAnnouncement.values()).filter(petAnnouncement -> petAnnouncement.name().equalsIgnoreCase(name)).findFirst().orElse(PetAnnouncement.CHAT);
    }
}
