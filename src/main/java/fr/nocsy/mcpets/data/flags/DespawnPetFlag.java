package fr.nocsy.mcpets.data.flags;

import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.PetDespawnReason;

public class DespawnPetFlag extends AbstractFlag implements StoppableFlag {

    int task;

    public static String NAME = "mcpets-despawn";

    public DespawnPetFlag(final MCPets instance) {
        super(NAME, false, instance);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void launch() {
        if (getFlag() == null) {
            MCPets.getLog().warning("Flag " + getFlagName() + " couldn't not be launched as it's null. Please contact Nocsy.");
            return;
        }

        MCPets.getLog().info("Starting flag " + getFlagName() + ".");

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), () -> {
            if (MCPets.getMythicMobs() == null) return;

            Player pl;
            for (UUID owner : new ArrayList<>(Pet.getActivePets().keySet())) {
                pl = Bukkit.getPlayer(owner);
                if (pl == null) continue;

                if (!testState(pl.getLocation())) continue;

                for (Pet pet : new ArrayList<>(Pet.getActivePetsForOwner(owner))) {
                    pet.despawn(PetDespawnReason.TELEPORT);
                }

                Language.CANT_FOLLOW_HERE.sendMessage(pl);
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(task);
    }

}
