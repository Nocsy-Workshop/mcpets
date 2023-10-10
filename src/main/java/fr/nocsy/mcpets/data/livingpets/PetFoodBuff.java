package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.utils.debug.Debugger;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PetFoodBuff {

    public static HashMap<Pet, ArrayList<PetFoodBuff>> runningBuffs = new HashMap<>();

    @Getter
    private PetFoodType type;
    @Getter
    private float power;
    @Getter
    private Pet pet;
    @Getter
    private PetMath operator;

    @Getter
    private long duration;

    public PetFoodBuff(@NotNull Pet pet, @NotNull PetFoodType type, float power, PetMath operator, long duration)
    {
        this.pet = pet;
        this.type = type;
        this.power = power;
        this.duration = duration;
    }

    public boolean apply()
    {

        PetStats stats = pet.getPetStats();
        if(stats == null)
        {
            Debugger.send("§cBuff could not be triggered on that pet as it has no statistics of a living pet.");
            return false;
        }

        List<PetFoodBuff> buffs = getBuffs(pet);
        for(PetFoodBuff buff : buffs)
        {
            if(buff.getType() == this.getType())
            {
                buff.stop();
            }
        }

        runTask();
        return true;
    }

    private void runTask()
    {
        ArrayList<PetFoodBuff> buffs = (ArrayList<PetFoodBuff>) getBuffs(pet);

        buffs.add(this);

        PetFoodBuff instance = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                instance.stop();
            }
        }.runTaskLater(MCPets.getInstance(), duration);
    }

    public void stop()
    {
        ArrayList<PetFoodBuff> buffs = runningBuffs.get(pet);
        buffs.remove(this);
    }

    public static List<PetFoodBuff> getBuffs(Pet pet)
    {
        ArrayList<PetFoodBuff> buffs = runningBuffs.get(pet);
        if(buffs == null)
            buffs = new ArrayList<>();
        return buffs;
    }

}
