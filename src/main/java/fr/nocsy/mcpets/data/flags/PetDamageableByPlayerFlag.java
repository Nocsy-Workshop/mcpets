package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;

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
