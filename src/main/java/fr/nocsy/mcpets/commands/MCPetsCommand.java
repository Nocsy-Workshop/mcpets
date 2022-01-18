package fr.nocsy.mcpets.commands;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.*;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import fr.nocsy.mcpets.listeners.PetInteractionMenuListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MCPetsCommand implements CCommand {
    @Override
    public String getName() {
        return "mcpets";
    }

    @Override
    public String getPermission() {
        return PPermission.USE.getPermission();
    }

    public String getAdminPermission() {
        return PPermission.ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(getPermission()))
        {
            if(args.length == 4)
            {
                if(args[0].equalsIgnoreCase("spawn")
                        && sender.hasPermission(getAdminPermission()))
                {

                    String petId = args[1];
                    String playerName = args[2];
                    String booleanValue = args[3];

                    Player target = Bukkit.getPlayer(playerName);
                    if(target == null)
                    {
                        Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
                        return;
                    }

                    Pet petObject = Pet.getFromId(petId);
                    if(petObject == null)
                    {
                        Language.PET_DOESNT_EXIST.sendMessage(sender);
                        return;
                    }
                    Pet pet = petObject.copy();

                    boolean checkPermission = booleanValue.equalsIgnoreCase("true");
                    if(checkPermission && !target.hasPermission(pet.getPermission()))
                    {
                        Language.NOT_ALLOWED.sendMessage(target);
                        return;
                    }
                    pet.setCheckPermission(checkPermission);
                    pet.spawnWithMessage(target, target.getLocation());
                    return;
                }
            }
            else if(args.length == 2)
            {
                if(args[0].equalsIgnoreCase("open")
                        && sender.hasPermission(getAdminPermission())
                        && sender instanceof Player)
                {
                    String playerName = args[1];
                    Player playerToOpen = Bukkit.getPlayer(playerName);
                    if(playerToOpen == null)
                    {
                        Language.PLAYER_NOT_CONNECTED.sendMessageFormated(sender, new FormatArg("%player%", playerName));
                        return;
                    }

                    PetMenu menu = new PetMenu(playerToOpen, 0, false);
                    menu.open((Player)sender);
                    return;
                }
            }
            else if(args.length == 1)
            {
                if(args[0].equalsIgnoreCase("reload")
                        && sender.hasPermission(getAdminPermission()) )
                {
                    MCPets.loadConfigs();
                    Language.RELOAD_SUCCESS.sendMessage(sender);
                    Language.HOW_MANY_PETS_LOADED.sendMessageFormated(sender, new FormatArg("%numberofpets%", Integer.toString(Pet.getObjectPets().size())));
                    return;
                }
                if(sender instanceof Player
                        && (args[0].equalsIgnoreCase("name")
                            || args[0].equalsIgnoreCase("mount"))
                            || args[0].equalsIgnoreCase("revoke"))
                {
                    Player p = (Player)sender;
                    Pet pet = Pet.fromOwner(p.getUniqueId());

                    if(pet == null)
                    {
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
                        case "revoke":
                            PetInteractionMenuListener.revoke(p, pet);
                            return;
                    }
                }
            }
            else if(args.length == 0
                    && sender instanceof Player)
            {
                PetMenu menu = new PetMenu((Player)sender, 0, false);
                menu.open((Player)sender);
                return;
            }

            if(sender.hasPermission(getAdminPermission()))
                Language.USAGE.sendMessage(sender);
        }
        else
        {
            Language.NO_PERM.sendMessage(sender);
        }
    }
}
