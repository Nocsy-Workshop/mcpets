package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class PetInteractionMenuListener implements Listener {

    @Getter
    private static final ArrayList<UUID> waitingForAnswer = new ArrayList<>();

    public static void changeName(Player p) {
        if (!waitingForAnswer.contains(p.getUniqueId()))
            waitingForAnswer.add(p.getUniqueId());
        Language.TYPE_NAME_IN_CHAT.sendMessage(p);
        Language.IF_WISH_TO_REMOVE_NAME.sendMessageFormated(p, new FormatArg("%tag%", Language.TAG_TO_REMOVE_NAME.getMessage()));
    }

    public static void mount(Player p, Pet pet) {
        if (p.isInsideVehicle()) {
            Language.ALREADY_INSIDE_VEHICULE.sendMessage(p);
        } else if (!pet.setMount(p)) {
            Language.NOT_MOUNTABLE.sendMessage(p);
        }
    }

    public static void inventory(Player p, Pet pet)
    {
        PetInventory inventory = PetInventory.get(pet);
        if(inventory != null)
        {
            inventory.open(p);
        }
    }

    public static void skins(Player p, Pet pet)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                PetSkin.openInventory(p, pet);
            }
        }.runTaskLater(MCPets.getInstance(), 2L);
    }

    public static void revoke(Player p, Pet pet) {
        pet.despawn(PetDespawnReason.REVOKE);
        Language.REVOKED.sendMessage(p);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase(PetInteractionMenu.getTitle())) {
            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            if (e.getClickedInventory() == null && GlobalConfig.getInstance().isActivateBackMenuIcon()) {
                openBackPetMenu(p);
                return;
            }

            if (e.getSlot() == 4) {
                Pet pet = Pet.getFromLastInteractedWith(p);
                if (pet == null || !pet.isStillHere()) {
                    p.closeInventory();
                    Language.REVOKED_BEFORE_CHANGES.sendMessage(p);
                    return;
                }
                revoke(p, pet);
                p.closeInventory();
                return;
            }

            ItemStack it = e.getCurrentItem();
            if (it != null && it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().hasLocalizedName()) {

                String localizedName = it.getItemMeta().getLocalizedName();
                if (localizedName.contains("AlmPetPage;"))
                    return;

                if(localizedName.equals(Items.PETMENU.getLocalizedName()))
                {
                    openBackPetMenu(p);
                    return;
                }

                Pet pet = Pet.getFromLastInteractedWith(p);
                if (pet == null) {
                    pet = Pet.getFromLastOpInteractedWith(p);
                    if(pet == null)
                    {
                        p.closeInventory();
                        return;
                    }
                }

                if (!pet.isStillHere()) {
                    Language.REVOKED_BEFORE_CHANGES.sendMessage(p);
                    p.closeInventory();
                    return;
                }

                if (localizedName.equals(Items.MOUNT.getLocalizedName())) {
                    mount(p, pet);
                } else if (localizedName.equals(Items.RENAME.getLocalizedName())) {
                    changeName(p);
                } else if (localizedName.equals(Items.INVENTORY.getLocalizedName())) {
                    inventory(p, pet);
                } else if (pet.getSignalStick() != null && it.isSimilar(pet.getSignalStick())) {
                    pet.giveStickSignals(p);
                } else if(localizedName.equals(Items.SKINS.getLocalizedName())) {
                    skins(p, pet);
                }
                p.closeInventory();
            }

        }

    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (waitingForAnswer.contains(p.getUniqueId())) {
            waitingForAnswer.remove(p.getUniqueId());
            e.setCancelled(true);

            String name = e.getMessage().replace("'", "");
            name = name.replace(";;", ";").replace(";;;", ";");
            name = Utils.hex(name);

            String blackListedWord = Utils.isInBlackList(name);
            if (blackListedWord != null) {
                Language.BLACKLISTED_WORD.sendMessageFormated(p, new FormatArg("%word%", blackListedWord));
                return;
            }

            Pet pet = Pet.getFromLastInteractedWith(p);
            if(pet == null)
                pet = Pet.getFromLastOpInteractedWith(p);

            if (pet != null && pet.isStillHere()) {
                if (!p.hasPermission(PPermission.COLOR.getPermission()))
                    name = ChatColor.stripColor(name);

                if(name == null || name.isEmpty())
                {
                    Language.NICKNAME_NOT_CHANGED.sendMessage(p);
                    return;
                }
                pet.setDisplayName(name, true);

                Language.NICKNAME_CHANGED_SUCCESSFULY.sendMessage(p);
            } else {
                Language.REVOKED_BEFORE_CHANGES.sendMessage(p);
            }

        }
    }

    private void openBackPetMenu(Player p) {
        PetMenu menu = new PetMenu(p, 0, false);
        menu.open(p);
    }

}
