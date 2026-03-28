package fr.nocsy.mcpets.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.BlacklistConfig;
import io.lumine.mythic.api.skills.Skill;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static ItemStack createHead(final String name, final List<String> lore, final String base64) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        try {
            final byte[] decodedBytes = Base64.getDecoder().decode(base64);
            final String decodedString = new String(decodedBytes);

            final JsonParser parser = new JsonParser();
            final JsonObject jsonObject = parser.parse(decodedString).getAsJsonObject();
            final String url = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

            final PlayerProfile pp = Bukkit.createPlayerProfile(UUID.fromString("4fbecd49-c7d4-4c18-8410-adf7a7348728"));
            final PlayerTextures pt = pp.getTextures();

            URL urlObject = null;
            try {
                urlObject = new URL(url);
            }
            catch (final MalformedURLException e) {
                try {
                    urlObject = new URL("http://textures.minecraft.net/texture/8dcfabbbb4d7b0381135bf07b6af3de920ab4c366c06c37fa4c4e8b8f43bbb2b");
                }
                catch (final MalformedURLException malformedURLException) {
                    MCPets.getLog().log(Level.SEVERE, "Failed to parse fallback texture URL", malformedURLException);
                }
            }

            pt.setSkin(urlObject);
            pp.setTextures(pt);
            meta.setOwnerProfile(pp);
            item.setItemMeta(meta);
            return item;
        }
        catch (final Exception e) {
            item.setItemMeta(meta);
            return item;
        }
    }

    public static double distance(final Location loc1, final Location loc2) {
        final double x1 = loc1.getX();
        final double y1 = loc1.getY();
        final double z1 = loc1.getZ();

        final double x2 = loc2.getX();
        final double y2 = loc2.getY();
        final double z2 = loc2.getZ();

        final double square = (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));

        return Math.sqrt(square);
    }

    /**
     * Location bruiser
     */
    public static Location bruised(Location loc, final double distance) {
        final Location origin = loc.clone();

        final Random random = new Random();
        final double r = Math.min(1, distance) + (Math.max(distance - 0.1, 1) - Math.min(1, distance)) * random.nextDouble();
        final double theta = 2 * Math.PI * random.nextDouble();

        final double x = r * Math.cos(theta) + loc.getX();
        final double z = r * Math.sin(theta) + loc.getZ();
        final double y = loc.getY();

        loc = new Location(loc.getWorld(), x, y, z);

        final int threshHoldY = 5;
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
     */
    public static String hex(String message) {
        final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            final String hexCode = message.substring(matcher.start(), matcher.end());
            final String replaceSharp = hexCode.replace('#', 'x');

            final char[] ch = replaceSharp.toCharArray();
            final StringBuilder builder = new StringBuilder();
            for (final char c : ch) {
                builder.append("&" + c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Convert a legacy color-coded string (§ codes) to an Adventure Component
     */
    public static Component toComponent(final String legacyText) {
        return LegacyComponentSerializer.legacySection().deserialize(legacyText);
    }

    /**
     * Convert a string with § codes to plain text (no formatting)
     */
    public static String stripColors(final String text) {
        return PlainTextComponentSerializer.plainText().serialize(toComponent(text));
    }

    public static void sendActionBar(final Player p, final String message) {
        ((Audience) p).sendActionBar(toComponent(message));
    }

    /**
     * Says whether the string is in the blacklist of words
     */
    public static String isInBlackList(final String s) {
        final String toMatch = stripColors(s).toLowerCase();

        for (final String blackListedWord : BlacklistConfig.getInstance().getBlackListedWords()) {
            if (toMatch.contains(blackListedWord.toLowerCase())) {
                return blackListedWord;
            }
        }
        return null;
    }

    /**
     * Get the sign symbol of the value
     * Return an empty string if it's negative to prevent duplicating issue
     */
    public static String getSignSymbol(final double value) {
        if (value < 0)
            return "";
        else
            return "+";
    }

    /**
     * Used to call any event
     */
    public static void callEvent(final Event e) {
        Bukkit.getPluginManager().callEvent(e);
    }

    /**
     * Private debugger for Nocsy
     */
    public static void debug(final String msg) {
        final Player p = Bukkit.getPlayer("Nocsy");
        if (p != null) {
            p.sendMessage(msg);
        }
        MCPets.getLog().severe("[DEBUG]: " + msg);
    }

    /**
     * Give permission to a player (based on LuckPerms)
     * Return false if we are unable to give the permission on a long term basis
     */
    public static boolean givePermission(final UUID uuid, final String permission) {
        if (MCPets.getLuckPerms() != null) {
            return PermsUtils.givePermission(uuid, permission);
        }

        if (Bukkit.getPlayer(uuid) != null) {
            // This is not saved in any file, just in the MCPets instance so it's not a viable solution
            // Hence we return false
            Bukkit.getPlayer(uuid).addAttachment(MCPets.getInstance(), permission, true);
            return false;
        }
        return false;
    }

    /**
     * Remove permission to the player
     */
    public static boolean removePermission(final UUID uuid, final String permission) {
        if (MCPets.getLuckPerms() != null) {
            PermsUtils.removePermission(uuid, permission);
            return true;
        }

        return false;
    }

    /**
     * Give permission async, returning a future that completes once LuckPerms has applied the change.
     */
    public static CompletableFuture<Void> givePermissionAsync(final UUID uuid, final String permission) {
        return PermsUtils.givePermissionAsync(uuid, permission);
    }

    /**
     * Remove permission async, returning a future that completes once LuckPerms has applied the change.
     */
    public static CompletableFuture<Void> removePermissionAsync(final UUID uuid, final String permission) {
        return PermsUtils.removePermissionAsync(uuid, permission);
    }

    /**
     * Check if the player has the permission
     */
    public static boolean hasPermission(@NotNull final UUID uuid, final String permission) {
        boolean hasPerm = false;
        final Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            hasPerm = p.hasPermission(permission);
        }
        if (!hasPerm && MCPets.getLuckPerms() != null) {
            return PermsUtils.hasPermission(uuid, permission);
        }
        return hasPerm;
    }

    /**
     * Translate the string to hex color code
     */
    public static String translateHexColorCodes(final String startTag, final String endTag, final String message) {
        final char COLOR_CHAR = ChatColor.COLOR_CHAR;
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        final Matcher matcher = hexPattern.matcher(message);
        final StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            final String group = matcher.group(1);
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
     */
    public static boolean isNumeric(final String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            final double d = Double.parseDouble(strNum);
        }
        catch (final NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String applyPlaceholders(UUID uuid, final String msg) {
        if (MCPets.getPlaceholderAPI() == null) {
            return msg;
        }
        if (uuid == null)
            uuid = UUID.randomUUID();
        final Player p = Bukkit.getPlayer(uuid);
        if (p == null)
            return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(uuid), msg);

        return PlaceholderAPI.setPlaceholders(p, msg);
    }

    public static Skill getSkill(final String skillName) {
        final Optional<Skill> optionalSkill = MCPets.getMythicMobs().getSkillManager().getSkill(skillName);
        return optionalSkill.orElse(null);
    }
}
