package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.utils.PetAnnouncement;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.Skill;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PetConfig extends AbstractConfig {

    private static HashMap<String, PetConfig> petConfigMapping = new HashMap<>();
    public static String getFilePath(String petId)
    {
        PetConfig config = getConfig(petId);
        if(config ==  null)
            return null;
        return getPath() + config.getFolderName() + "/" + config.getFileName();
    }
    public static PetConfig getConfig(String petId)
    {
        return petConfigMapping.get(petId);
    }

    @Getter
    private Pet pet = null;

    /**
     * Base constructor of a pet configuration (one to one)
     * It will initialize the variables while loading the data
     *
     * @param fileName
     */
    public PetConfig(String folderName, String fileName) {
        init(folderName, fileName);
        reload();
    }

    public void init(String folderName, String fileName) {
        super.init(folderName, fileName);

        if (getConfig().get("Id") == null)
            getConfig().set("Id", fileName.replace(".yml", ""));
        if (getConfig().get("MythicMob") == null)
            getConfig().set("MythicMob", "No MythicMob defined");
        if (getConfig().get("Permission") == null)
            getConfig().set("Permission", PPermission.USE.getPermission());
        if (getConfig().get("Distance") == null)
            getConfig().set("Distance", 6);
        if (getConfig().get("SpawnRange") == null)
            getConfig().set("SpawnRange", 3);
        if (getConfig().get("ComingBackRange") == null)
            getConfig().set("ComingBackRange", 3);
        if (getConfig().get("MythicMob") == null)
            getConfig().set("MythicMob", "No MythicMob defined");
        if (getConfig().get("MythicMob") == null)
            getConfig().set("MythicMob", "No MythicMob defined");

        save();
    }


    /**
     * Load all the existing pets
     *
     * @param folderPath : folder where to seek for the pets
     * @param clearPets  : whether or not the loaded pets should be cleared (only first call should do that)
     */
    public static void loadPets(String folderPath, boolean clearPets) {
        if (clearPets) {
            Bukkit.getConsoleSender().sendMessage("§9Loading pets... ");
            Pet.getObjectPets().clear();
        }

        File folder = new File(folderPath);
        if (!folder.exists())
            folder.mkdirs();

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                loadPets(file.getPath().replace("\\", "/"), false);
                continue;
            }

            PetConfig petConfig = new PetConfig(folder.getPath().replace("\\", "/").replace(AbstractConfig.getPath(), ""), file.getName());

            if (petConfig.getPet() != null) {
                if(Pet.getObjectPets().stream().anyMatch(pet -> pet.getId().equalsIgnoreCase(petConfig.getPet().getId())))
                    Bukkit.getConsoleSender().sendMessage("  §c* " + petConfig.getPet().getId() + " could not be loaded: another pet with the same ID already exists.");
                else
                {
                    Bukkit.getConsoleSender().sendMessage("  §7- " + petConfig.getPet().getId() + " loaded succesfully.");
                    Pet.getObjectPets().add(petConfig.getPet());
                }
            }

        }


        Pet.getObjectPets().sort(new Comparator<Pet>() {
            @Override
            public int compare(Pet o1, Pet o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        if (clearPets)
            MCPets.getLog().info(MCPets.getLogName() + Pet.getObjectPets().size() + " pets registered successfully !");
    }

    /**
     * Save the data within the file (unused for a PetConfig)
     */
    @Override
    public void save() {
        super.save();
    }

    /**
     * Load the data from the file to create an associated Pet
     */
    @Override
    public void reload() {
        // Loading the YAMLConfiguration object
        loadConfig();

        // Setting up the data
        String id = getConfig().getString("Id");
        String mobType = getConfig().getString("MythicMob");
        String permission = getConfig().getString("Permission");
        int distance = getConfig().getInt("Distance");
        int spawnRange = getConfig().getInt("SpawnRange");
        int comingbackRange = getConfig().getInt("ComingBackRange");
        String despawnSkillName = getConfig().getString("DespawnSkill");
        String tamingSkillName = getConfig().getString("Taming.TamingProgressSkill");
        String tamingOverSkillName = getConfig().getString("Taming.TamingFinishedSkill");

        boolean autoRide = getConfig().getBoolean("AutoRide");
        String mountType = getConfig().getString("MountType");
        String mountPermission = getConfig().getString("MountPermission");
        boolean despawnOnDismount = getConfig().getBoolean("DespawnOnDismount");
        int inventorySize = Math.max(Math.min(getConfig().getInt("InventorySize"), 54), 0);
        while(inventorySize < 54 && inventorySize % 9 != 0)
            inventorySize++;

        List<String> signals = getConfig().getStringList("Signals.Values");
        boolean enableSignalStickFromMenu = true;
        if(getConfig().get("Signals.Item.GetFromMenu") != null)
            enableSignalStickFromMenu = getConfig().getBoolean("Signals.Item.GetFromMenu");

        if (id == null) {
            // Warning case on which something essential would be missing
            MCPets.getLog().warning(MCPets.getLogName() + "This pet could not be registered. Please check the configuration file to make sure you didn't miss anything.");
            MCPets.getLog().warning(MCPets.getLogName() + "Information about the registered pet : ");
            MCPets.getLog().warning("id : " + id);
            MCPets.getLog().warning("mobType : " + mobType);
            MCPets.getLog().warning("permission : " + permission);
            return;
        }

        this.pet = new Pet(id);

        petConfigMapping.put(id, this);
        pet.setMythicMobName(mobType);
        pet.setPermission(permission);
        pet.setMountPermission(mountPermission);
        if (getConfig().get("Mountable") == null) {
            pet.setMountable(GlobalConfig.getInstance().isMountable());
        } else {
            pet.setMountable(getConfig().getBoolean("Mountable"));
        }
        if (mountType == null)
            mountType = "walking";
        pet.setDespawnOnDismount(despawnOnDismount);
        pet.setAutoRide(autoRide);
        pet.setDistance(distance);
        pet.setSpawnRange(spawnRange);
        pet.setComingBackRange(comingbackRange);
        pet.setMountType(mountType);
        pet.setDefaultInventorySize(inventorySize);
        pet.setSignals(signals);
        pet.setEnableSignalStickFromMenu(enableSignalStickFromMenu);

        if (despawnSkillName != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Optional<Skill> optionalSkill = MCPets.getMythicMobs().getSkillManager().getSkill(despawnSkillName);
                    optionalSkill.ifPresent(pet::setDespawnSkill);
                    if (pet.getDespawnSkill() == null) {
                        MCPets.getLog().warning(MCPets.getLogName() + "Impossible to link the despawn skill \"" + despawnSkillName + "\" to the pet \"" + pet.getId() + "\", because this skill doesn't exist.");
                    }
                }
            }.runTaskLater(MCPets.getInstance(), 5L);
        }
        if (tamingSkillName != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Optional<Skill> optionalSkill = MCPets.getMythicMobs().getSkillManager().getSkill(tamingSkillName);
                    optionalSkill.ifPresent(pet::setTamingProgressSkill);
                }
            }.runTaskLater(MCPets.getInstance(), 5L);
        }
        if (tamingOverSkillName != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Optional<Skill> optionalSkill = MCPets.getMythicMobs().getSkillManager().getSkill(tamingOverSkillName);
                    optionalSkill.ifPresent(pet::setTamingOverSkill);
                }
            }.runTaskLater(MCPets.getInstance(), 5L);
        }

        ItemStack icon = legacyItemRead(pet.getIcon(), true, pet.toString(), "§cIcon (not set)", "Icon");
        pet.setIcon(icon);

        ItemStack signalStickItem = legacyItemRead(pet.getSignalStick(), false, Items.buildSignalStickTag(pet), "§cSignal stick (not set)", "Signals.Item");
        pet.setSignalStick(signalStickItem);

        reloadSkins();
        reloadLevels();
    }

    private ItemStack legacyItemRead(ItemStack item, boolean showStats, String localName, String defaultName, String path)
    {
        ItemStack itemStack = null;
        try
        {
            itemStack = getConfig().getItemStack(path + ".Raw");
            ItemMeta meta = itemStack.getItemMeta();
            meta.setLocalizedName(localName);
            itemStack.setItemMeta(meta);
            if(showStats)
                itemStack = pet.applyStats(itemStack);
            return itemStack;
        }
        catch (Exception ignored) {}

        if(itemStack == null)
        {
            String name = getConfig().getString(path + ".Name");
            if(name == null || name.isEmpty())
            {
                name = getConfig().getString(path + ".DisplayName");
            }
            if(name == null)
            {
                name = defaultName;
            }
            String mat = getConfig().getString(path + ".Material");
            int data = getConfig().getInt(path + ".CustomModelData");
            String textureBase = getConfig().getString(path + ".TextureBase64");
            List<String> description = getConfig().getStringList(path + ".Description");
            itemStack = pet.buildItem(
                    item,
                    showStats,
                    localName,
                    name, description, mat, data, textureBase
            );
        }
        return itemStack;
    }

    private void reloadLevels()
    {
        ArrayList<PetLevel> levels = new ArrayList<>();

        for(String key : getConfig().getKeys(true).stream()
                .filter(key ->  key.contains("Levels") &&
                        key.replace(".", ";").split(";").length == 2)
                .collect(Collectors.toList()))
        {
            String levelId = key.replace(".", ";").split(";")[1];

            String evolutionId = null;
            int delayBeforeEvolution = 0;
            boolean removePrevious = true;
            double maxHealth = Optional.of(getConfig().getDouble(key + ".MaxHealth")).orElse(10D);
            double regeneration = Optional.of(getConfig().getDouble(key + ".Regeneration")).orElse(0.1);
            double resistanceModifier = Optional.of(getConfig().getDouble(key + ".ResistanceModifier")).orElse(1D);
            double damageModifier = Optional.of(getConfig().getDouble(key + ".DamageModifier")).orElse(1D);
            double power = Optional.of(getConfig().getDouble(key + ".Power")).orElse(1D);
            int respawnCooldown = Optional.of(getConfig().getInt(key + ".Cooldowns.Respawn")).orElse(GlobalConfig.getInstance().getDefaultRespawnCooldown());
            int revokeCooldown = Optional.of(getConfig().getInt(key + ".Cooldowns.Revoke")).orElse(0);;
            int inventoryExtension = Optional.of(getConfig().getInt(key + ".InventoryExtension")).orElse(0);;
            String levelName = getConfig().getString(key + ".Name");
            double expThreshold = getConfig().getDouble(key + ".ExperienceThreshold");
            String announcement = null;
            PetAnnouncement announcementType = null;
            String mythicSkill = Optional.ofNullable(getConfig().getString(key + ".Announcement.Skill")).orElse(null);

            if(getConfig().get(key + ".Evolution.PetId") != null)
            {
                evolutionId = getConfig().getString(key + ".Evolution.PetId");
                delayBeforeEvolution = Optional.of(getConfig().getInt(key + ".Evolution.DelayBeforeEvolution")).orElse(0);
                removePrevious = getConfig().get(key + ".Evolution.RemoveAccess") == null ||
                        getConfig().getBoolean(key + ".Evolution.RemoveAccess");
            }
            if(getConfig().get(key + ".Announcement.Text") != null)
            {
                announcement = getConfig().getString(key + ".Announcement.Text");
                announcementType = Arrays.stream(PetAnnouncement.values())
                        .filter(type -> type.name().equalsIgnoreCase(getConfig().getString(key + ".Announcement.Type")))
                        .findFirst().orElse(PetAnnouncement.CHAT);
            }

            PetLevel petLevel = new PetLevel(pet,
                    levelId,
                    evolutionId,
                    delayBeforeEvolution,
                    removePrevious,
                    maxHealth,
                    regeneration,
                    resistanceModifier,
                    damageModifier,
                    power,
                    respawnCooldown,
                    revokeCooldown,
                    inventoryExtension,
                    levelName,
                    expThreshold,
                    announcement,
                    announcementType,
                    mythicSkill);

            levels.add(petLevel);
        }

        levels.sort(new Comparator<PetLevel>() {
            @Override
            public int compare(PetLevel level1, PetLevel level2) {
                return level1.compareTo(level2);
            }
        });
        pet.setPetLevels(levels);
    }

    /**
     * Method used only for the editor to register a clean level to the pet
     * @param levelId
     */
    public void registerCleanPetLevel(@Nullable String levelId)
    {
        if(levelId == null)
            levelId = UUID.randomUUID().toString();

        double defaultExp = 0.0;
        if(pet.getPetLevels().size() > 0)
            defaultExp = pet.getPetLevels().get(pet.getPetLevels().size()-1).getExpThreshold() + 100;
        getConfig().set("Levels." + levelId + ".Name", levelId);
        getConfig().set("Levels." + levelId + ".ExperienceThreshold", defaultExp);
        save();

        // Then we reload the level cache
        reloadLevels();
    }

    public void deletePetLevel(String levelId)
    {

        PetLevel petLevel = pet.getPetLevels().stream().filter(level -> level.getLevelId().equals(levelId)).findFirst().orElse(null);
        if(petLevel == null)
            return;

        getConfig().set("Levels." + levelId, null);
        save();

        // Then we reload the cache
        reloadLevels();
    }

    private void reloadSkins()
    {
        PetSkin.clearList(pet);
        for(String key : getConfig().getKeys(true).stream()
                .filter(key ->
                        key.contains("Skins") &&
                                key.replace(".", ";").split(";").length == 2)
                .collect(Collectors.toList()))
        {
            String mythicMobId = getConfig().getString(key + ".MythicMob");
            String skinPerm = getConfig().getString(key + ".Permission");

            ItemStack skinIcon = legacyItemRead(null, false, "", "§cSkin icon (not set)", key + ".Icon");

            PetSkin.load(key, pet, mythicMobId, skinPerm, skinIcon);
        }
    }

    public void registerCleanPetSkin()
    {
        String id = UUID.randomUUID().toString();
        getConfig().set("Skins." + id + ".MythicMob", pet.getMythicMobName());
        getConfig().set("Skins." + id + ".Permission", pet.getPermission());
        save();

        // Then we reload the level cache
        reloadSkins();
    }

    public void deletePetSkin(String skinPath)
    {
        getConfig().set(skinPath, null);
        save();

        // Then we reload the cache
        reloadSkins();
    }

    /**
     * Load a fresh Pet Object instance with latest config info (for editor)
     * @param petId
     * @return
     */
    public static Pet loadConfigPet(String petId)
    {
        PetConfig oldConfig = PetConfig.getConfig(petId);
        PetConfig refreshedConfig = new PetConfig(oldConfig.getFolderName(), oldConfig.getFileName());
        return refreshedConfig.getPet();
    }

}
