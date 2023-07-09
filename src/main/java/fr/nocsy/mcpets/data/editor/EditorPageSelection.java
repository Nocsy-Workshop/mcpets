package fr.nocsy.mcpets.data.editor;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorPageSelection {

    private static HashMap<UUID, Integer> pageview = new HashMap<>();

    public static void set(Player p, int page)
    {
        pageview.put(p.getUniqueId(), page);
    }

    public static int get(Player p)
    {
        if(pageview.containsKey(p.getUniqueId()))
            return pageview.get(p.getUniqueId());
        set(p, 0);
        return pageview.get(p.getUniqueId());
    }

}
