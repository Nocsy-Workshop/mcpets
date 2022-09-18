package fr.nocsy.mcpets.data.config;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Language {

    INVENTORY_PETS_MENU("§0☀ §4Pets §0☀"),
    INVENTORY_PETS_MENU_INTERACTIONS("§0☀ §4Pet §0☀"),

    MOUNT_ITEM_NAME("§6Mount"),
    MOUNT_ITEM_DESCRIPTION("§7Click to mount your pet"),

    RENAME_ITEM_NAME("§6Rename"),
    RENAME_ITEM_DESCRIPTION("§7Click to rename your pet"),

    BACK_TO_PETMENU_ITEM_NAME("§cBack to menu"),
    BACK_TO_PETMENU_ITEM_DESCRIPTION("§7Click to get back to the menu"),

    INVENTORY_ITEM_NAME("§6Inventory"),
    INVENTORY_ITEM_DESCRIPTION("§7Click to open the pet's inventory"),

    SKINS_ITEM_NAME("§6Skins"),
    SKINS_ITEM_DESCRIPTION("§7Click to change your pet's skin"),

    EQUIPMENT_ITEM_NAME("§6Equipment"),
    EQUIPMENT_DESCRIPTION("§7Click to open your pet's equipment"),

    TURNPAGE_ITEM_NAME("§6Next page §7(§e%currentPage%§8/§7%maxPage%)"),
    TURNPAGE_ITEM_DESCRIPTION("§eRight click§7 to go forward \n§aLeft click§7 to go backward"),

    NICKNAME("§9Nickname : §7%nickname%"),
    NICKNAME_ITEM_LORE("§cClick here to revoke your pet"),

    SUMMONED("§7A pet has been summoned !"),
    REVOKED("§7Your pet was revoked."),
    REVOKED_FOR_NEW_ONE("§7Your previous pet was revoked to summon the new one."),
    REVOKED_UNKNOWN("§cThe pet could not be spawned due to one of the following reasons :" +
            "\n§7- The provided §cMythicMob in the pet config doesn't exist§7 (try to spawn it through /mm m spawn)§7." +
            "\n§7- The world is on §cpeaceful or easy mode§7." +
            "\n§7- A region §cprevents the mob from spawning§7 (the anchor is an aggressive mob most likely)." +
            "\n§7- You have a §cspawn protector plugin§7, try to spawn the mob in another world or far from spawn." +
            "\n§7- There exist other pets with the §csame id§7. Make sure you have unique ids."),
    MYTHICMOB_NULL("§cThis pet could not be summoned. The associated mythicMob entity or file is null or was removed."),
    NO_MOB_MATCH("§cThis pet could not be summoned. The associated mythicmob isn't registered in MythicMobs."),
    NOT_ALLOWED("§cYou're not allowed to summon this pet."),
    OWNER_NOT_FOUND("§cThis pet could not be summoned. The summoner couldn't be found."),
    REVOKED_BEFORE_CHANGES("§cYour pet was revoked before the modifications could take place."),
    NOT_MOUNTABLE("§cThis pet has no mounting point."),
    NOT_MOUNTABLE_HERE("§cYou can't ride a pet in this area."),
    CANT_FOLLOW_HERE("§cYour pet can't follow you in this area."),
    TYPE_NAME_IN_CHAT("§aWrite down in the chat the name of your pet."),
    IF_WISH_TO_REMOVE_NAME("§aIf you wish to remove it, write §c%tag%§a in the chat."),
    NICKNAME_CHANGED_SUCCESSFULY("§aNickname successfully changed !"),
    TAG_TO_REMOVE_NAME("None"),
    ALREADY_INSIDE_VEHICULE("§7You're already mounting something. Please dismount your current mount to use this feature."),
    PET_DOESNT_EXIST("§cThis pet doesn't exist. Please check the id."),
    PLAYER_NOT_CONNECTED("§cThe player §6%player%§c isn't connected."),
    BLACKLISTED_WORD("§cRename operation has been cancelled. The word %word% is not allowed in a pet name."),
    NO_ACTIVE_PET("§cYou have no active pet."),
    SIGNAL_STICK_GIVEN("§aYou've received an order stick. Right click to cast an order, left click to switch orders."),
    SIGNAL_STICK_SIGNAL("§6Active order : §e%signal%"),
    LOOP_SPAWN("§cYour pet was revoked because it seems to struggle with numerous teleportations."),
    REQUIRES_ITEM_IN_HAND("§cYou must holding an item in your hand it update it in the config."),
    ITEM_UPDATED("§aItem updated successful with the key : §e%key%"),
    ITEM_DOESNT_EXIST("§aThe item with the key §e%key%§c doesn't exist. If you want to add it you can use the §eadd§c argument instead."),
    KEY_DOESNT_EXIST("§cThe specified key is not registered."),
    KEY_REMOVED("§aThe key item was removed succesfully."),
    KEY_ALREADY_EXISTS("§cThis key is already registered. Use it to replace the current item."),
    KEY_ADDED("§aKey added successfully with the corresponding item."),
    KEY_LIST("§aAvailable keys :"),

    RELOAD_SUCCESS("§aReloaded successfully."),
    HOW_MANY_PETS_LOADED("§a%numberofpets% were registered successfully"),

    REQUIRES_MODELENGINE("§cThis plugin requires ModelEngine r2.3.1. It seems that this requirement is not satisfied."),

    USAGE("§cThis command doesn't exist. \n§7Check out the wiki: §nhttps://alexandre-chaussard.gitbook.io/mcpets/tutorials/plugin-features/commands"),
    NO_PERM("§cYou're not allowed to use this command."),
    BLACKLISTED_WORLD("§cMCPets is disabled in this world."),

    CATEGORY_PET_AMOUNT("§e%petAmount% §6registered"),
    CATEGORY_MENU_TITLE("§0☀ §4Pets §8- Pick a category §0☀"),

    PET_INVENTORY_TITLE("§0☀ §4%pet% §8- §0Inventory §0☀§"),

    PET_SKINS_TITLE("§0☀ §4%pet% §8- §0Skins §0☀§"),

    SKIN_COULD_NOT_APPLY("§cThe skin could not be applied to the pet."),
    SKIN_APPLIED("§aSkin changed successfully !"),

    GLOBAL_RESPAWN_TIMER_RUNNING("§cThis pet could not be spawned. You need to wait %timeLeft%s/%cooldown%s."),
    RESPAWN_TIMER_RUNNING("§cThis pet could not be spawned. It's still recovering from its wounds. You need to wait %timeLeft%s/%cooldown%s."),
    REVOKE_TIMER_RUNNING("§cThis pet could not be spawned. It's still recovering from its wounds. You need to wait %timeLeft%s/%cooldown%s."),

    PLAYER_OR_PET_DOESNT_EXIST("§cThis pet doesn't exist, or this player has never played on your server."),
    STATS_CLEARED("§aAll stats have been cleared successfully !"),
    STATS_CLEARED_FOR_PET_FOR_PLAYER("§aAll stats have been cleared successfully for the pet %petId% for the player %player%."),
    STATS_CLEARED_FOR_PET("§aAll stats have been cleared successfully for the pet %petId%"),

    PET_TAMING_PROGRESS("§7Taming progress §a%progress%% §7- %progressbar%"),

    PETFOOD_DOESNT_EXIST("§cThis pet food doesn't exist."),

    PET_ALREADY_TAMED("§cThis pet is already tamed."),

    PET_STATUS_ALIVE("§aAvailable"),
    PET_STATUS_REVOKED("§cUnavailable §7(%timeleft%s left)"),
    PET_STATUS_DEAD("§cDead §7(%timeleft%s left)"),

    PET_STATS("§6✦ Pet's Information ✦" +
            "\n§7Status: %status%" +
            "\n§6Level §7- §6%levelname%" +
            "\n " +
            "\n§f%health%§7/§f%maxhealth% §c❤" +
            "\n§7Regeneration: %regeneration% ❤/s" +
            "\n§7Damage Modifier: §f%damagemodifier%%" +
            "\n§7Resistance Modifier: §f%resistancemodifier%%" +
            "\n§7Power: §f%power%%" +
            "\n " +
            "\n§7Experience: §a%experience%/%threshold% xp" +
            "\n%progressbar%");

    @Getter
    private String message;

    Language(String message) {
        this.message = message;
    }

    public void reload() {
        if (LanguageConfig.getInstance().getMap().containsKey(this.name().toLowerCase())) {
            this.message = LanguageConfig.getInstance().getMap().get(this.name().toLowerCase());
        }
    }

    public void sendMessage(Player p) {
        p.sendMessage(GlobalConfig.getInstance().getPrefix() + message);
    }

    public void sendMessage(CommandSender sender) {
        if(message.isEmpty())
            return;
        sender.sendMessage(GlobalConfig.getInstance().getPrefix() + message);
    }

    public void sendMessageFormated(CommandSender sender, FormatArg... args) {
        if(message.isEmpty())
            return;
        String toSend = message;
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        sender.sendMessage(GlobalConfig.getInstance().getPrefix() + toSend);
    }

    public String getMessageFormatted(FormatArg... args) {
        String toSend = message;
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        return toSend;
    }

}
