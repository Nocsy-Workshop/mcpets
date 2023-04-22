package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentDebug extends AArgument {

    public ArgumentDebug(CommandSender sender, String[] args)
    {
        super("debug", new int[]{1}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender instanceof Player &&
                sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        Player p = (Player) sender;
        if(Debugger.isListening((p.getUniqueId())))
        {
            Debugger.leave(p.getUniqueId());
            Language.DEBUGGER_LEAVE.sendMessage(p);
        }
        else
        {
            Debugger.join(p.getUniqueId());
            Language.DEBUGGER_JOINING.sendMessage(p);
        }
        return;
    }

}
