package fr.nocsy.mcpets.data.flags;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;

import fr.nocsy.mcpets.MCPets;

public class FlagsManager {

    private static final List<AbstractFlag> flags = new ArrayList<>();

    @Getter
    private static boolean registered;

    public static void init(MCPets instance) {
        flags.clear();
        registered = true;

        if (instance == null) {
            MCPets.getLog().warning("The main instance is null. The flags could not be registered...");
            return;
        }

        flags.add(new DismountPetFlag(instance));
        flags.add(new DespawnPetFlag(instance));
        flags.add(new DismountFlyPetFlag(instance));
        flags.add(new PetDamageableByPlayerFlag(instance));

        for (AbstractFlag flag : flags) {
            flag.register();
        }
    }

    /**
     * Get the abstract flag registered
     */
    public static AbstractFlag getFlag(String flagName) {
        return flags.stream().filter(f -> f.getFlagName().equals(flagName)).findFirst().orElse(null);
    }

    /**
     * Start the schedulers
     */
    public static void launchFlags() {
        MCPets.getLog().info("-=- Launching Flags -=-");
        int count = 0;
        for (AbstractFlag flag : flags) {
            if (!(flag instanceof StoppableFlag stoppableFlag)) continue;
            stoppableFlag.launch();
            count++;
        }
        MCPets.getLog().info(count + " flags launched.");
    }

    /**
     * Stop the schedulers
     */
    public static void stopFlags() {
        for (AbstractFlag flag : flags) {
            if (!(flag instanceof StoppableFlag stoppableFlag)) continue;
            stoppableFlag.stop();
        }
        flags.clear();
    }

}
