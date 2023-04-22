package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentPetFood extends AArgument {

    public ArgumentPetFood(CommandSender sender, String[] args)
    {
        super("petFood", new int[]{3, 2}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        if(args.length == 2)
        {
            String id = args[1];
            PetFood petFood = PetFood.getFromId(id);
            if(petFood == null)
            {
                Language.PETFOOD_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            Player p = ((Player)sender);
            p.getInventory().addItem(petFood.getItemStack());
            return;
        }

        else if(args.length == 3)
        {
            String petFoodId = args[1];
            PetFood petFood = PetFood.getFromId(petFoodId);
            if(petFood == null)
            {
                Language.PETFOOD_DOESNT_EXIST.sendMessage(sender);
                return;
            }

            String playerName = args[2];
            Player p = Bukkit.getPlayer(playerName);
            if(p == null)
            {
                Language.PLAYER_NOT_CONNECTED.sendMessage(sender);
                return;
            }

            p.getInventory().addItem(petFood.getItemStack());
            return;
        }

    }

}
