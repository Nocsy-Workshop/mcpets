package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public class ArgumentMenu extends AArgument {

    private static final int INV_SIZE = 9;

    public ArgumentMenu(final CommandSender sender, final String[] args) {
        super("menu", new int[]{1, 2}, sender, args);
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player;
    }

    @Override
    public void commandEffect() {
        final Player p = (Player) sender;
        final List<Pet> activePets = Pet.getActivePetsForOwner(p.getUniqueId())
                .stream()
                .filter(pet -> !pet.isMountable())
                .toList();

        if (activePets.isEmpty()) {
            Language.NO_ACTIVE_PET.sendMessage(p);
            return;
        }

        final Pet pet;
        if (args.length == 2) {
            final String petId = args[1];
            pet = activePets.stream()
                    .filter(ap -> ap.getId().equalsIgnoreCase(petId))
                    .findFirst()
                    .orElse(null);
            if (pet == null) {
                Language.NO_ACTIVE_PET.sendMessage(p);
                return;
            }
        } else if (activePets.size() == 1) {
            pet = activePets.getFirst();
        } else {
            final String petIds = activePets.stream().map(Pet::getId).collect(Collectors.joining(", "));
            Language.SPECIFY_PET.sendMessageFormated(p, new FormatArg("%pets%", petIds));
            return;
        }

        openCommandMenu(p, pet, false);
    }

    /**
     * Open a simplified interaction menu from command (back, skins, rename, revoke only)
     */
    public static void openCommandMenu(final Player p, final Pet pet, final boolean isMount) {
        if (pet.getTamingProgress() < 1)
            return;

        pet.setOwner(p.getUniqueId());
        final String title = isMount
                ? Language.INVENTORY_MOUNTS_MENU_INTERACTIONS.getMessage()
                : Language.INVENTORY_PETS_MENU_INTERACTIONS.getMessage();
        final PetInventoryHolder.Type type = isMount
                ? PetInventoryHolder.Type.MOUNT_INTERACTION_MENU
                : PetInventoryHolder.Type.PET_INTERACTION_MENU;
        final Inventory inventory = new PetInventoryHolder(INV_SIZE, title, type).getInventory();

        inventory.setItem(0, isMount ? Items.MOUNTMENU.getItem() : Items.PETMENU.getItem());
        if (pet.hasSkins())
            inventory.setItem(2, Items.SKINS.getItem());
        if (GlobalConfig.getInstance().isNameable())
            inventory.setItem(3, Items.RENAME.getItem());
        inventory.setItem(4, pet.buildItem(Items.petInfo(pet), true, null, null, null, null, 0, null));

        p.openInventory(inventory);
    }
}
