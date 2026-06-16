package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;

public abstract class PetPlaceholder<T> extends GenericPlaceholder<T> {

    public PetPlaceholder(GenericPlaceholderArguments context) {
        super(context);
    }

    protected Pet getPet(PlaceholderContext context) {
        SkillCaster caster = context.skillMetadata().getCaster();

        if (caster == null || caster.getEntity() == null) {
            return null;
        }

        return Pet.getFromEntity(caster.getEntity().getBukkitEntity());
    }

}
