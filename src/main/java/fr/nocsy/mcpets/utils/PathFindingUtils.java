package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;

public class PathFindingUtils {

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
    public static void stop(AbstractEntity entity)
    {
        moveTo(entity, entity.getLocation());
    }

}
