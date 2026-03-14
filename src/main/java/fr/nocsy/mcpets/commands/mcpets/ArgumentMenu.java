package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public class ArgumentMenu extends AArgument {

    public ArgumentMenu(CommandSender sender, String[] args) {
        super("menu", new int[]{1, 2}, sender, args);
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player;
    }

    @Override
    public void commandEffect() {
        Player p = (Player) sender;
        List<Pet> activePets = Pet.getActivePetsForOwner(p.getUniqueId())
                .stream()
                .filter(pet -> !pet.isMountable())
                .collect(Collectors.toList());

        if (activePets.isEmpty()) {
            Language.NO_ACTIVE_PET.sendMessage(p);
            return;
        }

        Pet pet;
        if (args.length == 2) {
            String petId = args[1];
            pet = activePets.stream()
                    .filter(ap -> ap.getId().equalsIgnoreCase(petId))
                    .findFirst()
                    .orElse(null);
            if (pet == null) {
                Language.NO_ACTIVE_PET.sendMessage(p);
                return;
            }
        } else if (activePets.size() == 1) {
            pet = activePets.get(0);
        } else {
            String petIds = activePets.stream().map(Pet::getId).collect(Collectors.joining(", "));
            Language.SPECIFY_PET.sendMessageFormated(p, new FormatArg("%pets%", petIds));
            return;
        }

        openCommandMenu(p, pet, false);
    }

    /**
     * Open a simplified interaction menu from command (back, skins, rename, revoke only)
     */
    public static void openCommandMenu(Player p, Pet pet, boolean isMount) {
        if (pet.getTamingProgress() < 1)
            return;

        pet.setOwner(p.getUniqueId());
        String title = isMount
                ? Language.INVENTORY_MOUNTS_MENU_INTERACTIONS.getMessage()
                : Language.INVENTORY_PETS_MENU_INTERACTIONS.getMessage();
        Inventory inventory = Bukkit.createInventory(null, 9, title);

        inventory.setItem(0, isMount ? Items.MOUNTMENU.getItem() : Items.PETMENU.getItem());
        if (pet.hasSkins())
            inventory.setItem(2, Items.SKINS.getItem());
        if (GlobalConfig.getInstance().isNameable())
            inventory.setItem(3, Items.RENAME.getItem());
        inventory.setItem(4, pet.buildItem(Items.petInfo(pet), true, null, null, null, null, 0, null));

        p.openInventory(inventory);
    }
}
