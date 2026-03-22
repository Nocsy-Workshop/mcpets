package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Argument to filter the categories menu by only showing mounts
 */
public class ArgumentMounts extends AArgument {

    public ArgumentMounts(CommandSender sender, String[] args) {
        super("mounts", new int[]{1}, sender, args);
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player && sender.hasPermission(PPermission.USE.getPermission());
    }

    @Override
    public void commandEffect() {
        if (sender instanceof Player) {
            CategoriesMenu.openFiltered((Player) sender, CategoryType.MOUNT);
        }
    }
}
