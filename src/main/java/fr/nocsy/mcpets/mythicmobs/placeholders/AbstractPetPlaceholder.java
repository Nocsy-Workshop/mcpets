package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;

abstract class AbstractPetPlaceholder implements MetaPlaceholder {

    protected AbstractEntity entity(PlaceholderMeta meta) {
        return meta.getCaster().getEntity();
    }

    protected Pet pet(PlaceholderMeta meta) {
        return Pet.getFromEntity(entity(meta).getBukkitEntity());
    }
}
