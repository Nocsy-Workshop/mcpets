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

    public ArgumentSignalStick(final CommandSender sender, final String[] args) {
        super("signalStick", new int[]{3, 2}, sender, args, "/mcpets signalStick <petId> [player]");
    }

    @Override
    public boolean additionalConditions() {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        if (args.length == 2 && sender instanceof final Player p) {
            final String petId = args[1];
            final Pet pet = Pet.getFromId(petId);
            if (pet == null) {
                Language.PET_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            final ItemStack it = p.getInventory().getItemInMainHand();
            if (it == null || it.getType().isAir()) {
                Language.REQUIRES_ITEM_IN_HAND.sendMessage(p);
                return;
            }

            p.getInventory().setItemInMainHand(Items.turnIntoSignalStick(it, pet));
        }
        else if (args.length == 3) {
            final String playerName = args[1];
            final Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
                return;
            }

            final String petId = args[2];
            final Pet pet = Pet.getFromId(petId);
            if (pet == null) {
                Language.PET_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            player.getInventory().addItem(pet.getSignalStick());
        }
    }
}
