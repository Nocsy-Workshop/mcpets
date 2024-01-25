package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;

import java.util.ArrayList;

public class FlagsManager {

    private static ArrayList<AbstractFlag> flags = new ArrayList<>();
    @Getter
    private static boolean registered;

    public static void init(MCPets instance) {
        flags = new ArrayList<>();
        registered = true;

        if (instance == null) {
            MCPets.getLog().warning("主实例为空.标志无法注册...");
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
     * @return
     */
    public static AbstractFlag getFlag(String flagName)
    {
        return flags.stream().filter(f -> f.getFlagName().equals(flagName)).findFirst().orElse(null);
    }

    /**
     * Start the schedulers
     */
    public static void launchFlags() {
        MCPets.getLog().info("-=- 启动标志 -=-");
        int count = 0;
        for (AbstractFlag flag : flags) {
            if (flag instanceof StoppableFlag) {
                ((StoppableFlag) flag).launch();
                count++;
            }
        }
        MCPets.getLog().info(count + " 个标志已启动.");
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
