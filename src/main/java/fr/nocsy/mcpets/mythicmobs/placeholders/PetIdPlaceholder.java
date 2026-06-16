package fr.nocsy.mcpets.mythicmobs.placeholders;

import javax.annotation.Nullable;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;

@MythicPlaceholder(placeholder = "pet.id", version = "5.9")
public class PetIdPlaceholder extends PetPlaceholder<String> implements StringPlaceholder {

    public PetIdPlaceholder(GenericPlaceholderArguments context) {
        super(context);
    }

    @Nullable
    @Override
    public String applyWithMetaKeywords(PlaceholderContext context) {
        Pet pet = getPet(context);

        if (pet == null) {
            return "null";
        }

        return pet.getId();
    }

}