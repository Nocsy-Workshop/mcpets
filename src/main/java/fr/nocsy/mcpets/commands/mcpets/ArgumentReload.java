package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentReload extends AArgument {

    public ArgumentReload(CommandSender sender, String[] args)
    {
        super("reload", new int[]{1}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        PlayerData.saveDB();
        MCPets.loadConfigs();
        Language.RELOAD_SUCCESS.sendMessage(sender);
        Language.HOW_MANY_PETS_LOADED.sendMessageFormated(sender, new FormatArg("%numberofpets%", Integer.toString(Pet.getObjectPets().size())));
        return;
    }

}
