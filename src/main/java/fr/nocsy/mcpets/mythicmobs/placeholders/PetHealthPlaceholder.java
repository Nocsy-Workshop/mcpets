package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder = "pet.hp", aliases = {})
public class PetHealthPlaceholder extends AbstractPetPlaceholder {
    @Override
    public String apply(PlaceholderMeta meta, String arg) {
        AbstractEntity entity = entity(meta);
        Pet pet = Pet.getFromEntity(entity.getBukkitEntity());
        return pet == null || pet.getPetStats() == null
                ? Integer.toString((int) entity.getHealth())
                : Integer.toString((int) pet.getPetStats().getCurrentHealth());
    }
}
