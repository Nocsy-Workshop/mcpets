package fr.nocsy.mcpets.utils;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nocsy.mcpets.data.config.GlobalConfig;

public class MenuPaginationHelper {


    public record PaginationConfig(int invSize,
                                   int startIndex,
                                   int itemsToShow,
                                   boolean needsNextButton,
                                   boolean needsPreviousButton,
                                   int startSlot) {
    }

    /**
     * Calculate PaginationConfig for fixed inventory size
     *
     * @param page Current page number
     * @param totalItems Total number of items
     * @param configuredSize Configured inventory size
     * @return PaginationConfig
     */
    public static PaginationConfig calculateFixedSizePagination(int page, int totalItems, int configuredSize) {
        int invSize = configuredSize;

        while (invSize <= 0 || invSize % 9 != 0) {
            invSize++;
        }

        int page0Slots = invSize - 1; // Reserve 1 slot for potential next button
        int otherPageSlots = invSize - 2; // Reserve slots for prev and next buttons

        int startIndex;
        int maxItemsPerPage;
        if (page == 0) {
            startIndex = 0;
            maxItemsPerPage = page0Slots;
        } else {
            startIndex = page0Slots + (page - 1) * otherPageSlots;
            maxItemsPerPage = otherPageSlots;
        }

        int remainingItems = totalItems - startIndex;

        boolean needsNextButton = remainingItems > (maxItemsPerPage + 1);

        int itemsToShow;
        if (needsNextButton) {
            itemsToShow = maxItemsPerPage;
        } else {
            itemsToShow = Math.min(remainingItems, maxItemsPerPage + 1);
        }

        int startSlot = page > 0 ? 1 : 0;

        return new PaginationConfig(invSize, startIndex, itemsToShow, needsNextButton, page > 0, startSlot);
    }

    /**
     * Calculate PaginationConfig for dynamic inventory size
     *
     * @param page Current page number
     * @param totalItems Total number of items
     * @return PaginationConfig
     */
    public static PaginationConfig calculateDynamicSizePagination(int page, int totalItems) {
        int pageSize = 52;

        int startIndex = pageSize * page;
        int remainingItems = totalItems - startIndex;

        int itemsToShow = remainingItems;
        boolean needsNextButton = false;

        if (remainingItems > pageSize + 1) {
            itemsToShow = pageSize;
            needsNextButton = true;
        } else if (remainingItems == pageSize + 1) {
            itemsToShow = pageSize + 1;
        }

        boolean needsPreviousButton = page > 0;
        int startSlot = needsPreviousButton ? 1 : 0;

        int invSize = itemsToShow + (needsPreviousButton ? 1 : 0) + (needsNextButton ? 1 : 0);
        while (invSize % 9 != 0) {
            invSize++;
        }
        invSize = Math.max(9, invSize);

        return new PaginationConfig(invSize, startIndex, itemsToShow, needsNextButton, needsPreviousButton, startSlot);
    }

    /**
     * Calculate PaginationConfig based on global config
     *
     * @param page Current page number
     * @param totalItems Total number of items
     * @return PaginationConfig
     */
    public static PaginationConfig calculatePagination(int page, int totalItems) {
        int adaptiveInv = GlobalConfig.getInstance().getAdaptiveInventory();

        if (adaptiveInv > 0) {
            return calculateFixedSizePagination(page, totalItems, adaptiveInv);
        } else {
            return calculateDynamicSizePagination(page, totalItems);
        }
    }

    /**
     * Calculate maximum pages for fixed inventory size
     *
     * @param totalItems Total number of items
     * @param configuredSize Configured inventory size
     * @return Maximum number of pages
     */
    public static int calculateMaxPagesFixedSize(int totalItems, int configuredSize) {
        int invSize = configuredSize;

        while (invSize <= 0 || invSize % 9 != 0) {
            invSize++;
        }

        int firstPageSlots = invSize - 1;
        int otherPageSlots = invSize - 2;

        if (totalItems <= firstPageSlots) {
            return 1;
        }

        int remainingAfterFirst = totalItems - firstPageSlots;

        if (remainingAfterFirst <= otherPageSlots + 1) {
            return 2;
        }

        int itemsNeedingPagination = remainingAfterFirst - (otherPageSlots + 1);
        int additionalPages = (int) Math.ceil((double) itemsNeedingPagination / otherPageSlots);

        // first page + middle pages + last page
        return 1 + additionalPages + 1;
    }

    /**
     * Calculate maximum pages for dynamic inventory size
     *
     * @param totalItems Total number of items
     * @return Maximum number of pages
     */
    public static int calculateMaxPagesDynamicSize(int totalItems) {
        int pageSize = 52;

        if (totalItems <= pageSize) {
            return 1;
        }

        int afterFirstPage = totalItems - pageSize;
        if (afterFirstPage <= pageSize + 1) {
            return 2;
        }

        return 1 + (int) Math.ceil((double) (afterFirstPage - (pageSize + 1)) / pageSize) + 1;
    }

    /**
     * Calculate maximum pages based on global config
     *
     * @param totalItems Total number of items
     * @return Maximum number of pages
     */
    public static int calculateMaxPages(int totalItems) {
        int adaptiveInv = GlobalConfig.getInstance().getAdaptiveInventory();

        if (adaptiveInv > 0) {
            return calculateMaxPagesFixedSize(totalItems, adaptiveInv);
        } else {
            return calculateMaxPagesDynamicSize(totalItems);
        }
    }

    /**
     * Handles pagination navigation for menus with simple page storage in tag
     *
     * @param item The clicked item
     * @param player The player who clicked
     * @param previousTag The tag prefix for previous page button
     * @param nextTag The tag prefix for next page button
     * @param menuOpener A function that opens the menu at a specific page (player, page)
     * @return true if navigation was handled, false otherwise
     */
    public static boolean handlePagination(ItemStack item, Player player, String previousTag, String nextTag, BiConsumer<Player, Integer> menuOpener) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        String tag = PDCTag.get(item.getItemMeta());
        if (tag == null) {
            return false;
        }

        if (tag.startsWith(previousTag)) {
            final int page = extractPageFromTag(tag);
            player.closeInventory();
            menuOpener.accept(player, Math.max(page - 1, 0));
            return true;
        }

        if (tag.startsWith(nextTag)) {
            final int page = extractPageFromTag(tag);
            player.closeInventory();
            menuOpener.accept(player, page + 1);
            return true;
        }

        return false;
    }

    /**
     * Extracts page number from tag string.
     * Handles tags in format "Prefix;page" or "Prefix;categoryId;page"
     *
     * @param tag The tag string
     * @return The page number
     */
    private static int extractPageFromTag(String tag) {
        String[] parts = tag.split(";");
        return Integer.parseInt(parts[parts.length - 1]);
    }

}
