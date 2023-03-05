package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PlayerSignal;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SignalStickListener implements Listener {

    @EventHandler
    public void switchSignal(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR ||
                e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            ItemStack stick = p.getInventory().getItemInMainHand();

            if (!Items.isSignalStick(stick)) {
                stick = p.getInventory().getItemInOffHand();
                if (!Items.isSignalStick(stick))
                    return;
            }

            UUID owner = p.getUniqueId();
            if (Pet.fromOwner(owner) == null)
                return;

            String nextSignal = PlayerSignal.getNextSignal(owner);

            if (nextSignal == null)
                return;

            PlayerSignal.setSignal(owner, nextSignal);
            Utils.sendActionBar(p, Utils.hex(Language.SIGNAL_STICK_SIGNAL.getMessageFormatted(new FormatArg("%signal%", nextSignal.toLowerCase().replace("_", " ")))));
        }
    }

    @EventHandler
    public void castSkill(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR ||
                e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (checkSkillCast(e.getPlayer()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void castSkill(PlayerInteractAtEntityEvent e) {
        if (checkSkillCast(e.getPlayer()))
            e.setCancelled(true);
    }

    private boolean checkSkillCast(Player p) {
        ItemStack stick = p.getInventory().getItemInMainHand();

        if (!Items.isSignalStick(stick)) {
            stick = p.getInventory().getItemInOffHand();
            if (!Items.isSignalStick(stick))
                return false;
        }

        Pet pet = Pet.fromOwner(p.getUniqueId());
        if (pet == null)
            return false;
        String signal = PlayerSignal.getSignalTag(p.getUniqueId());

        if (pet.isStillHere()) {
            pet.sendSignal(signal);
            return true;
        }
        return false;
    }

    @EventHandler
    public void dropStick(PlayerDropItemEvent e) {
        ItemStack it = e.getItemDrop().getItemStack();

        if (Items.isSignalStick(it)) {
            e.getItemDrop().remove();
        }
    }

    @EventHandler
    public void antiCraft(InventoryClickEvent e)
    {
        if(e.getView() == null || e.getView().getTopInventory() == null)
            return;

        if(e.getView().getTopInventory().getType().equals(InventoryType.ANVIL) ||
                e.getView().getTopInventory().getType().equals(InventoryType.WORKBENCH) ||
                e.getView().getTopInventory().getType().equals(InventoryType.ENCHANTING) ||
                e.getView().getTopInventory().getType().equals(InventoryType.GRINDSTONE) ||
                e.getView().getTopInventory().getType().equals(InventoryType.MERCHANT) ||
                e.getView().getTopInventory().getType().equals(InventoryType.LOOM))
        {
            ItemStack it = e.getCurrentItem();
            if(Items.isSignalStick(it))
                e.setCancelled(true);
        }


        if(e.getView().getTopInventory().getType().equals(InventoryType.CRAFTING) &&
                e.getSlot() <= 83 && e.getSlot() >= 80)
        {
            ItemStack it = e.getCurrentItem();
            if(Items.isSignalStick(it))
                e.setCancelled(true);
        }
    }

}
