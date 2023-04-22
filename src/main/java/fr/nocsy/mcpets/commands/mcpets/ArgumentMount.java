package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.listeners.PetInteractionMenuListener;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentMount extends AArgument {

    public ArgumentMount(CommandSender sender, String[] args)
    {
        super("mount", new int[]{1}, sender, args);
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

        PetInteractionMenuListener.mount(p, pet);

    }

}
