package fr.nocsy.mcpets.mythicmobs.mechanics;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.ItemsListConfig;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;

@MythicMechanic(
        name = "dropPetItem"
)
public class DropPetItemMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final String petItemId;
    private final float percentage;

    public DropPetItemMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());

        MythicLineConfig config = event.getConfig();

        petItemId = config.getString(new String[]{"petItem"}, "");
        percentage = Math.clamp(config.getFloat(new String[]{"chance"}, 1.0f), 0.0f, 1.0f);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);

        ItemStack item = ItemsListConfig.getInstance().getItemStack(petItemId);

        if (item != null && ThreadLocalRandom.current().nextFloat() <= percentage) {
            Bukkit.getScheduler().runTask(
                    MCPets.getInstance(),
                    () -> entity.getWorld().dropItemNaturally(
                            entity.getLocation(),
                            item
                    )
            );
        }

        return SkillResult.SUCCESS;
    }

}
