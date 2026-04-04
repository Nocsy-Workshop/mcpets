package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentName extends AArgument {

    public ArgumentName(CommandSender sender, String[] args) {
        super("name", new int[]{2}, sender, args, "/mcpets name <name>");
    }

    @Override
    public void commandEffect() {
        if (!(sender instanceof Player p))
            return;

        if (!GlobalConfig.getInstance().isNameable()) {
            Language.NO_PERM.sendMessage(p);
            return;
        }

        Pet pet = Pet.fromOwner(p.getUniqueId());
        if (pet == null) {
            Language.NO_ACTIVE_PET.sendMessage(p);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) sb.append(" ");
            sb.append(args[i]);
        }
        String name = sb.toString().replace("'", "").replace(";;", ";").replace(";;;", ";");
        name = Utils.hex(name);

        String blackListedWord = Utils.isInBlackList(name);
        if (blackListedWord != null) {
            Language.BLACKLISTED_WORD.sendMessageFormated(p, new FormatArg("%word%", blackListedWord));
            return;
        }

        if (!p.hasPermission(PPermission.COLOR.getPermission()))
            name = ChatColor.stripColor(name);

        if (name.isEmpty()) {
            Language.NICKNAME_NOT_CHANGED.sendMessage(p);
            return;
        }

        pet.setDisplayName(name, true);
        Language.NICKNAME_CHANGED_SUCCESSFULY.sendMessage(p);
    }

    @Override
    protected boolean additionalConditions() {
        return true;
    }

    @Override
    public boolean conditionsVerified() {
        // Accept 2 or more args (name with spaces)
        return args.length >= 2 && args[0].equalsIgnoreCase(argumentName);
    }
}
