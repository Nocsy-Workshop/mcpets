package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;

import java.util.HashMap;
import java.util.UUID;

public class PathFindingUtils {

    private static HashMap<UUID, AbstractLocation> registry = new HashMap<>();

    /**
     * Move the entity to the specified location
     * @param entity
     * @param destination
     */
    public static void moveTo(AbstractEntity entity, AbstractLocation destination)
    {
        MCPets.getMythicMobs().getVolatileCodeHandler().getAIHandler().navigateToLocation(entity, destination, 1);
    }

    /**
     * Stop the entity at its location
     * @param entity
     */
    public static void stop(AbstractEntity entity, UUID owner)
    {
        if(registry.get(owner) != null)
        {
            AbstractLocation loc = registry.get(owner);
            if(loc.getBlockX() == entity.getLocation().getBlockX() &&
                    loc.getBlockY() == entity.getLocation().getBlockY() &&
                    loc.getBlockZ() == entity.getLocation().getBlockZ())
            {
                return;
            }
        }
        moveTo(entity, entity.getLocation());
        registry.put(owner, entity.getLocation());
    }

}
