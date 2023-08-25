package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PetDamageableByPlayerFlag extends AbstractFlag {

    public static String NAME = "mcpets-pet-player-damage";

    public PetDamageableByPlayerFlag(MCPets instance) {
        super(NAME, false, instance);
    }

    @Override
    public void register() {
        super.register();
    }

}
