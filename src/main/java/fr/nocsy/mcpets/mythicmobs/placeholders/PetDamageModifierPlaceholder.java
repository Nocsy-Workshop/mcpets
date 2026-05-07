package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder = "pet.damagemodifier", aliases = {})
public class PetDamageModifierPlaceholder extends AbstractPetPlaceholder {
    @Override
    public String apply(PlaceholderMeta meta, String arg) {
        Pet pet = pet(meta);
        return pet == null || pet.getPetStats() == null ? "1" : Double.toString(pet.getPetStats().getDamageModifier());
    }
}
