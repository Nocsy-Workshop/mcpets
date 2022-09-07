package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;

public class PetOnlyFlag extends AbstractFlag {

    @Getter
    public static String name = "mcpets-petonly";

    public PetOnlyFlag(MCPets instance) {
        super(name, false, instance);
    }
}
