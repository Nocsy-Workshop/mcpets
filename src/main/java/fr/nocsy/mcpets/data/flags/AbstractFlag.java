package fr.nocsy.mcpets.data.flags;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.association.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.utils.debug.Debugger;
import lombok.Getter;

public abstract class AbstractFlag {

    @Getter
    private final MCPets MCPetsInstance;
    @Getter
    private final String flagName;
    @Getter
    private final boolean defaultValue;
    @Getter
    private StateFlag flag;

    public AbstractFlag(String flagName, boolean defaultValue, MCPets instance) {
        this.flagName = flagName;
        this.defaultValue = defaultValue;
        MCPetsInstance = instance;
    }

    /**
     * Register the given flag in WorldGuard
     */
    public void register() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            // create a flag with the name "flagName", defaulting to defaultValue
            StateFlag flag = new StateFlag(flagName, defaultValue);
            registry.register(flag);
            this.flag = flag; // only set our field if there was no error

            Debugger.send(getFlagName() + " flag registered successfully !");

        } catch (Exception e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get(flagName);
            if (existing instanceof StateFlag) {
                this.flag = (StateFlag) existing;
                Debugger.send(getFlagName() + " flag reattached successfully (was already registered).");
            }
            else {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
                MCPets.getLog().warning(MCPets.getLogName() + getFlagName() + " Flag couldn't be attached... Server restart will be necessary to fix the issue.");
                MCPets.getLog().warning(MCPets.getLogName() + getFlagName() + " has raised the following exception : " + e.getClass().getSimpleName());
                e.printStackTrace();
            }

        }

    }

    /**
     * Test if the state flag is allowed at player's location
     */
    public boolean testState(org.bukkit.Location location) {
        if (!GlobalConfig.getInstance().isWorldguardsupport())
            return true;
        Location loc = BukkitAdapter.adapt(location);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        RegionAssociable associable =  new DelayedRegionOverlapAssociation(query, loc, true);
        if (query == null || loc == null || associable == null || getFlag() == null)
            return false;
        return query.testState(loc, associable, getFlag());
    }
}
