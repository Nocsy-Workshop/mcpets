package fr.nocsy.mcpets.utils;

import java.util.Arrays;
import java.time.Duration;

import org.bukkit.entity.Player;

import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;

public enum PetAnnouncement {

    TITLE("title"),
    CHAT("chat"),
    ACTIONBAR("actionbar");

    private final String announcementType;

    PetAnnouncement(String announcementType) {
        this.announcementType = announcementType;
    }

    public void announce(Player p, String message) {
        switch(announcementType) {
            case "title":
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500));
                String[] cut = message.split("\n");
                if (cut.length == 2) {
                    Component title = Utils.toComponent(cut[0]);
                    Component subtitle = Utils.toComponent(cut[1]);
                    p.showTitle(Title.title(title, subtitle, times));
                } else {
                    p.showTitle(Title.title(Utils.toComponent(message), Component.empty(), times));
                }
                break;

            case "chat":
                p.sendMessage(Utils.toComponentWithPrefix(message));
                break;
            case "actionbar":
                p.sendActionBar(Utils.toComponent(message));
                break;
            default:
                p.sendMessage(Utils.toComponentWithPrefix(message));
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
