package fr.nocsy.mcpets.mythicmobs.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;

@MythicMechanic(
        name = "dropPetInventory"
)
public class DropPetInventoryMechanic extends SkillMechanic implements ITargetedEntitySkill {

    public DropPetInventoryMechanic(MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile());
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        AbstractEntity ent = data.getCaster().getEntity();

        Pet pet = Pet.getFromEntity(ent.getBukkitEntity());
        if (pet == null) return SkillResult.CONDITION_FAILED;

        try {
            PetInventory petInventory = PetInventory.get(pet);

            if (petInventory != null) {
                Inventory inv = petInventory.getInventory();
                Location loc = BukkitAdapter.adapt(pet.getActiveMob().getLocation());

                Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
                    for (ItemStack item : inv.getContents()) {
                        if (item == null) continue;
                        loc.getWorld().dropItemNaturally(loc, item);
                    }
                });

                petInventory.setInventory(new PetInventoryHolder(inv.getSize(), PetInventoryHolder.Type.PET_INVENTORY_MENU).getInventory());
            }
        } catch (Exception ex) {
            MCPets.getLog().log(java.util.logging.Level.SEVERE, "Failed to drop pet inventory", ex);
            return SkillResult.CONDITION_FAILED;
        }

        return SkillResult.SUCCESS;
    }

}
