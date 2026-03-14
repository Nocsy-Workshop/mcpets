package fr.nocsy.mcpets.commands.mounts;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.CCommand;
import fr.nocsy.mcpets.commands.mcpets.ArgumentMenu;
import fr.nocsy.mcpets.commands.tabcompleters.MountsCommandTabCompleter;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to open the categories menu filtered by mounts
 */
public class MountsCommand implements CCommand {

    @Override
    public String getName() {
        return "mcmounts";
    }

    @Override
    public String getPermission() {
        return PPermission.USE.getPermission();
    }

    @Override
    public TabCompleter getCompleter() {
        return new MountsCommandTabCompleter();
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            Language.NO_PERM.sendMessage(sender);
            return;
        }

        if (!(sender instanceof Player))
            return;

        Player p = (Player) sender;

        // /mounts menu [mountId]
        if (args.length >= 1 && args[0].equalsIgnoreCase("menu")) {
            List<Pet> activeMounts = Pet.getActivePetsForOwner(p.getUniqueId())
                    .stream()
                    .filter(Pet::isMountable)
                    .collect(Collectors.toList());

            if (activeMounts.isEmpty()) {
                Language.NO_ACTIVE_PET.sendMessage(p);
                return;
            }

            Pet mount;
            if (args.length >= 2) {
                String mountId = args[1];
                mount = activeMounts.stream()
                        .filter(m -> m.getId().equalsIgnoreCase(mountId))
                        .findFirst()
                        .orElse(null);
                if (mount == null) {
                    Language.NO_ACTIVE_PET.sendMessage(p);
                    return;
                }
            } else if (activeMounts.size() == 1) {
                mount = activeMounts.get(0);
            } else {
                String mountIds = activeMounts.stream().map(Pet::getId).collect(Collectors.joining(", "));
                Language.SPECIFY_PET.sendMessageFormated(p, new FormatArg("%pets%", mountIds));
                return;
            }

            ArgumentMenu.openCommandMenu(p, mount, true);
            return;
        }

        // Default: open categories menu
        CategoriesMenu.openFiltered(p, CategoryType.MOUNT);
    }
}
