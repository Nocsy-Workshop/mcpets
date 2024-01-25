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
            Debugger.send("§c无法在该宠物上触发增益效果,因为它没有生活宠物的统计信息.");
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

        Debugger.send("§7应用增益效果 §a" + type.name() + "§7 到 §6" + pet.getId() + "§7,持续 §a" + duration + "§7 个刻.");
        Debugger.send("§7增益效果信息:" +
                "  \n宠物所有者是 §b" + pet.getOwner() +
                "  \n§a类型: §7" + type.name() +
                "  \n§a强度: §7" + power +
                "  \n§a运算符: §7" + operator.name() +
                "  \n§a持续时间: §7" + duration + "§7 tick.");

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
        Debugger.send("§7应用到 §6" + pet.getId() + "§7 的增益效果 §a" + type.name() + "§7 在 §a" + duration + "§7 个tick后 §c过期§7.");
    }

    public static List<PetFoodBuff> getBuffs(Pet pet)
    {
        ArrayList<PetFoodBuff> buffs = runningBuffs.get(pet);
        if(buffs == null)
            buffs = new ArrayList<>();
        return buffs;
    }

}
