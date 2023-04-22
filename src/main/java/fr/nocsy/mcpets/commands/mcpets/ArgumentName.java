package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.listeners.PetInteractionMenuListener;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentName extends AArgument {

    public ArgumentName(CommandSender sender, String[] args)
    {
        super("name", new int[]{1}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender instanceof Player;
    }

    @Override
    public void commandEffect() {
        Player p = (Player) sender;
        Pet pet = Pet.fromOwner(p.getUniqueId());

        if (pet == null) {
            Language.NO_ACTIVE_PET.sendMessage(p);
            return;
        }

        PetInteractionMenuListener.changeName(p);

    }


}
