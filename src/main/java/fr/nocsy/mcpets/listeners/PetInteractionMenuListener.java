package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.*;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInteractionMenu;
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

import java.util.ArrayList;
import java.util.UUID;

public class PetInteractionMenuListener implements Listener {

    @EventHandler
    public void click(InventoryClickEvent e)
    {
        if(e.getView().getTitle().equalsIgnoreCase(PetInteractionMenu.getTitle()))
        {
            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            if(e.getClickedInventory() == null && GlobalConfig.getInstance().isActivateBackMenuIcon())
            {
                openBackPetMenu(p);
                return;
            }

            ItemStack it = e.getCurrentItem();
            if(it != null && it.hasItemMeta() && it.getItemMeta().hasDisplayName())
            {

                if(it.getItemMeta().hasLocalizedName() && it.getItemMeta().getLocalizedName().equals(Items.PETMENU.getItem().getItemMeta().getLocalizedName()))
                {
                    openBackPetMenu(p);
                    return;
                }

                Pet pet = Pet.getFromLastInteractedWith(p);

                if(pet == null)
                {
                    p.closeInventory();
                    return;
                }

                if(!pet.isStillHere())
                {
                    Language.REVOKED_BEFORE_CHANGES.sendMessage(p);
                    p.closeInventory();
                    return;
                }

                if(e.getSlot() == 2)
                {
                    revoke(p, pet);
                }
                else if(it.isSimilar(Items.MOUNT.getItem()))
                {
                    mount(p, pet);
                }
                else if(it.isSimilar(Items.RENAME.getItem()))
                {
                    changeName(p);
                }
                else if(it.isSimilar(pet.getSignalStick()))
                {
                    pet.giveStickSignals(p);
                }
                p.closeInventory();
            }

        }

    }

    @Getter
    private static ArrayList<UUID> waitingForAnswer = new ArrayList<>();

    @EventHandler
    public void chat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();

        if(waitingForAnswer.contains(p.getUniqueId()))
        {
            waitingForAnswer.remove(p.getUniqueId());
            e.setCancelled(true);

            String name = e.getMessage();
            name = name.replace(";;", ";").replace(";;;", ";");
            name = Utils.hex(name);

            String blackListedWord = Utils.isInBlackList(name);
            if(blackListedWord != null)
            {
                Language.BLACKLISTED_WORD.sendMessageFormated(p, new FormatArg("%word%", blackListedWord));
                return;
            }

            Pet pet = Pet.getFromLastInteractedWith(p);

            if(pet != null && pet.isStillHere())
            {
                if(!p.hasPermission(PPermission.COLOR.getPermission()))
                    name = ChatColor.stripColor(name);
                
                pet.setDisplayName(name, true);

                Language.NICKNAME_CHANGED_SUCCESSFULY.sendMessage(p);
            }
            else
            {
                Language.REVOKED_BEFORE_CHANGES.sendMessage(p);
            }

        }
    }

    private void openBackPetMenu(Player p)
    {
        UUID uuid = p.getUniqueId();

        PetMenu menu = new PetMenu(p, 0, false);
        menu.open(p);
    }

    public static void changeName(Player p)
    {
        if(!waitingForAnswer.contains(p.getUniqueId()))
            waitingForAnswer.add(p.getUniqueId());
        Language.TYPE_NAME_IN_CHAT.sendMessage(p);
        Language.IF_WISH_TO_REMOVE_NAME.sendMessageFormated(p, new FormatArg("%tag%", Language.TAG_TO_REMOVE_NAME.getMessage()));
    }

    public static void mount(Player p, Pet pet)
    {
        if(p.isInsideVehicle())
        {
            Language.ALREADY_INSIDE_VEHICULE.sendMessage(p);
        }
        else if(!pet.setMount(p))
        {
            Language.NOT_MOUNTABLE.sendMessage(p);
        }
    }

    public static void revoke(Player p, Pet pet)
    {
        pet.despawn(PetDespawnReason.REVOKE);
        Language.REVOKED.sendMessage(p);
    }

}
