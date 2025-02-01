package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentInventory extends AArgument {

    public ArgumentInventory(CommandSender sender, String[] args) {
        super("inventory", new int[]{3}, sender, args);
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player && sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        String playerName = args[1];
        String petId = args[2];
        Pet pet = Pet.getFromId(petId);

        if (pet == null) {
            Language.PET_DOESNT_EXIST.sendMessage(sender);
            return;
        }

        OfflinePlayer playerToOpen = Bukkit.getOfflinePlayer(playerName);

        if (!playerToOpen.hasPlayedBefore()) {
            Language.PLAYER_OR_PET_DOESNT_EXIST.sendMessage(sender);
            return;
        }

        PetInventory inventory = PetInventory.get(playerToOpen.getUniqueId(), petId);

        if (inventory == null) {
            Language.PET_INVENTORY_COULDNOT_OPEN.sendMessage(sender);
            return;
        }

        inventory.open((Player)sender);
    }
}
