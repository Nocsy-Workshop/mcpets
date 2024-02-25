package fr.nocsy.mcpets.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.BlacklistConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static ItemStack createHead(String name, List<String> lore, String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            String decodedString = new String(decodedBytes);

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(decodedString).getAsJsonObject();
            String url = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

            PlayerProfile pp = Bukkit.createPlayerProfile(UUID.fromString("4fbecd49-c7d4-4c18-8410-adf7a7348728"));
            PlayerTextures pt = pp.getTextures();
            URL urlObject = null;
            try {
                urlObject = new URL(url);
            } catch (MalformedURLException e) {
                try {
                    urlObject = new URL("http://textures.minecraft.net/texture/8dcfabbbb4d7b0381135bf07b6af3de920ab4c366c06c37fa4c4e8b8f43bbb2b");
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }

            pt.setSkin(urlObject);
            pp.setTextures(pt);
            meta.setOwnerProfile(pp);
            item.setItemMeta(meta);
            return item;
        } catch (Exception e) {
            item.setItemMeta(meta);
            return item;
        }
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
     * Get the sign symbol of the value
     * Return an empty string if it's negative to prevent duplicating issue
     * @param value
     * @return
     */
    public static String getSignSymbol(double value)
    {
        if(value < 0)
            return "";
        else
            return "+";
    }

    /**
     * Used to call any event
     * @param e
     */
    public static void callEvent(Event e) {
        Bukkit.getPluginManager().callEvent(e);
    }

    /**
     * Private debugger for Nocsy
     * @param msg
     */
    public static void debug(String msg)
    {
        Player p = Bukkit.getPlayer("Nocsy");
        if(p != null) {
            p.sendMessage(msg);
        }
        Bukkit.getLogger().severe("[MCPets - 调试]: " + msg);
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
            return PermsUtils.givePermission(uuid, permission);
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

    /**
     * Remove permission to the player
     * @param uuid
     * @param permission
     * @return
     */
    public static boolean removePermission(UUID uuid, String permission)
    {
        if(MCPets.getLuckPerms() != null)
        {
            PermsUtils.removePermission(uuid, permission);
            return true;
        }

        return false;
    }

    /**
     * Check if the player has the permission
     * @param uuid
     * @param permission
     * @return
     */
    public static boolean hasPermission(@NotNull UUID uuid, String permission)
    {
        if(MCPets.getLuckPerms() != null)
        {
            return PermsUtils.hasPermission(uuid, permission);
        }

        Player p = Bukkit.getPlayer(uuid);
        if(p != null)
        {
            return p.hasPermission(permission);
        }

        return false;
    }

    /**
     * Translate the string to hex color code
     * @param startTag
     * @param endTag
     * @param message
     * @return
     */
    public static String translateHexColorCodes(String startTag, String endTag, String message)
    {
        char COLOR_CHAR = ChatColor.COLOR_CHAR;
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    /**
     * Check if a string is a numerical expression
     * @param strNum
     * @return
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String applyPlaceholders(UUID uuid, String msg)
    {
        if(MCPets.getPlaceholderAPI() == null) {
            return msg;
        }
        if(uuid == null)
            uuid = UUID.randomUUID();
        Player p = Bukkit.getPlayer(uuid);
        if(p == null)
            return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(uuid), msg);
        return PlaceholderAPI.setPlaceholders(p, msg);
    }

}
