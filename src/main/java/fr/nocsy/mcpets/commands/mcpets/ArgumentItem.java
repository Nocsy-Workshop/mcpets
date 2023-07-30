package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArgumentItem extends AArgument {

    public ArgumentItem(CommandSender sender, String[] args)
    {
        super("item", new int[]{3, 2}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender instanceof Player && sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        if(args.length == 2)
        {
            Player p = ((Player)sender);

            if(args[1].equalsIgnoreCase("list"))
            {
                Language.KEY_LIST.sendMessage(p);
                for(String keys : ItemsListConfig.getInstance().listKeys())
                {
                    p.sendMessage("§8- §7" + keys);
                }
                return;
            }

            ItemStack item = p.getInventory().getItemInMainHand();
            if(item == null ||
                    item.getType().isAir())
            {
                Language.REQUIRES_ITEM_IN_HAND.sendMessage(p);
                return;
            }

            String key = args[1];
            if(ItemsListConfig.getInstance().getItemStack(key) != null)
            {
                ItemsListConfig.getInstance().setItemStack(key, item);
                Language.ITEM_UPDATED.sendMessageFormated(p, new FormatArg("%key%", key));
                return;
            }
            else
            {
                Language.ITEM_DOESNT_EXIST.sendMessageFormated(p, new FormatArg("%key%", key));
                return;
            }
        }

        else if(args.length == 3)
        {
            String action = args[1];
            if (action.equalsIgnoreCase("add") ||
                    action.equalsIgnoreCase("remove") ||
                    action.equalsIgnoreCase("give")) {
                String key = args[2];

                Player p = ((Player) sender);

                if (action.equalsIgnoreCase("remove")) {
                    if (ItemsListConfig.getInstance().getItemStack(key) == null) {
                        Language.KEY_DOESNT_EXIST.sendMessage(p);
                        return;
                    }
                    ItemsListConfig.getInstance().removeItemStack(key);
                    Language.KEY_REMOVED.sendMessage(p);
                    return;
                } else if (action.equalsIgnoreCase("add")) {
                    if (ItemsListConfig.getInstance().getItemStack(key) != null) {
                        Language.KEY_ALREADY_EXISTS.sendMessage(p);
                        return;
                    }

                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item == null ||
                            item.getType().isAir() ||
                            item.getType() == Material.AIR ||
                            item.getType() == Material.CAVE_AIR ||
                            item.getType() == Material.VOID_AIR) {
                        Language.REQUIRES_ITEM_IN_HAND.sendMessage(p);
                        return;
                    }

                    ItemsListConfig.getInstance().setItemStack(key, item);
                    Language.KEY_ADDED.sendMessage(p);
                    return;
                } else if (action.equalsIgnoreCase("give")) {
                    if (ItemsListConfig.getInstance().getItemStack(key) == null) {
                        Language.KEY_DOESNT_EXIST.sendMessage(p);
                        return;
                    }

                    p.getInventory().addItem(ItemsListConfig.getInstance().getItemStack(key));
                    return;
                }

            }

        }

    }

}
