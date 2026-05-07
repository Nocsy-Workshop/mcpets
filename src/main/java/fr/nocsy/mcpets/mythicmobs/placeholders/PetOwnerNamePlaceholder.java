package fr.nocsy.mcpets.mythicmobs.placeholders;

import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.Bukkit;

@MythicPlaceholder(placeholder = "pet.owner.name", aliases = {})
public class PetOwnerNamePlaceholder extends AbstractPetPlaceholder {
    @Override
    public String apply(PlaceholderMeta meta, String arg) {
        Pet pet = pet(meta);
        return pet == null || pet.getOwner() == null ? "null" : Bukkit.getOfflinePlayer(pet.getOwner()).getName();
    }
}
