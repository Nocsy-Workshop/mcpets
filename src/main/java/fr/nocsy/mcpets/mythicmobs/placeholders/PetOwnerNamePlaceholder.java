package fr.nocsy.mcpets.mythicmobs.placeholders;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;

import fr.nocsy.mcpets.data.Pet;

import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;

@MythicPlaceholder(placeholder = "pet.owner.name", version = "5.9")
public class PetOwnerNamePlaceholder extends PetPlaceholder<String> implements StringPlaceholder {

    public PetOwnerNamePlaceholder(GenericPlaceholderArguments context) {
        super(context);
    }

    @Nullable
    @Override
    public String applyWithMetaKeywords(PlaceholderContext context) {
        Pet pet = getPet(context);

        if (pet == null || pet.getOwner() == null) {
            return "null";
        }

        return Bukkit.getOfflinePlayer(pet.getOwner()).getName();
    }

}
