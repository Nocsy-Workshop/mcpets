package fr.nocsy.mcpets.data.config;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import dev.lone.itemsadder.api.CustomStack;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.PetAnnouncement;
import fr.nocsy.mcpets.utils.debug.Debugger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PetConfig extends AbstractConfig {

    private static HashMap<String, PetConfig> petConfigMapping = new HashMap<>();

    public static String getFilePath(final String petId) {
        final PetConfig config = getConfig(petId);
        if (config ==  null)
            return null;
        return getPath() + config.getFolderName() + "/" + config.getFileName();
    }

    public static PetConfig getConfig(final String petId) {
        return petConfigMapping.get(petId);
    }

    @Getter
    private Pet pet = null;

    /**
     * Base constructor of a pet configuration (one to one)
     * It will initialize the variables while loading the data
     */
    public PetConfig(final String folderName, final String fileName) {
        init(folderName, fileName);
        reload();
    }

    public void init(final String folderName, final String fileName) {
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
     * @param folderPath folder where to seek for the pets
     * @param clearPets whether the loaded pets should be cleared (only first call should do that)
     */
    public static void loadPets(final String folderPath, final boolean clearPets) {
        if (clearPets) {
            Bukkit.getConsoleSender().sendMessage("§9Loading pets... ");
            Pet.getObjectPets().clear();
        }

        final File folder = new File(folderPath);
        if (!folder.exists())
            folder.mkdirs();

        final File[] files = folder.listFiles();
        if (files == null) return;
        for (final File file : files) {
            if (file.isDirectory()) {
                loadPets(file.getPath().replace("\\", "/"), false);
                continue;
            }

            final PetConfig petConfig = new PetConfig(folder.getPath().replace("\\", "/").replace(AbstractConfig.getPath(), ""), file.getName());

            if (petConfig.getPet() != null) {
                if (Pet.getObjectPets().stream().anyMatch(pet -> pet.getId().equalsIgnoreCase(petConfig.getPet().getId())))
                    Bukkit.getConsoleSender().sendMessage("  §c* " + petConfig.getPet().getId() + " could not be loaded: another pet with the same ID already exists.");
                else {
                    Debugger.send("  §7- " + petConfig.getPet().getId() + " loaded succesfully.");
                    Pet.getObjectPets().add(petConfig.getPet());
                }
            }

        }


        Pet.getObjectPets().sort(Comparator.comparing(Pet::getId));

        if (clearPets)
            MCPets.getLog().info(Pet.getObjectPets().size() + " pets registered successfully !");
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
        final String id = getConfig().getString("Id");
        final String mobType = getConfig().getString("MythicMob");
        final String permission = getConfig().getString("Permission");
        final int distance = getConfig().getInt("Distance");
        final int spawnRange = getConfig().getInt("SpawnRange");
        final int comingbackRange = getConfig().getInt("ComingBackRange");
        final String despawnSkillName = getConfig().getString("DespawnSkill");
        final String tamingSkillName = getConfig().getString("Taming.TamingProgressSkill");
        final String tamingOverSkillName = getConfig().getString("Taming.TamingFinishedSkill");

        final boolean autoRide = getConfig().getBoolean("AutoRide");
        String mountType = getConfig().getString("MountType");
        final String mountPermission = getConfig().getString("MountPermission");
        final boolean despawnOnDismount = getConfig().getBoolean("DespawnOnDismount");
        int inventorySize = Math.max(Math.min(getConfig().getInt("InventorySize"), 54), 0);
        while(inventorySize < 54 && inventorySize % 9 != 0)
            inventorySize++;

        final List<String> signals = getConfig().getStringList("Signals.Values");
        boolean enableSignalStickFromMenu = true;
        if (getConfig().get("Signals.Item.GetFromMenu") != null)
            enableSignalStickFromMenu = getConfig().getBoolean("Signals.Item.GetFromMenu");

        if (id == null) {
            // Warning case on which something essential would be missing
            MCPets.getLog().warning("This pet could not be registered. Please check the configuration file to make sure you didn't miss anything.");
            MCPets.getLog().warning("Information about the registered pet : ");
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
        }
        else {
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

        pet.setDespawnSkill(despawnSkillName);
        pet.setTamingProgressSkill(tamingSkillName);
        pet.setTamingOverSkill(tamingOverSkillName);

        final ItemStack icon = legacyItemRead(pet.getIcon(), true, pet.toString(), "§cIcon (not set)", "Icon");
        pet.setIcon(icon);

        final ItemStack signalStickItem = legacyItemRead(pet.getSignalStick(), false, Items.buildSignalStickTag(pet), "§cSignal stick (not set)", "Signals.Item");
        pet.setSignalStick(signalStickItem);

        reloadSkins();
        reloadLevels();
    }

    private ItemStack legacyItemRead(final ItemStack item, final boolean showStats, final String localName, final String defaultName, final String path) {
        ItemStack itemStack = null;
        try {
            itemStack = getConfig().getItemStack(path + ".Raw");
            final ItemMeta meta = itemStack.getItemMeta();
            PDCTag.set(meta, localName);
            itemStack.setItemMeta(meta);
            if (showStats)
                itemStack = pet.applyStats(itemStack);
            return itemStack;
        }
        catch (final Exception ignored) {}

        if (itemStack == null) {
            String name = getConfig().getString(path + ".Name");
            if (name == null || name.isEmpty()) {
                name = getConfig().getString(path + ".DisplayName");
            }
            if (name == null) {
                name = defaultName;
            }
            final String mat = getConfig().getString(path + ".Material");
            final String itemsAdder = getConfig().getString(path + ".ItemsAdder", "");
            final int data = getConfig().getInt(path + ".CustomModelData");
            final String textureBase = getConfig().getString(path + ".TextureBase64");
            final List<String> description = getConfig().getStringList(path + ".Description");
            itemStack = pet.buildItem(
                    item, showStats, localName,
                    name, description, mat, data, textureBase
            );
            // ItemsAdder compat
            if (MCPets.isItemsAdderLoaded() && !itemsAdder.isEmpty()) {
                final CustomStack customStack = CustomStack.getInstance(itemsAdder);
                if (customStack != null) {
                    final ItemStack iaItem = customStack.getItemStack();
                    itemStack = pet.buildItem(
                            iaItem,
                            showStats,
                            localName,
                            name,
                            description,
                            iaItem.getType().toString(),
                            iaItem.getItemMeta().getCustomModelData(),
                            textureBase
                    );
                }
            }

            // Nexo integration
            if (MCPets.checkNexo()) {
                final String itemId = getConfig().getString(path + ".NexoId");
                if (itemId != null && !itemId.isEmpty()) {
                    final ItemBuilder builder = NexoItems.itemFromId(itemId);
                    if (builder != null) {
                        final ItemStack nexoItem = builder.build();
                        final Material nexoMat = nexoItem.getType();
                        final ItemMeta nexoMeta = nexoItem.getItemMeta();
                        final int nexoModelData = (nexoMeta != null && nexoMeta.hasCustomModelData())
                                ? nexoMeta.getCustomModelData()
                                : 0;

                        itemStack = pet.buildItem(
                                nexoItem,
                                showStats,
                                localName,
                                name,
                                description,
                                nexoMat != null ? nexoMat.toString() : null,
                                nexoModelData,
                                textureBase
                        );
                        return itemStack;
                    }
                }
            }
        }
        return itemStack;
    }

    private void reloadLevels() {
        final ArrayList<PetLevel> levels = new ArrayList<>();

        final List<String> keys = getConfig().getKeys(true).stream()
                .filter(key ->  key.contains("Levels") &&
                        key.replace(".", ";").split(";").length == 2)
                .collect(Collectors.toList());
        for (final String key : keys) {
            final String levelId = key.replace(".", ";").split(";")[1];

            String evolutionId = null;
            int delayBeforeEvolution = 0;
            boolean removePrevious = true;
            final double maxHealth = Optional.of(getConfig().getDouble(key + ".MaxHealth")).orElse(10D);
            final double regeneration = Optional.of(getConfig().getDouble(key + ".Regeneration")).orElse(0.1);
            final double resistanceModifier = Optional.of(getConfig().getDouble(key + ".ResistanceModifier")).orElse(1D);
            final double damageModifier = Optional.of(getConfig().getDouble(key + ".DamageModifier")).orElse(1D);
            final double power = Optional.of(getConfig().getDouble(key + ".Power")).orElse(1D);
            final int respawnCooldown = Optional.of(getConfig().getInt(key + ".Cooldowns.Respawn")).orElse(GlobalConfig.getInstance().getDefaultRespawnCooldown());
            final int revokeCooldown = Optional.of(getConfig().getInt(key + ".Cooldowns.Revoke")).orElse(0);
            final int inventoryExtension = Optional.of(getConfig().getInt(key + ".InventoryExtension")).orElse(0);
            final String levelName = getConfig().getString(key + ".Name");
            final double expThreshold = getConfig().getDouble(key + ".ExperienceThreshold");
            String announcement = null;
            PetAnnouncement announcementType = null;
            final String mythicSkill = Optional.ofNullable(getConfig().getString(key + ".Announcement.Skill")).orElse(null);

            if (getConfig().get(key + ".Evolution.PetId") != null) {
                evolutionId = getConfig().getString(key + ".Evolution.PetId");
                delayBeforeEvolution = Optional.of(getConfig().getInt(key + ".Evolution.DelayBeforeEvolution")).orElse(0);
                removePrevious = getConfig().get(key + ".Evolution.RemoveAccess") == null ||
                        getConfig().getBoolean(key + ".Evolution.RemoveAccess");
            }
            if (getConfig().get(key + ".Announcement.Text") != null) {
                announcement = getConfig().getString(key + ".Announcement.Text");
                announcementType = Arrays.stream(PetAnnouncement.values())
                        .filter(type -> type.name().equalsIgnoreCase(getConfig().getString(key + ".Announcement.Type")))
                        .findFirst().orElse(PetAnnouncement.CHAT);
            }

            final PetLevel petLevel = new PetLevel(pet,
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

        levels.sort(PetLevel::compareTo);
        pet.setPetLevels(levels);
    }

    /**
     * Method used only for the editor to register a clean level to the pet
     */
    public void registerCleanPetLevel(@Nullable String levelId) {
        if (levelId == null)
            levelId = UUID.randomUUID().toString();

        double defaultExp = 0.0;
        if (!pet.getPetLevels().isEmpty())
            defaultExp = pet.getPetLevels().get(pet.getPetLevels().size()-1).getExpThreshold() + 100;
        getConfig().set("Levels." + levelId + ".Name", levelId);
        getConfig().set("Levels." + levelId + ".ExperienceThreshold", defaultExp);
        save();

        // Then we reload the level cache
        reloadLevels();
    }

    public void deletePetLevel(final String levelId) {

        final PetLevel petLevel = pet.getPetLevels().stream().filter(level -> level.getLevelId().equals(levelId)).findFirst().orElse(null);
        if (petLevel == null)
            return;

        getConfig().set("Levels." + levelId, null);
        save();

        // Then we reload the cache
        reloadLevels();
    }

    private void reloadSkins() {
        PetSkin.clearList(pet);

        final List<String> keys = getConfig().getKeys(true).stream()
                .filter(key ->
                        key.contains("Skins") &&
                                key.replace(".", ";").split(";").length == 2)
                .collect(Collectors.toList());

        for(final String key : keys) {
            final String mythicMobId = getConfig().getString(key + ".MythicMob");
            final String skinPerm = getConfig().getString(key + ".Permission");

            final ItemStack skinIcon = legacyItemRead(null, false, "", "§cSkin icon (not set)", key + ".Icon");

            PetSkin.load(key, pet, mythicMobId, skinPerm, skinIcon);
        }
    }

    public void registerCleanPetSkin() {
        final String id = UUID.randomUUID().toString();
        getConfig().set("Skins." + id + ".MythicMob", pet.getMythicMobName());
        getConfig().set("Skins." + id + ".Permission", pet.getPermission());
        save();

        // Then we reload the level cache
        reloadSkins();
    }

    public void deletePetSkin(final String skinPath) {
        getConfig().set(skinPath, null);
        save();

        // Then we reload the cache
        reloadSkins();
    }

    /**
     * Load a fresh Pet Object instance with latest config info (for editor)
     */
    public static Pet loadConfigPet(final String petId) {
        final PetConfig oldConfig = PetConfig.getConfig(petId);
        final PetConfig refreshedConfig = new PetConfig(oldConfig.getFolderName(), oldConfig.getFileName());
        return refreshedConfig.getPet();
    }
}
