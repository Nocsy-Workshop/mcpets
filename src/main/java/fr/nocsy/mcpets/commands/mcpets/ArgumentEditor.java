package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.editor.Editor;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArgumentEditor extends AArgument {

    public ArgumentEditor(CommandSender sender, String[] args)
    {
        super("editor", new int[]{1}, sender, args);
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
        Editor editor = Editor.getEditor(p);
        editor.openEditor();
    }

}
