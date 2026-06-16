package fr.nocsy.mcpets.mythicmobs.placeholders;

import javax.annotation.Nullable;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;

@MythicPlaceholder(placeholder = "pet.level.index", version = "5.9")
public class PetLevelIndexPlaceholder extends PetPlaceholder<Integer> implements IntegerPlaceholder {

    public PetLevelIndexPlaceholder(GenericPlaceholderArguments context) {
        super(context);
    }

    @Nullable
    @Override
    public Integer applyWithMetaKeywords(PlaceholderContext context) {
        Pet pet = getPet(context);

        if (pet == null || pet.getPetStats() == null) {
            return 0;
        }

        return pet.getPetStats().getCurrentLevelIndex();
    }

}
