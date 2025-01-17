package fr.nocsy.mcpets.data;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class PetSkin {

    private static HashMap<String, ArrayList<PetSkin>> petSkins = new HashMap<>();

    @Getter
    private String uuid;

    @Getter
    private Pet objectPet;
    @Getter
    private String mythicMobId;
    @Getter
    private ItemStack icon;
    @Getter
    private String permission;
    @Getter
    private String pathId;

    private PetSkin(String pathId, Pet objectPet, String mythicMobId, String permission)
    {
        this.uuid = UUID.randomUUID().toString();

        this.pathId = pathId;
        this.objectPet = objectPet;
        this.mythicMobId = mythicMobId;
        this.permission = permission;
        initIcon();
    }

    /**
     * Load the PetSkin object in the cache
     * @param objectPet
     * @param modelSkinId
     * @param icon
     */
    public static void load(String pathId, Pet objectPet, String modelSkinId, String permission, ItemStack icon)
    {
        PetSkin petSkin = new PetSkin(pathId, objectPet, modelSkinId, permission);
        petSkin.setIcon(icon);

        ArrayList<PetSkin> listSkins = petSkins.get(objectPet.getId());
        if(listSkins == null)
            listSkins = new ArrayList<>();

        listSkins.add(petSkin);
        petSkins.put(objectPet.getId(), listSkins);
    }

    /**
     * Fetch the PetSkin from the icon
     * @param it
     * @return
     */
    public static PetSkin fromIcon(ItemStack it)
    {
        if(it.hasItemMeta() &&
            it.getItemMeta().hasLocalizedName())
        {
            String[] code = it.getItemMeta().getLocalizedName().split(";");
            if(code.length > 0 && code[0].equals("MCPetsSkins"))
            {
                String petId = code[1];
                String skinUuid = code[2];
                ArrayList<PetSkin> skins = petSkins.get(petId);
                if(skins != null)
                {
                    Optional<PetSkin> opt = skins.stream().filter(petSkin -> petSkin.getUuid().equals(skinUuid)).findFirst();
                    return opt.orElse(null);
                }
            }
        }
        return null;
    }

    /**
     * Fetch all skins from the pet
     * @param pet
     * @return
     */
    public static ArrayList<PetSkin> getSkins(Pet pet)
    {
        if(!petSkins.containsKey(pet.getId()))
            return new ArrayList<>();
        return petSkins.get(pet.getId());
    }

    /**
     * Open the pet skins to the player
     * @param p
     * @param pet
     * @return
     */
    public static boolean openInventory(Player p, Pet pet)
    {
        if(pet == null)
            return false;

        List<PetSkin> skins = petSkins.get(pet.getId());
        if(skins == null || skins.size() == 0)
            return false;

        skins = skins.stream().filter(petSkin -> p.hasPermission(petSkin.getPermission())).collect(Collectors.toList());

        int invSize = Math.min(skins.size(), 54);
        while(invSize <= 0 || invSize%9 != 0)
            invSize++;

        Inventory inventory = Bukkit.createInventory(null, invSize, Language.PET_SKINS_TITLE.getMessageFormatted(new FormatArg("%pet%", pet.getIcon().getItemMeta().getDisplayName())));

        for(PetSkin petSkin : skins)
        {
            inventory.addItem(petSkin.getIcon());
        }

        p.openInventory(inventory);
        addMetada(p);
        return true;
    }

    /**
     * Add metadata to handle GUI
     * @param p
     */
    private static void addMetada(Player p)
    {
        p.setMetadata("MCPetsSkins", new FixedMetadataValue(MCPets.getInstance(), "opened"));
    }

    /**
     * Remove the metadata to handle GUI
     * @param p
     */
    public static void removeMetadata(Player p)
    {
        p.setMetadata("MCPetsSkins", new FixedMetadataValue(MCPets.getInstance(), null));
    }

    /**
     * Clear previous entries
     */
    public static void clearList(Pet pet)
    {
        if(pet.hasSkins())
        {
            petSkins.put(pet.getId(), new ArrayList<>());
        }
    }

    /**
     * Check if the player has the skins metadata
     * @param p
     * @return
     */
    public static boolean hasMetadata(Player p)
    {
        return !p.getMetadata("MCPetsSkins").isEmpty() &&
                p.getMetadata("MCPetsSkins").get(0) != null &&
                p.getMetadata("MCPetsSkins").get(0).value() != null;
    }

    /**
     * Set the petSkin icon
     * @param icon
     */
    private void setIcon(ItemStack icon)
    {
        if (icon != null)
        {
            this.icon = icon;
            prepareIcon();
        }
    }
    /**
     * Initialize the icon
     */
    private void initIcon()
    {
        icon = Items.UNKNOWN.getItem().clone();
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("§6Skin §7: " + mythicMobId);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Click to apply that skin");
        meta.setLore(lore);
        icon.setItemMeta(meta);
        prepareIcon();
    }

    /**
     * Add the required localized name to the icon so we can identify it later on
     */
    private void prepareIcon()
    {
        ItemMeta meta = icon.getItemMeta();
        meta.setLocalizedName("MCPetsSkins;" + objectPet.getId() + ";" + uuid);
        icon.setItemMeta(meta);
    }

    /**
     * Apply the skin to the pet
     * @param instancePet
     */
    public boolean apply(Pet instancePet)
    {
        if(!instancePet.isStillHere())
            return false;

        if(!instancePet.getId().equals(objectPet.getId()))
            return false;

        Location loc = instancePet.getActiveMob().getEntity().getBukkitEntity().getLocation();

        boolean hasRider = instancePet.hasMount(Bukkit.getPlayer(instancePet.getOwner()));
        instancePet.setActiveSkin(this);

        instancePet.despawn(PetDespawnReason.SKIN);

        new BukkitRunnable() {
            @Override
            public void run() {
                instancePet.spawn(loc, false);
                if(hasRider)
                {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            instancePet.setMount(Bukkit.getPlayer(instancePet.getOwner()));
                        }
                    }.runTaskLater(MCPets.getInstance(), 2L);
                }
            }
        }.runTaskLater(MCPets.getInstance(), 2L);
        return true;
    }

}
