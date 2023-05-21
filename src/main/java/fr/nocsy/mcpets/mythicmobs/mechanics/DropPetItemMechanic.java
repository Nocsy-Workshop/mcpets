package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DropPetItemMechanic implements ITargetedEntitySkill {

    String petItemId = "";
    float percentage = 1.0f;

    public DropPetItemMechanic(MythicLineConfig config) {
        this.petItemId = config.getString(new String[]{"petItem"}, this.petItemId);
        this.percentage = config.getFloat(new String[]{"chance"}, this.percentage);

        this.percentage = Math.max(Math.min(percentage, 1.0f), 0);
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);

        ItemStack it = ItemsListConfig.getInstance().getItemStack(petItemId);
        if(it != null)
        {
            Random random = new Random();
            if(random.nextFloat() <= percentage)
            {
                // Call the drop on sync so it can trigger events
                Bukkit.getScheduler().runTask(MCPets.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), it);
                    }
                });
            }

        }

        return SkillResult.SUCCESS;

    }
}
