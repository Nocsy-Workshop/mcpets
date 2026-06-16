package fr.nocsy.mcpets.mythicmobs.placeholders;

import javax.annotation.Nullable;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder = "pet.hp", version = "5.9")
public class PetHealthPlaceholder extends PetPlaceholder<Integer> implements IntegerPlaceholder {

    public PetHealthPlaceholder(GenericPlaceholderArguments context) {
        super(context);
    }

    @Nullable
    @Override
    public Integer applyWithMetaKeywords(PlaceholderContext context) {
        Pet pet = getPet(context);

        if (pet != null && pet.getPetStats() != null) {
            return (int) pet.getPetStats().getCurrentHealth();
        }

        SkillMetadata skillMetadata = context.skillMetadata();
        if (skillMetadata == null) return 0;

        SkillCaster caster = skillMetadata.getCaster();
        if (caster == null) return 0;
        if (caster.getEntity() == null) return 0;

        return (int) caster.getEntity().getHealth();
    }

}
