package fr.nocsy.mcpets.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public abstract class AArgument {

    protected CommandSender sender;
    @Getter
    protected String args[];
    @Getter
    protected String argumentName;
    @Getter
    protected int[] validArgsLength;

    public abstract void commandEffect();
    protected abstract boolean additionalConditions();
    public boolean conditionsVerified()
    {
        return this.args != null &&
                this.args.length > 0 &&
                Arrays.stream(this.validArgsLength).anyMatch(validLength -> this.args.length == validLength) &&
                this.args[0].equalsIgnoreCase(this.argumentName)
                && additionalConditions();
    }

    public AArgument(String argumentName, int[] validArgsLength, CommandSender sender, String[] args)
    {
        this.sender = sender;
        this.args = args;
        this.validArgsLength = validArgsLength;
        this.argumentName = argumentName;
    }

}
