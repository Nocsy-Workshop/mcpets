package fr.nocsy.mcpets.mythicmobs.placeholders;

import javax.annotation.Nullable;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;

@MythicPlaceholder(placeholder = "pet.owner.uuid", version = "5.9")
public class PetOwnerUUIDPlaceholder extends PetPlaceholder<String> implements StringPlaceholder {

    public PetOwnerUUIDPlaceholder(GenericPlaceholderArguments context) {
        super(context);
    }

    @Nullable
    @Override
    public String applyWithMetaKeywords(PlaceholderContext context) {
        Pet pet = getPet(context);

        if (pet == null || pet.getOwner() == null) {
            return "null";
        }

        return pet.getOwner().toString();
    }

}
