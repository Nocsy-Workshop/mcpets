package fr.nocsy.mcpets.commands.mcpets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetFood;

public class ArgumentPetFood extends AArgument {

    public ArgumentPetFood(CommandSender sender, String[] args) {
        super("petFood", new int[]{2, 3, 4}, sender, args, "/mcpets petFood <id> [player] [amount]");
    }

    @Override
    public boolean additionalConditions() {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        String petFoodId = args[1];
        PetFood petFood = PetFood.getFromId(petFoodId);

        if (petFood == null) {
            Language.PETFOOD_DOESNT_EXIST.sendMessage(sender);
            return;
        }

        Player target;
        int amount = 1;

        switch (args.length) {
            case 2 -> {
                if (!(sender instanceof Player player)) return;
                target = player;
            }

            case 3 -> {
                try {
                    amount = Integer.parseInt(args[2]);

                    if (!(sender instanceof Player player)) return;
                    target = player;

                } catch (NumberFormatException ignored) {
                    target = Bukkit.getPlayer(args[2]);

                    if (target == null) {
                        Language.PLAYER_NOT_CONNECTED.sendMessage(sender);
                        return;
                    }
                }
            }

            case 4 -> {
                target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    Language.PLAYER_NOT_CONNECTED.sendMessage(sender);
                    return;
                }

                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§cInvalid amount.");
                    return;
                }
            }

            default -> {
                return;
            }
        }

        if (amount < 1) {
            sender.sendMessage("§cAmount must be greater than 0.");
            return;
        }

        ItemStack item = petFood.getItemStack().clone();
        item.setAmount(Math.min(amount, item.getMaxStackSize()));

        while (amount > 0) {
            ItemStack stack = item.clone();
            stack.setAmount(Math.min(amount, stack.getMaxStackSize()));
            target.getInventory().addItem(stack);
            amount -= stack.getAmount();
        }
    }

}
