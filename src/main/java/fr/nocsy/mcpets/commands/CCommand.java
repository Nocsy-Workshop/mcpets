package fr.nocsy.mcpets.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public interface CCommand {

    String getName();

    String getPermission();

    TabCompleter getCompleter();

    void execute(CommandSender sender, Command command, String label, String[] args);

}
