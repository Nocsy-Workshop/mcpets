package fr.nocsy.mcpets.data.flags;

import com.sk89q.worldguard.protection.flags.StateFlag;
import fr.nocsy.mcpets.MCPets;

import java.util.ArrayList;

public class FlagsManager {

    public static StateFlag ALMPET;

    private static final ArrayList<AbstractFlag> flags = new ArrayList<>();

    public static void init(MCPets instance) {
        ArrayList<AbstractFlag> flags = new ArrayList<>();

        if (instance == null) {
            MCPets.getLog().warning("The main instance is null. The flags could not be registered...");
            return;
        }

        flags.add(new DismountPetFlag(instance));
        flags.add(new DespawnPetFlag(instance));

        for (AbstractFlag flag : flags) {
            flag.register();
        }

    }

    public static void stopFlags() {
        for (AbstractFlag flag : flags) {
            if (flag instanceof StoppableFlag) {
                ((StoppableFlag) flag).stop();
            }
        }
    }

}
