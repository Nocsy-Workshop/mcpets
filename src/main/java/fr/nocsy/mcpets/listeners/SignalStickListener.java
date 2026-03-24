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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SignalStickListener implements Listener {

    @EventHandler
    public void switchSignal(final PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            final Player p = e.getPlayer();
            ItemStack stick = p.getInventory().getItemInMainHand();

            if (!Items.isSignalStick(stick)) {
                stick = p.getInventory().getItemInOffHand();
                if (!Items.isSignalStick(stick))
                    return;
            }

            final UUID owner = p.getUniqueId();
            final Pet pet = Pet.fromOwner(owner);
            if (pet == null)
                return;

            final String nextSignal = PlayerSignal.getNextSignal(owner);

            if (nextSignal == null)
                return;

            PlayerSignal.setSignal(owner, nextSignal);
            Utils.sendActionBar(p, Utils.hex(Language.SIGNAL_STICK_SIGNAL.getMessageFormatted(new FormatArg("%signal%", nextSignal.toLowerCase().replace("_", " ")))));
        }
    }

    @EventHandler
    public void castSkill(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (checkSkillCast(e.getPlayer()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void castSkill(final PlayerInteractAtEntityEvent e) {
        if (checkSkillCast(e.getPlayer()))
            e.setCancelled(true);
    }

    private boolean checkSkillCast(final Player p) {
        ItemStack stick = p.getInventory().getItemInMainHand();

        if (!Items.isSignalStick(stick)) {
            stick = p.getInventory().getItemInOffHand();
            if (!Items.isSignalStick(stick))
                return false;
        }

        final Pet pet = Pet.fromOwner(p.getUniqueId());
        if (pet == null)
            return false;
        final String signal = PlayerSignal.getSignalTag(p.getUniqueId());

        if (pet.isStillHere()) {
            pet.sendSignal(signal);
            return true;
        }
        return false;
    }

    /**
     * Prevent the signal stick from being dropped on the ground.
     * The item is silently destroyed to avoid exploitation.
     */
    @EventHandler
    public void dropStick(final PlayerDropItemEvent e) {
        final ItemStack it = e.getItemDrop().getItemStack();

        if (Items.isSignalStick(it)) {
            e.getItemDrop().remove();
        }
    }

    /**
     * Prevent swapping the signal stick between main hand and off-hand using the F key.
     * A stick present in the off-hand before a disconnect could cause a duplication bug,
     * as the inventory may be saved before the stick is properly removed.
     */
    @EventHandler
    public void antiSwap(final PlayerSwapHandItemsEvent e) {
        if (Items.isSignalStick(e.getMainHandItem()) || Items.isSignalStick(e.getOffHandItem())) {
            e.setCancelled(true);
        }
    }

    /**
     * Prevent the signal stick from being placed in:
     * - The off-hand slot (slot 40) via inventory click
     * - Any external inventory (chest, hopper, etc.)
     * - Any crafting station (anvil, workbench, enchanting table, etc.)
     * - The crafting result slots of the survival inventory
     * This prevents item duplication and stock farming exploits.
     */
    @EventHandler
    public void antiCraft(final InventoryClickEvent e) {
        if (e.getView() == null || e.getView().getTopInventory() == null)
            return;

        // Determine whether the action involves a signal stick
        boolean movingStick = Items.isSignalStick(e.getCurrentItem()) || Items.isSignalStick(e.getCursor());
        if (!movingStick && e.getClick() == ClickType.NUMBER_KEY) {
            // Number key hotbar swaps can also move the stick into a forbidden slot
            final ItemStack hotbarItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
            movingStick = Items.isSignalStick(hotbarItem);
        }

        if (!movingStick)
            return;

        // Block placement in the off-hand slot (slot 40 in the player inventory view)
        if (e.getSlot() == 40) {
            e.setCancelled(true);
            return;
        }

        final InventoryType topType = e.getView().getTopInventory().getType();

        // Block moving the stick into any non-player container (chest, hopper, barrel, etc.)
        if (e.getClickedInventory() != null
                && e.getClickedInventory().getType() != InventoryType.PLAYER
                && e.getClickedInventory().getType() != InventoryType.CRAFTING) {
            e.setCancelled(true);
            return;
        }

        // Block shift-clicking the stick into any open container other than the survival crafting grid
        if (e.isShiftClick() && topType != InventoryType.CRAFTING) {
            e.setCancelled(true);
            return;
        }

        // Block usage in crafting stations
        if (topType == InventoryType.ANVIL
                || topType == InventoryType.WORKBENCH
                || topType == InventoryType.ENCHANTING
                || topType == InventoryType.GRINDSTONE
                || topType == InventoryType.MERCHANT
                || topType == InventoryType.LOOM) {
            e.setCancelled(true);
            return;
        }

        // Block placement in the survival crafting result slots (slots 80-83 of the CRAFTING view)
        if (topType == InventoryType.CRAFTING && e.getSlot() >= 80 && e.getSlot() <= 83) {
            e.setCancelled(true);
        }
    }
}
