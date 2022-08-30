package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.livingpets.PetAnnouncement;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import io.lumine.mythic.api.skills.Skill;
import lombok.Getter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PetConfig extends AbstractConfig {

    @Getter
    private Pet pet = null;

    /**
     * Base constructor of a pet configuration (one to one)
     * It will initialize the variables while loading the data
     *
     * @param fileName
     */
    public PetConfig(String folderName, String fileName) {
        super.init(folderName, fileName);
        reload();
    }

    /**
     * Load all the existing pets
     *
     * @param folderPath : folder where to seek for the pets
     * @param clearPets  : whether or not the loaded pets should be cleared (only first call should do that)
     */
    public static void loadPets(String folderPath, boolean clearPets) {
        if (clearPets) {
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

            if (petConfig.getPet() != null)
                Pet.getObjectPets().add(petConfig.getPet());

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
        boolean autoRide = getConfig().getBoolean("AutoRide");
        String mountType = getConfig().getString("MountType");
        boolean despawnOnDismount = getConfig().getBoolean("DespawnOnDismount");
        int inventorySize = Math.min(getConfig().getInt("InventorySize"), 54);
        while(inventorySize < 54 && inventorySize % 9 != 0)
            inventorySize++;

        String iconName = getConfig().getString("Icon.Name");
        String materialType = getConfig().getString("Icon.Material");
        int customModelData = getConfig().getInt("Icon.CustomModelData");
        String textureBase64 = getConfig().getString("Icon.TextureBase64");
        List<String> description = getConfig().getStringList("Icon.Description");

        List<String> signals = getConfig().getStringList("Signals.Values");
        boolean enableSignalStickFromMenu = true;
        if(getConfig().get("Signals.Item.GetFromMenu") != null)
            enableSignalStickFromMenu = getConfig().getBoolean("Signals.Item.GetFromMenu");

        String signalStick_Name = getConfig().getString("Signals.Item.Name");
        String signalStick_Mat = getConfig().getString("Signals.Item.Material");
        int signalStick_Data = getConfig().getInt("Signals.Item.CustomModelData");
        String signalStick_64 = getConfig().getString("Signals.Item.TextureBase64");
        List<String> signalStick_Description = getConfig().getStringList("Signals.Item.Description");

        if (id == null ||
                mobType == null ||
                permission == null ||
                iconName == null) {
            // Warning case on which something essential would be missing
            MCPets.getLog().warning(MCPets.getLogName() + "This pet could not be registered. Please check the configuration file to make sure you didn't miss anything.");
            MCPets.getLog().warning(MCPets.getLogName() + "Information about the registered pet : ");
            MCPets.getLog().warning("id : " + id);
            MCPets.getLog().warning("mobType : " + mobType);
            MCPets.getLog().warning("permission : " + permission);
            return;
        }

        Pet pet = new Pet(id);
        pet.setMythicMobName(mobType);
        pet.setPermission(permission);
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

        pet.setIcon(pet.buildItem(pet.getIcon(), true, pet.toString(), iconName, description, materialType, customModelData, textureBase64));
        pet.setSignalStick(pet.buildItem(pet.getSignalStick(), false, Items.buildSignalStickTag(pet), signalStick_Name, signalStick_Description, signalStick_Mat, signalStick_Data, signalStick_64));

        PetSkin.clearList(pet);
        for(String key : getConfig().getKeys(true).stream()
                                                        .filter(key ->
                                                                   key.contains("Skins") &&
                                                                   key.replace(".", ";").split(";").length == 2)
                                                        .collect(Collectors.toList()))
        {
            String mythicMobId = getConfig().getString(key + ".MythicMob");
            String skinPerm = getConfig().getString(key + ".Permission");

            PetSkin.load(pet, mythicMobId, skinPerm, pet.buildItem(null, false, "",
                                                                        getConfig().getString(key + ".Icon.DisplayName"),
                                                                        getConfig().getStringList(key + ".Icon.Lore"),
                                                                        getConfig().getString(key + ".Icon.Material"),
                                                                        getConfig().getInt(key + ".Icon.CustomModelData"),
                                                                        getConfig().getString(key + ".Icon.TextureBase64")));
        }

        ArrayList<PetLevel> levels = new ArrayList<>();

        for(String key : getConfig().getKeys(true).stream()
                                    .filter(key ->  key.contains("Levels") &&
                                                    key.replace(".", ";").split(";").length == 2)
                                    .collect(Collectors.toList()))
        {

            String evolutionId = null;
            int delayBeforeEvolution = 0;
            double maxHealth = getConfig().getDouble(key + ".MaxHealth");
            double regeneration = Optional.of(getConfig().getDouble(key + ".Regeneration")).orElse(0.1);
            double resistanceModifier = Optional.of(getConfig().getDouble(key + ".ResistanceModifier")).orElse(1D);
            double damageModifier = Optional.of(getConfig().getDouble(key + ".DamageModifier")).orElse(1D);
            double power = Optional.of(getConfig().getDouble(key + ".Power")).orElse(1D);
            int respawnCooldown = Optional.of(getConfig().getInt(key + ".Cooldowns.Respawn")).orElse(0);
            int revokeCooldown = Optional.of(getConfig().getInt(key + ".Cooldowns.Revoke")).orElse(0);;
            int inventoryExtension = Optional.of(getConfig().getInt(key + ".InventoryExtension")).orElse(0);;
            String levelName = getConfig().getString(key + ".Name");
            double expThreshold = getConfig().getDouble(key + ".ExperienceThreshold");
            String announcement = null;
            PetAnnouncement announcementType = null;
            String mythicSkill = Optional.ofNullable(getConfig().getString(key + ".Announcement.Skill")).orElse(null);

            if(getConfig().get(key + ".Evolution") != null)
            {
                evolutionId = getConfig().getString(key + ".Evolution.PetId");
                delayBeforeEvolution = getConfig().getInt(key + ".Evolution.DelayBeforeEvolution");
            }
            if(getConfig().get(key + "Announcement") != null)
            {
                announcement = getConfig().getString(key + ".Announcement.Text");
                announcementType = Optional.of(PetAnnouncement.valueOf(getConfig().getString(key + ".Announcement.Type"))).orElse(PetAnnouncement.CHAT);
            }

            PetLevel petLevel = new PetLevel(pet,
                                            evolutionId,
                                            delayBeforeEvolution,
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

        this.pet = pet;
    }

}
