package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArgumentSignalStick extends AArgument {

    public ArgumentSignalStick(CommandSender sender, String[] args)
    {
        super("signalStick", new int[]{3, 2}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        if(args.length == 2 && sender instanceof Player)
        {
            String petId = args[1];
            Pet pet = Pet.getFromId(petId);
            if(pet == null)
            {
                Language.PET_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            Player p = ((Player)sender);
            ItemStack it = p.getInventory().getItemInMainHand();
            if(it == null ||
                    it.getType().isAir())
            {
                Language.REQUIRES_ITEM_IN_HAND.sendMessage(p);
                return;
            }

            ((Player)sender).getInventory().setItemInMainHand(Items.turnIntoSignalStick(it, pet));
            return;
        }

        else if(args.length == 3)
        {
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
                return;
            }

            String petId = args[2];
            Pet pet = Pet.getFromId(petId);
            if (pet == null) {
                Language.PET_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            player.getInventory().addItem(pet.getSignalStick());
        }

    }

}
