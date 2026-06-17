package fr.nocsy.mcpets.data.livingpets;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import lombok.Getter;

import org.bukkit.Bukkit;

import org.jetbrains.annotations.NotNull;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.utils.debug.Debugger;

public class PetFoodBuff {

    public static Map<Pet, List<PetFoodBuff>> runningBuffs = new HashMap<>();

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

    public PetFoodBuff(@NotNull Pet pet, @NotNull PetFoodType type, float power, PetMath operator, long duration) {
        this.pet = pet;
        this.type = type;
        this.power = power;
        this.duration = duration;
        this.operator = operator;
    }

    public boolean apply() {
        if (pet.getPetStats() == null) {
            Debugger.send("§cBuff could not be triggered on that pet as it has no statistics of a living pet.");
            return false;
        }

        for (PetFoodBuff buff : new ArrayList<>(getBuffs(pet))) {
            if (buff.getType() != this.getType()) continue;
            buff.stop();
        }

        runTask();
        return true;
    }

    private void runTask() {
        List<PetFoodBuff> buffs = getBuffs(pet);
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

        Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), instance::stop, duration);
    }

    public void stop() {
        List<PetFoodBuff> buffs = runningBuffs.get(pet);
        if (buffs == null) return;

        buffs.remove(this);
        Debugger.send("§7Buff §a" + type.name() + "§7 applied to §6" + pet.getId() + "§7 has §cexpired§7 after §a" + duration + "§7 ticks.");
    }

    public static List<PetFoodBuff> getBuffs(Pet pet) {
        return runningBuffs.getOrDefault(pet, List.of());
    }

}
