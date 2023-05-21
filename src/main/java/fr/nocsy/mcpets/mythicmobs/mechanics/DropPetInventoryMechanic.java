package fr.nocsy.mcpets.mythicmobs.mechanics;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DropPetInventoryMechanic implements ITargetedEntitySkill {

    public DropPetInventoryMechanic(MythicLineConfig config) {}

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {

        AbstractEntity ent = data.getCaster().getEntity();

        Pet pet = Pet.getFromEntity(ent.getBukkitEntity());
        if (pet == null)
            return SkillResult.CONDITION_FAILED;

        try
        {
            PetInventory petInventory = PetInventory.get(pet);
            if(petInventory != null)
            {
                Inventory inv = petInventory.getInventory();
                Location loc = BukkitAdapter.adapt(pet.getActiveMob().getLocation());
                // Call the drop on sync so it can trigger events
                Bukkit.getScheduler().runTask(MCPets.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for(ItemStack it : inv.getContents())
                        {
                            if(it != null)
                                loc.getWorld().dropItemNaturally(loc, it);
                        }
                    }
                });

                petInventory.setInventory(Bukkit.createInventory(null, inv.getSize()));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return SkillResult.CONDITION_FAILED;
        }

        return SkillResult.SUCCESS;
    }
}
