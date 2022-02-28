package fr.nocsy.mcpets.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CCommand {

    String getName();

    String getPermission();

    void execute(CommandSender sender, Command command, String label, String[] args);

}
