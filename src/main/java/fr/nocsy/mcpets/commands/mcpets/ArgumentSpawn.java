package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentSpawn extends AArgument {

    public ArgumentSpawn(CommandSender sender, String[] args)
    {
        super("spawn", new int[]{4, 5}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        String petId = args[1];
        String playerName = args[2];
        String booleanValue = args[3];
        boolean silent = args.length == 5 && args[4].equals("-s");

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
            return;
        }

        Pet petObject = Pet.getFromId(petId);
        if (petObject == null) {
            Language.PET_DOESNT_EXIST.sendMessage(sender);
            return;
        }
        Pet pet = petObject.copy();

        boolean checkPermission = booleanValue.equalsIgnoreCase("true");
        if (checkPermission && !target.hasPermission(pet.getPermission())) {
            Language.NOT_ALLOWED.sendMessage(target);
            return;
        }
        pet.setCheckPermission(checkPermission);
        if (silent)
            pet.spawn(target, target.getLocation());
        else
            pet.spawnWithMessage(target, target.getLocation());
    }
}
