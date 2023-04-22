package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentCategory extends AArgument {

    public ArgumentCategory(CommandSender sender, String[] args)
    {
        super("category", new int[]{3, 2}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        if(args.length == 2 && sender instanceof Player)
        {
            String categoryId = args[1];

            Category category = Category.getFromId(categoryId);
            if(category == null)
            {
                Language.CATEGORY_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            category.openInventory((Player)sender, 0);
        }
        else if(args.length == 3)
        {
            String categoryId = args[1];

            Category category = Category.getFromId(categoryId);
            if (category == null) {
                Language.CATEGORY_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            String playerName = args[2];
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
                return;
            }

            category.openInventory(player, 0);
            return;
        }

    }

}
