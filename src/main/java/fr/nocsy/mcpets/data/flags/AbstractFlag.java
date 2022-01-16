package fr.nocsy.mcpets.data.flags;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.nocsy.mcpets.MCPets;
import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class AbstractFlag {

    @Getter
    private StateFlag flag;

    @Getter
    private  final MCPets MCPetsInstance;
    @Getter
    private final String flagName;
    @Getter
    private final boolean defaultValue;

    public AbstractFlag(String flagName, boolean defaultValue, MCPets instance)
    {
        this.flagName       = flagName;
        this.defaultValue   = defaultValue;
        MCPetsInstance = instance;
    }

    /**
     * Register the given flag in WorldGuard
     */
    public void register()
    {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        ((SimpleFlagRegistry) WorldGuard.getInstance().getFlagRegistry()).setInitialized(false);

        try {

            // create a flag with the name "flagName", defaulting to defaultValue
            StateFlag flag = new StateFlag(flagName, defaultValue);
            registry.register(flag);
            this.flag = flag; // only set our field if there was no error

            MCPets.getLog().info(MCPets.getLogName() + getFlagName() + " flag registered successfully !");

        } catch (Exception e) {
            MCPets.getLog().warning(MCPets.getLogName() + "Exception raised " + e.getClass().getSimpleName());
            MCPets.getLog().warning(MCPets.getLogName() + getFlagName() + " seems to be conflicting with a previously existing instance of the plugin. Trying to attach the flag to the previous version...");
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get(flagName);
            MCPets.getLog().warning(MCPets.getLogName() +  getFlagName() + " has been considered as " + existing + " by Worldguard");
            if (existing instanceof StateFlag) {
                this.flag = (StateFlag) existing;
                MCPets.getLog().info(MCPets.getLogName() +  getFlagName() + " flag attached successfully !");
            } else {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
                MCPets.getLog().warning(MCPets.getLogName() + getFlagName() + " Flag couldn't be attached... Server restart will be necessary to fix the issue.");
            }

        }
    }

    /**
     * Test if the state flag is allowed at player's location
     * @param p
     * @return
     */
    public boolean testState(Player p)
    {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
        Location loc = localPlayer.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.testState(loc, localPlayer, getFlag());
    }

}
