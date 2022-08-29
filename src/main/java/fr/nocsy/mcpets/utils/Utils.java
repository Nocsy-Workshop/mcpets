package fr.nocsy.mcpets.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.BlacklistConfig;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    @SuppressWarnings("deprecation")
    public static ItemStack createHead(String name, List<String> lore, String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        item.setDurability((short) 3);
        SkullMeta headMeta = (SkullMeta) item.getItemMeta();

        headMeta.setDisplayName(name);
        headMeta.setLore(lore);

        item.setItemMeta(headMeta);
        GameProfile profile = new GameProfile(UUID.randomUUID(), "MCPetsHeads");
        profile.getProperties().put("textures", new Property("textures", base64));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException localNoSuchFieldException) {
        }
        item.setItemMeta(headMeta);
        return item;
    }

    public static double distance(Location loc1, Location loc2) {
        double x1 = loc1.getX();
        double y1 = loc1.getY();
        double z1 = loc1.getZ();

        double x2 = loc2.getX();
        double y2 = loc2.getY();
        double z2 = loc2.getZ();

        double square = (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));

        return Math.sqrt(square);

    }

    /**
     * Location bruiser
     *
     * @param loc
     * @return
     */
    public static Location bruised(Location loc, double distance) {
        Location origin = loc.clone();

        Random random = new Random();
        double r = Math.min(1, distance) + (Math.max(distance - 0.1, 1) - Math.min(1, distance)) * random.nextDouble();
        double theta = 2 * Math.PI * random.nextDouble();

        double x = r * Math.cos(theta) + loc.getX();
        double z = r * Math.sin(theta) + loc.getZ();
        double y = loc.getY();

        loc = new Location(loc.getWorld(), x, y, z);

        int threshHoldY = 5;
        int maxY = 0;
        while (!loc.getBlock().isPassable() && maxY < threshHoldY) {
            loc.add(0, 1, 0);
            maxY++;
        }
        if (maxY == threshHoldY) {
            return origin;
        }
        return loc;
    }

    /**
     * Translate hexadecimal colors
     *
     * @param message
     * @return
     */
    public static String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&" + c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendActionBar(Player p, String message) {
        TextComponent text_component = new TextComponent(message);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, text_component);
    }

    /**
     * Says whether or not the string is in the blacklist of words
     *
     * @param s
     * @return
     */
    public static String isInBlackList(String s) {
        String toMatch = ChatColor.stripColor(s).toLowerCase();

        for (String blackListedWord : BlacklistConfig.getInstance().getBlackListedWords()) {
            if (toMatch.contains(blackListedWord.toLowerCase())) {
                return blackListedWord;
            }
        }
        return null;
    }

    /**
     * Give permission to a player (based on LuckPerms)
     * Return false if we are unable to give the permission on a long term basis
     * @param uuid
     * @param permission
     * @return
     */
    public static boolean givePermission(UUID uuid, String permission)
    {
        if(MCPets.getLuckPerms() != null)
        {
            MCPets.getLuckPerms().getUserManager().modifyUser(uuid, user -> user.data().add(Node.builder(permission).build()));
            return true;
        }

        if(Bukkit.getPlayer(uuid) != null)
        {
            // This is not saved in any file, just in the MCPets instance so it's not a viable solution
            // Hence we return false
            Bukkit.getPlayer(uuid).addAttachment(MCPets.getInstance(), permission, true);
            return false;
        }
        return false;
    }

    public static void callEvent(Event e) {
        Bukkit.getPluginManager().callEvent(e);
    }

}
