package fr.nocsy.mcpets.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CCommand {

    public abstract String getName();

    public abstract String getPermission();

    public abstract void execute(CommandSender sender, Command command, String label, String[] args);

}
