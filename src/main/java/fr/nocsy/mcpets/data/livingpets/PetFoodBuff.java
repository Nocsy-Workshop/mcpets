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
        this.operator = operator;
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
        ArrayList<PetFoodBuff> toRemove = new ArrayList<>();
        for(PetFoodBuff buff : buffs)
        {
            if(buff.getType() == this.getType())
            {
                toRemove.add(buff);
            }
        }
        for (PetFoodBuff buff : toRemove)
        {
            buff.stop();
        }

        runTask();
        return true;
    }

    private void runTask()
    {
        ArrayList<PetFoodBuff> buffs = (ArrayList<PetFoodBuff>) getBuffs(pet);
        buffs.add(this);
        runningBuffs.put(pet, buffs);

        Debugger.send("§7Applying buff §a" + type.name() + "§7 on §6" + pet.getId() + "§7 for §a" + duration + "§7 ticks.");
        Debugger.send("§7Buff information: " +
                "  \nPet owner is §b" + pet.getOwner() +
                "  \n§aType: §7" + type.name() +
                "  \n§aPower: §7" + power +
                "  \n§aOperator: §7" + operator.name() +
                "  \n§aDuration: §7" + duration + "§7 ticks.");

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
        if(buffs == null)
            return;
        buffs.remove(this);
        Debugger.send("§7Buff §a" + type.name() + "§7 applied to §6" + pet.getId() + "§7 has §cexpired§7 after §a" + duration + "§7 ticks.");
    }

    public static List<PetFoodBuff> getBuffs(Pet pet)
    {
        ArrayList<PetFoodBuff> buffs = runningBuffs.get(pet);
        if(buffs == null)
            buffs = new ArrayList<>();
        return buffs;
    }

}
