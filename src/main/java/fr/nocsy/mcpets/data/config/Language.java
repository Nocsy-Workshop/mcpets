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

    TURNPAGE_ITEM_NAME("§6Next page"),
    TURNPAGE_ITEM_DESCRIPTION("§eRight click§7 to go forward \n§aLeft click§7 to go backward"),

    NICKNAME("§9Nickname : §7%nickname%"),
    NICKNAME_ITEM_LORE("§cClick here to revoke your pet"),

    SUMMONED("§7A pet has been summoned !"),
    REVOKED("§7Your pet was revoked."),
    REVOKED_FOR_NEW_ONE("§7Your previous pet was revoked to summon the new one."),
    MYTHICMOB_NULL("§cThis pet could not be summoned. The associated mythicMob entity or file is null or was removed."),
    NO_MOB_MATCH("§cThis pet could not be summoned. The associated mythicmob isn't registered in MythicMobs."),
    NOT_ALLOWED("§cYou're not allowed to summon this pet."),
    OWNER_NOT_FOUND("§cThis pet could not be summoned. The summoner couldn't be found."),
    REVOKED_BEFORE_CHANGES("§cYour pet was revoked before the modifications could take place."),
    NOT_MOUNTABLE("§cThis pet has no mounting point."),
    NOT_MOUNTABLE_HERE("§cYou can't ride a pet in this area."),
    CANT_FOLLOW_HERE("§cYour pet can't follow you in this area."),
    TYPE_NAME_IN_CHAT("§aRight down in the chat the name of your pet."),
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

    USAGE("§7Usage : §6/mcpets §8..." +
            "\n§8   ... §areload " +
            "\n§8   ... §7(nothing here to open the GUI) " +
            "\n§8   ... §aopen §8<§7player§8>" +
            "\n§8   ... §aspawn §8<§7id§8> <§7player§8> §atrue§8/§cfalse §7(check if the player have the permission to spawn the pet or not)" +
            "\n§8   ... §arevoke" +
            "\n§8   ... §aname" +
            "\n§8   ... §amount" +
            "\n§8   ... §aitem §7<§elist§8/§8add/§8§7remove> <key>" +
            "\n§8   ... §asignalstick §7<§eplayer§7>"),
    NO_PERM("§cYou're not allowed to use this command.");

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
        p.sendMessage(GlobalConfig.getInstance().getPrefix() + " " + message);
    }

    public void sendMessage(CommandSender sender) {
        sender.sendMessage(GlobalConfig.getInstance().getPrefix() + " " + message);
    }

    public void sendMessageFormated(CommandSender sender, FormatArg... args) {
        String toSend = message;
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        sender.sendMessage(GlobalConfig.getInstance().getPrefix() + " " + toSend);
    }

    public String getMessageFormatted(FormatArg... args) {
        String toSend = message;
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        return toSend;
    }

}
