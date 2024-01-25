package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Language {

    INVENTORY_PETS_MENU("§0☀ §4宠物 §0☀"),
    INVENTORY_PETS_MENU_INTERACTIONS("§0☀ §4宠物 §0☀"),

    MOUNT_ITEM_NAME("§6骑乘"),
    MOUNT_ITEM_DESCRIPTION("§7点击骑乘你的宠物"),

    RENAME_ITEM_NAME("§6重命名"),
    RENAME_ITEM_DESCRIPTION("§7点击重命名你的宠物"),

    BACK_TO_PETMENU_ITEM_NAME("§c返回菜单"),
    BACK_TO_PETMENU_ITEM_DESCRIPTION("§7点击返回菜单"),

    INVENTORY_ITEM_NAME("§6背包"),
    INVENTORY_ITEM_DESCRIPTION("§7点击打开宠物的背包"),

    SKINS_ITEM_NAME("§6皮肤"),
    SKINS_ITEM_DESCRIPTION("§7点击更改你的宠物皮肤"),

    EQUIPMENT_ITEM_NAME("§6装备"),
    EQUIPMENT_DESCRIPTION("§7点击打开宠物的装备"),

    TURNPAGE_ITEM_NAME("§6下一页 §7(§e%currentPage%§8/§7%maxPage%)"),
    TURNPAGE_ITEM_DESCRIPTION("§e右键点击§7前进 \n§a左键点击§7后退"),

    NICKNAME("§9昵称 : §7%nickname%"),
    NICKNAME_ITEM_LORE("§c点击此处撤销你的宠物"),

    SUMMONED("§7一个宠物已被召唤!"),
    REVOKED("§7你的宠物已被撤销."),
    REVOKED_FOR_NEW_ONE("§7你之前的宠物已被撤销以召唤新的宠物."),
    REVOKED_UNKNOWN("§c由于以下原因,宠物无法被召唤:" +
            "\n§7- 提供的宠物配置文件中的§c神话生物不存在§7(尝试通过/mm m spawn来召唤)§7." +
            "\n§7- 世界处于§c和平或简单模式§7." +
            "\n§7- 某个区域§c阻止生物生成§7(锚点很可能是一个攻击性生物)." +
            "\n§7- 你有一个§c生成保护插件§7,请尝试在另一个世界或远离生成点生成生物." +
            "\n§7- 存在具有相同id的其他宠物§7.确保你的id是唯一的."),
    MYTHICMOB_NULL("§c无法召唤此宠物.关联的神话生物实体或文件为空或已被移除."),
    NO_MOB_MATCH("§c无法召唤此宠物.关联的神话生物在神话生物中未注册."),
    NOT_ALLOWED("§c你无权召唤此宠物."),
    OWNER_NOT_FOUND("§c无法召唤此宠物.召唤者未找到."),
    REVOKED_BEFORE_CHANGES("§c在修改生效之前,你的宠物已被撤销."),
    NOT_MOUNTABLE("§c此宠物无法骑乘."),
    ALREADY_MOUNTING("§c你已经在骑乘.请先下来再试."),
    NOT_MOUNTABLE_HERE("§c你不能在这个区域骑乘宠物."),
    CANT_MOUNT_PET_YET("§c你没有权限骑乘该宠物."),
    CANT_FOLLOW_HERE("§c你的宠物无法在这个区域跟随你."),
    TYPE_NAME_IN_CHAT("§7请输入一个名字到聊天栏:"),
    IF_WISH_TO_REMOVE_NAME("§a如果您希望删除,请在聊天中输入 §c%tag%§a."),
    NICKNAME_CHANGED_SUCCESSFULY("§a昵称已成功更改!"),
    NICKNAME_NOT_CHANGED("§c昵称无法更改,因为空字符串.请重试."),
    TAG_TO_REMOVE_NAME("无"),
    ALREADY_INSIDE_VEHICULE("§7您已经在骑乘某物.请解除当前骑乘以使用此功能."),
    PET_DOESNT_EXIST("§c宠物不存在.请检查ID."),
    PLAYER_NOT_CONNECTED("§c玩家 §6%player%§c 未连接."),
    BLACKLISTED_WORD("§c重命名操作已取消.名称中不允许使用单词 %word%."),
    NO_ACTIVE_PET("§c您没有活跃的宠物."),
    SIGNAL_STICK_GIVEN("§a您已收到指令棒.右键点击以发出指令,左键点击以切换指令."),
    SIGNAL_STICK_SIGNAL("§6活跃指令:§e%signal%"),
    LOOP_SPAWN("§c您的宠物已被撤销,因为似乎无法处理多次传送."),
    REQUIRES_ITEM_IN_HAND("§c您必须手持物品才能更新配置."),
    ITEM_UPDATED("§a成功使用键:§e%key% 更新物品."),
    ITEM_DOESNT_EXIST("§a具有键 §e%key%§c 的物品不存在.如果要添加它,可以使用 §eadd§c 参数."),
    KEY_DOESNT_EXIST("§c指定的键未注册."),
    KEY_REMOVED("§a成功移除键物品."),
    KEY_ALREADY_EXISTS("§c此键已注册.使用它替换当前物品."),
    KEY_ADDED("§a成功添加键,对应的物品."),
    KEY_LIST("§a可用键:"),

    RELOAD_SUCCESS("§a重新加载成功.汉化作者:jhqwqmc"),
    HOW_MANY_PETS_LOADED("§a%numberofpets% 宠物已成功注册"),

    REQUIRES_MODELENGINE("§c此插件需要 ModelEngine r2.3.1.似乎未满足此要求."),

    USAGE("§c此命令不存在.\n§7查看 wiki:§nhttps://alexandre-chaussard.gitbook.io/mcpets/tutorials/plugin-features/commands"),
    NO_PERM("§c您无权使用此命令."),
    BLACKLISTED_WORLD("§cMCPets 在此世界中已禁用."),

    CATEGORY_MENU_TITLE("§0☀ §4宠物 §8- 选择一个类别 §0☀"),
    CATEGORY_DOESNT_EXIST("§c此类别不存在."),

    PET_INVENTORY_TITLE("§0☀ §4%pet% §8- §0物品栏 §0☀§"),

    PET_INVENTORY_COULDNOT_OPEN("§c无法打开此物品栏,因为可能不存在."),

    PET_SKINS_TITLE("§0☀ §4%pet% §8- §0皮肤 §0☀§"),

    SKIN_COULD_NOT_APPLY("§c无法将皮肤应用于宠物."),
    SKIN_APPLIED("§a皮肤成功更改!"),

    GLOBAL_RESPAWN_TIMER_RUNNING("§c无法生成此宠物.您需要等待 %timeLeft%s/%cooldown%s."),
    RESPAWN_TIMER_RUNNING("§c无法生成此宠物.它仍在恢复中.您需要等待 %timeLeft%s/%cooldown%s."),
    REVOKE_TIMER_RUNNING("§c无法生成此宠物.它仍在恢复中.您需要等待 %timeLeft%s/%cooldown%s."),

    PLAYER_OR_PET_DOESNT_EXIST("§c此宠物不存在,或此玩家从未在您的服务器上玩过."),
    STATS_CLEARED("§a所有统计数据已成功清除!"),
    STATS_CLEARED_FOR_PET_FOR_PLAYER("§a已成功为玩家 %player% 的宠物 %petId% 清除所有统计数据."),
    STATS_CLEARED_FOR_PET("§a已成功为宠物 %petId% 清除所有统计数据"),

    PET_TAMING_PROGRESS("§7驯服进度 %progress%% - %progressbar%"),
    PET_COULD_NOT_EVOLVE("§7您的宠物无法进化,因为您已拥有该进化."),
    PETFOOD_DOESNT_EXIST("§c此宠物食物不存在."),
    PETUNLOCK_NOPERM("§c您无权使用该物品解锁宠物."),
    PETUNLOCKED("§a您已解锁宠物 %petName%,恭喜!"),
    PETUNLOCKED_ALREADY("§c您已拥有宠物 %petName%."),

    PET_ALREADY_TAMED("§c此宠物已被驯服."),
    PET_DOESNT_EAT("§c此宠物无法食用该食物."),

    PET_STATUS_ALIVE("§a可用"),
    PET_STATUS_REVOKED("§c不可用(剩余 %timeleft%s)"),
    PET_STATUS_DEAD("§c已死亡(剩余 %timeleft%s)"),

    PET_STATS("§6✦ 宠物信息 ✦" +
            "\n§7状态:%status%" +
            "\n§6等级:§7- §6%levelname%" +
            "\n " +
            "\n§f%health%§7/§f%maxhealth% §c❤" +
            "\n§7恢复:%regeneration% ❤/秒" +
            "\n§7伤害修饰:%damagemodifier%%" +
            "\n§7抗性修饰:%resistancemodifier%%" +
            "\n§7力量:%power%%" +
            "\n " +
            "\n§7经验:%experience%/%threshold% 经验" +
            "\n%progressbar%"),

    DEBUGGER_JOINING("§a调试器已启用.您现在正在监听它."),
    DEBUGGER_LEAVE("§a调试器已§7禁用§a.您将不再监听它.");

    private String message;

    Language(String message) {
        this.message = message;
    }

    public void reload() {
        if (LanguageConfig.getInstance().getMap().containsKey(this.name().toLowerCase())) {
            this.message = LanguageConfig.getInstance().getMap().get(this.name().toLowerCase());
        }
    }

    public String getMessage()
    {
        String m = Utils.hex(message);

        m = Utils.applyPlaceholders(null, m);
        return m;
    }

    public String getMessagePAPI(Player p)
    {
        String m = Utils.hex(message);

        m = Utils.applyPlaceholders(null, m);
        return m;
    }

    public void sendMessage(Player p) {
        if(message.isEmpty())
            return;
        p.sendMessage(Utils.hex(GlobalConfig.getInstance().getPrefix() + getMessagePAPI(p)));
    }

    public void sendMessage(CommandSender sender) {
        if(message.isEmpty())
            return;
        sender.sendMessage(Utils.hex(GlobalConfig.getInstance().getPrefix() + getMessage()));
    }

    public void sendMessageFormated(CommandSender sender, FormatArg... args) {
        if(message.isEmpty())
            return;
        String toSend = getMessage();
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        sender.sendMessage(Utils.hex(GlobalConfig.getInstance().getPrefix() + toSend));
    }

    public String getMessageFormatted(FormatArg... args) {
        String toSend = getMessage();
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        return toSend;
    }

}
