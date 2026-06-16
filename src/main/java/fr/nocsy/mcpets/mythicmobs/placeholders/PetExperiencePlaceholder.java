package fr.nocsy.mcpets.mythicmobs.placeholders;

import javax.annotation.Nullable;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.DoublePlaceholder;

@MythicPlaceholder(placeholder = "pet.experience", version = "5.9")
public class PetExperiencePlaceholder extends PetPlaceholder<Double> implements DoublePlaceholder {

    public PetExperiencePlaceholder(GenericPlaceholderArguments context) {
        super(context);
    }

    @Nullable
    @Override
    public Double applyWithMetaKeywords(PlaceholderContext context) {
        Pet pet = getPet(context);

        if (pet == null || pet.getPetStats() == null) {
            return 1D;
        }

        return pet.getPetStats().getExperience();
    }

}
