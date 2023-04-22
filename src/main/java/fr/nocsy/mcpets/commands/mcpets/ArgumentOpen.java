package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentOpen extends AArgument {

    public ArgumentOpen(CommandSender sender, String[] args)
    {
        super("open", new int[]{2}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender instanceof Player &&
                sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        String playerName = args[1];
        Player playerToOpen = Bukkit.getPlayer(playerName);
        if (playerToOpen == null) {
            Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
            return;
        }

        PetMenu menu = new PetMenu(playerToOpen, 0, false);
        menu.open((Player) sender);
        return;
    }

}
