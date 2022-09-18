package fr.nocsy.mcpets.commands;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.tabcompleters.MCPetsCommandTabCompleter;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.listeners.PetInteractionMenuListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MCPetsCommand implements CCommand {

    @Override
    public String getName() {
        return "mcpets";
    }

    @Override
    public String getPermission() {
        return PPermission.USE.getPermission();
    }

    @Override
    public TabCompleter getCompleter() {
        return new MCPetsCommandTabCompleter();
    }

    public String getAdminPermission() {
        return PPermission.ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(getPermission())) {
            if (args.length == 4 || args.length == 5)
            {
                if (args[0].equalsIgnoreCase("spawn")
                        && sender.hasPermission(getAdminPermission()))
                {

                    String petId = args[1];
                    String playerName = args[2];
                    String booleanValue = args[3];
                    boolean silent = false;
                    if (args.length == 5 && args[4].equals("-s"))
                    {
                        silent = true;
                    }

                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null)
                    {
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
                    if (checkPermission && !target.hasPermission(pet.getPermission()))
                    {
                        Language.NOT_ALLOWED.sendMessage(target);
                        return;
                    }
                    pet.setCheckPermission(checkPermission);
                    if (silent)
                        pet.spawn(target, target.getLocation());
                    else
                        pet.spawnWithMessage(target, target.getLocation());
                    return;
                }
            }
            else if (args.length == 3)
            {
                if(args[0].equalsIgnoreCase("signalstick")
                        && sender.hasPermission(getAdminPermission()))
                {
                    String playerName = args[1];
                    Player player = Bukkit.getPlayer(playerName);
                    if (player == null) {
                        Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
                        return;
                    }

                    String petId = args[2];
                    Pet pet = Pet.getFromId(petId);
                    if(pet == null)
                    {
                        Language.PET_DOESNT_EXIST.sendMessage(sender);
                        return;
                    }

                    player.getInventory().addItem(pet.getSignalStick());
                    return;
                }
                if(args[0].equalsIgnoreCase("item")
                        && sender.hasPermission(getAdminPermission())
                        && sender instanceof Player)
                {
                    String action = args[1];
                    if(action.equalsIgnoreCase("add") ||
                            action.equalsIgnoreCase("remove") ||
                            action.equalsIgnoreCase("give"))
                    {
                        String key = args[2];

                        Player p = ((Player)sender);

                        if(action.equalsIgnoreCase("remove"))
                        {
                            if(ItemsListConfig.getInstance().getItemStack(key) == null)
                            {
                                Language.KEY_DOESNT_EXIST.sendMessage(p);
                                return;
                            }
                            ItemsListConfig.getInstance().removeItemStack(key);
                            Language.KEY_REMOVED.sendMessage(p);
                            return;
                        }
                        else if(action.equalsIgnoreCase("add"))
                        {
                            if(ItemsListConfig.getInstance().getItemStack(key) != null)
                            {
                                Language.KEY_ALREADY_EXISTS.sendMessage(p);
                                return;
                            }

                            ItemStack item = p.getInventory().getItemInMainHand();
                            if(item == null ||
                                    item.getType().isAir())
                            {
                                Language.REQUIRES_ITEM_IN_HAND.sendMessage(p);
                                return;
                            }

                            ItemsListConfig.getInstance().setItemStack(key, item);
                            Language.KEY_ADDED.sendMessage(p);
                            return;
                        }
                        else if(action.equalsIgnoreCase("give"))
                        {
                            if(ItemsListConfig.getInstance().getItemStack(key) == null)
                            {
                                Language.KEY_DOESNT_EXIST.sendMessage(p);
                                return;
                            }

                            p.getInventory().addItem(ItemsListConfig.getInstance().getItemStack(key));

                        }

                    }
                }
                if(args[0].equalsIgnoreCase("clearStats")
                        && sender.hasPermission(getAdminPermission()))
                {
                    String petId = args[2];
                    Pet pet = Pet.getFromId(petId);
                    if(pet != null)
                    {
                        String playerName = args[1];
                        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
                        if(player != null || !player.hasPlayedBefore())
                        {
                            // Start by loading the player data
                            PlayerData pd = PlayerData.get(player.getUniqueId());

                            PetStats.remove(petId, player.getUniqueId());
                            Language.STATS_CLEARED_FOR_PET_FOR_PLAYER.sendMessageFormated(sender, new FormatArg("%petId%", petId),
                                                                                                new FormatArg("%player%", playerName));

                            pd.save();
                            return;
                        }
                    }

                    sender.sendMessage(Language.PLAYER_OR_PET_DOESNT_EXIST.getMessage());
                    return;
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("open")
                        && sender.hasPermission(getAdminPermission())
                        && sender instanceof Player) {
                    String playerName = args[1];
                    Player playerToOpen = Bukkit.getPlayer(playerName);
                    if (playerToOpen == null) {
                        Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
                        return;
                    }

                    PetMenu menu = new PetMenu(playerToOpen, 0, false);
                    menu.open((Player) sender);
                    return;
                }
                if(args[0].equalsIgnoreCase("item")
                        && sender.hasPermission(getAdminPermission())
                        && sender instanceof Player)
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
                if(args[0].equalsIgnoreCase("signalstick")
                        && sender.hasPermission(getAdminPermission())
                        && sender instanceof Player)
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

                if(args[0].equalsIgnoreCase("clearStats")
                        && sender.hasPermission(getAdminPermission()))
                {
                    // Either it's a player clear
                    String playerName = args[1];
                    OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
                    if(player != null || !player.hasPlayedBefore())
                    {
                        PlayerData pd = PlayerData.get(player.getUniqueId());

                        PetStats.remove(player.getUniqueId());
                        Language.STATS_CLEARED.sendMessage(sender);
                        pd.save();
                        return;
                    }
                    else
                    {
                        // Or it's a pet clear
                        String petId = args[1];
                        Pet pet = Pet.getFromId(petId);
                        if(pet != null)
                        {
                            PetStats.remove(petId);
                            Language.STATS_CLEARED_FOR_PET.sendMessageFormated(sender, new FormatArg("%petId%", petId));
                            PlayerData.saveDB();
                            return;
                        }

                        // In that case it's not a pet clear so he probably failed to give a player name
                        sender.sendMessage(Language.PLAYER_OR_PET_DOESNT_EXIST.getMessage());
                        return;
                    }
                }
                if(args[0].equalsIgnoreCase("petFood")
                        && sender instanceof Player
                        && sender.hasPermission(getAdminPermission()))
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
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")
                        && sender.hasPermission(getAdminPermission())) {
                    PlayerData.saveDB();
                    MCPets.loadConfigs();
                    Language.RELOAD_SUCCESS.sendMessage(sender);
                    Language.HOW_MANY_PETS_LOADED.sendMessageFormated(sender, new FormatArg("%numberofpets%", Integer.toString(Pet.getObjectPets().size())));
                    return;
                }
                if (sender instanceof Player
                        && (args[0].equalsIgnoreCase("name")
                        || args[0].equalsIgnoreCase("mount"))
                        || args[0].equalsIgnoreCase("revoke")) {
                    Player p = (Player) sender;
                    Pet pet = Pet.fromOwner(p.getUniqueId());

                    if (pet == null) {
                        Language.NO_ACTIVE_PET.sendMessage(p);
                        return;
                    }

                    switch (args[0]) {
                        case "name":
                            PetInteractionMenuListener.changeName(p);
                            return;
                        case "mount":
                            PetInteractionMenuListener.mount(p, pet);
                            return;
                        case "inventory":
                            PetInteractionMenuListener.inventory(p, pet);
                            return;
                        case "revoke":
                            PetInteractionMenuListener.revoke(p, pet);
                            return;
                    }
                }
            } else if (args.length == 0
                    && sender instanceof Player) {
                PetMenu menu = new PetMenu((Player) sender, 0, false);
                menu.open((Player) sender);
                return;
            }

            if (sender.hasPermission(getAdminPermission()))
                Language.USAGE.sendMessage(sender);
        } else {
            Language.NO_PERM.sendMessage(sender);
        }
    }
}
