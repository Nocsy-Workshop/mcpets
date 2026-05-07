package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder = "pet.owner.uuid", aliases = {})
public class PetOwnerUuidPlaceholder extends AbstractPetPlaceholder {
    @Override
    public String apply(PlaceholderMeta meta, String arg) {
        Pet pet = pet(meta);
        return pet == null || pet.getOwner() == null ? "null" : pet.getOwner().toString();
    }
}
