package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;

import java.util.ArrayList;

public class FlagsManager {

    private static ArrayList<AbstractFlag> flags = new ArrayList<>();

    public static void init(MCPets instance) {
        flags = new ArrayList<>();

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

    /**
     * Start the schedulers
     */
    public static void launchFlags() {
        MCPets.getLog().info("-=- Launching Flags -=-");
        int count = 0;
        for (AbstractFlag flag : flags) {
            if (flag instanceof StoppableFlag) {
                ((StoppableFlag) flag).launch();
                count++;
            }
        }
        MCPets.getLog().info(count + " flags launched.");
    }

    /**
     * Stop the schedulers
     */
    public static void stopFlags() {
        for (AbstractFlag flag : flags) {
            if (flag instanceof StoppableFlag) {
                ((StoppableFlag) flag).stop();
            }
        }
    }

}
