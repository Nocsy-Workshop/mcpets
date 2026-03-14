package fr.nocsy.mcpets.commands.mounts;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.CCommand;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Command to open the categories menu filtered by mounts
 */
public class MountsCommand implements CCommand {

    @Override
    public String getName() {
        return "mcmounts";
    }

    @Override
    public String getPermission() {
        return PPermission.USE.getPermission();
    }

    @Override
    public TabCompleter getCompleter() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(getPermission())) {
            if (sender instanceof Player) {
                CategoriesMenu.openFiltered((Player) sender, CategoryType.MOUNT);
            }
        }
        else {
            Language.NO_PERM.sendMessage(sender);
        }
    }
}
