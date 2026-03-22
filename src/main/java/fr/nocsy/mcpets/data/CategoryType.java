package fr.nocsy.mcpets.data;

import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;

/**
 * Enumeration of the different categories of pets
 */
public enum CategoryType {
    PET(Language.INVENTORY_PETS_MENU),
    MOUNT(Language.INVENTORY_MOUNTS_MENU),
    DEFAULT(Language.CATEGORY_MENU_TITLE);

    @Getter
    private final Language title;

    CategoryType(Language title) {
        this.title = title;
    }
}
