package fr.nocsy.mcpets.data.editor;

import com.ticxo.modelengine.api.ModelEngineAPI;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.utils.PetAnnouncement;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.Skill;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum EditorExpectationType {

    // General expectations
    BOOLEAN("boolean"),
    INT("integer"),
    POSITIVE_INT("positive_integer"),
    INVENTORY_SIZE("inventory_size"),
    FLOAT("float"),
    POSITIVE_FLOAT("positive_float"),
    STRING("string"),
    STRING_LIST("string_list"),
    ITEM_SECTION_ID("section_id"),

    // Restricted expectations
    PET_CREATE("pet_create"),
    PET_ID("pet id"),
    ANNOUNCEMENT_TYPE("announcement_type"),
    MYTHICMOB("mythicmob"),
    SKILL("skill"),
    MOUNT_TYPE("mount_type"),
    CATEGORY_ID("category_id"),
    CATEGORY_PET_LIST_ADD("pet_id"),
    CATEGORY_PET_LIST_REMOVE("pet_id"),
    PETFOOD_TYPE("petfood_type"),
    OPERATOR_TYPE("operator_type"),
    ITEM_ID_OR_MATERIAL("item_id_or_material"),
    PETFOOD_ID("petfood_id"),
    PETFOOD_PET_LIST_ADD("pet_id"),
    PETFOOD_PET_LIST_REMOVE("pet_id"),

    // Action expectations
    ITEM("item"),
    PAGE_SELECTOR("page_selector"),

    PET("pet"),
    PET_DELETE("pet_delete"),
    PET_LEVEL_EDIT("pet_level_edit"),
    PET_LEVEL_DELETE("pet_level_delete"),
    PET_SKIN_DELETE("pet_skin_delete"),
    PET_SKIN_EDIT("pet_level_edit"),
    PET_LEVEL_CREATE("pet_level_create"),
    PET_SKIN_CREATE("pet_skin_create"),

    CATEGORY_CREATE("category_create"),
    CATEGORY_DELETE("category_delete"),
    CATEGORY_EDIT("category_edit"),

    ITEM_CREATE("item_create"),
    ITEM_EDIT("item_edit"),
    ITEM_DELETE("item_delete"),

    PETFOOD_EDIT("petfood_edit"),
    PETFOOD_CREATE("petfood_create"),
    PETFOOD_DELETE("petfood_delete"),
    ;

    public static int ERROR_PARSE = -808757986;

    @Getter
    private String name;

    EditorExpectationType(String name)
    {
        this.name = name;
    }

    public Object parse(Object any)
    {
        if(this.equals(EditorExpectationType.STRING) ||
                this.equals(EditorExpectationType.MYTHICMOB) ||
                this.equals(EditorExpectationType.SKILL) ||
                this.equals(EditorExpectationType.PET_CREATE) ||
                this.equals(EditorExpectationType.PET_ID) ||
                this.equals(EditorExpectationType.CATEGORY_ID) ||
                this.equals(EditorExpectationType.CATEGORY_PET_LIST_ADD) ||
                this.equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE) ||
                this.equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                this.equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE) ||
                this.equals(EditorExpectationType.ITEM_SECTION_ID) ||
                this.equals(EditorExpectationType.PETFOOD_TYPE) ||
                this.equals(EditorExpectationType.OPERATOR_TYPE) ||
                this.equals(EditorExpectationType.ITEM_ID_OR_MATERIAL) ||
                this.equals(EditorExpectationType.PETFOOD_ID) ||
                this.equals(EditorExpectationType.MOUNT_TYPE))
            return any + "";
        else if((this.equals(EditorExpectationType.FLOAT))
                || this.equals(EditorExpectationType.POSITIVE_FLOAT))
        {
            float value = parseFloat(any + "");
            if(value == (float)ERROR_PARSE)
                return null;
            if(value < 0 && this.equals(EditorExpectationType.POSITIVE_FLOAT))
                return null;
            return value;
        }
        else if((this.equals(EditorExpectationType.INT))
                || this.equals(EditorExpectationType.POSITIVE_INT)
                || this.equals(EditorExpectationType.INVENTORY_SIZE))
        {
            int value = parseInt(any + "");
            if(value == ERROR_PARSE)
                return null;
            if(value < 0 && this.equals(EditorExpectationType.POSITIVE_INT))
                return null;
            if((value < 0 || value > 54) && this.equals(EditorExpectationType.INVENTORY_SIZE))
                return null;
            return value;
        }
        else if((this.equals(EditorExpectationType.STRING_LIST)))
        {
            String value = any + "";
            return List.of(value.split(","));
        }
        else if(this.equals(EditorExpectationType.ANNOUNCEMENT_TYPE))
        {
            PetAnnouncement announcement = Arrays.stream(PetAnnouncement.values()).filter(
                    petAnnouncement -> petAnnouncement.name().replace("_", " ").toUpperCase().equals((any + "").toUpperCase())
            ).findFirst().orElse(null);
            if(announcement == null)
                return null;
            else
                return announcement.name().toUpperCase();
        }
        return null;
    }

    public boolean matches(Object any)
    {
        if(this.equals(EditorExpectationType.STRING))
        {
            return true;
        }
        else if(this.equals(EditorExpectationType.INT))
        {
            try {
                Integer.parseInt(any + "");
                return true;
            } catch (NumberFormatException ex)
            {
                return false;
            }
        }
        else if(this.equals(EditorExpectationType.FLOAT))
        {
            try {
                Float.parseFloat(any + "");
                return true;
            } catch (NumberFormatException ex)
            {
                return false;
            }
        }
        else if(this.equals(EditorExpectationType.STRING_LIST))
        {
            return true;
        }
        else if(this.equals(EditorExpectationType.PET_ID) ||
                this.equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                this.equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE) ||
                this.equals(EditorExpectationType.CATEGORY_PET_LIST_ADD)||
                this.equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE))
        {
            Pet pet = Pet.getFromId(any + "");
            return pet != null;
        }
        else if(this.equals(EditorExpectationType.PET_CREATE))
        {
            Pet pet = Pet.getFromId(any + "");
            // We dont want any pet to exist with that id
            return pet == null;
        }
        else if(this.equals(EditorExpectationType.SKILL))
        {
            Optional<Skill> optional = MCPets.getMythicMobs().getSkillManager().getSkill(any + "");
            // Check if the skill exists
            return optional.isPresent();
        }
        else if(this.equals(EditorExpectationType.MYTHICMOB))
        {
            Optional<MythicMob> optional = MCPets.getMythicMobs().getMobManager().getMythicMob(any + "");
            // Check if the mythicmob exists
            return optional.isPresent();
        }
        else if(this.equals(EditorExpectationType.MOUNT_TYPE))
        {
            return ModelEngineAPI.getControllerRegistry().get(any + "") != null;
        }
        else if(this.equals(EditorExpectationType.CATEGORY_ID))
        {
            Category cat = Category.getFromId(any + "");
            return cat != null;
        }
        else if(this.equals(EditorExpectationType.PETFOOD_TYPE))
        {
            PetFoodType type = Arrays.stream(PetFoodType.values()).filter(petFoodType -> petFoodType.getType().equalsIgnoreCase(any + "")).findFirst().orElse(null);
            return type != null;
        }
        else if(this.equals(EditorExpectationType.OPERATOR_TYPE))
        {
            return PetMath.get(any + "") != null;
        }
        else if(this.equals(EditorExpectationType.ITEM_ID_OR_MATERIAL))
        {
            boolean value = ItemsListConfig.getInstance().getItems().containsKey(any + "");
            if(!value)
                value = Arrays.stream(Material.values()).anyMatch(mat -> mat.name().equalsIgnoreCase(any + ""));
            return value;
        }
        try
        {
            return this.parse(any) != null;
        } catch (Exception ex)
        {
            return false;
        }
    }

    public int parseInt(String entry)
    {
        if(Utils.isNumeric(entry))
        {
            try
            {
                return Integer.parseInt(entry);
            }
            catch (NumberFormatException ex)
            {
                return ERROR_PARSE;
            }
        }
        return ERROR_PARSE;
    }

    public float parseFloat(String entry)
    {
        if(Utils.isNumeric(entry))
        {
            try
            {
                return Float.parseFloat(entry);
            }
            catch (NumberFormatException ex)
            {
                return ERROR_PARSE;
            }
        }
        return ERROR_PARSE;
    }

}
