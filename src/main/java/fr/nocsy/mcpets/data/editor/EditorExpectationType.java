package fr.nocsy.mcpets.data.editor;

import com.ticxo.modelengine.api.ModelEngineAPI;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.Skill;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

public enum EditorExpectationType {

    // General expectations
    BOOLEAN("boolean"),
    INT("integer"),
    FLOAT("float"),
    STRING("string"),
    STRING_LIST("string_list"),

    // Restricted expectations
    PET_CREATE("pet_create"),
    MYTHICMOB("mythicmob"),
    SKILL("skill"),
    MOUNT_TYPE("mount_type"),

    // Action expectations
    ITEM("item"),
    PAGE_SELECTOR("page_selector"),
    PET("pet"),
    PET_DELETE("pet_delete");

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
                this.equals(EditorExpectationType.PET_CREATE))
            return any + "";
        else if((this.equals(EditorExpectationType.FLOAT)))
        {
            float value = parseFloat(any + "");
            if(value == (float)ERROR_PARSE)
                return null;
            return value;
        }
        else if((this.equals(EditorExpectationType.INT)))
        {
            int value = parseInt(any + "");
            if(value == ERROR_PARSE)
                return null;
            return value;
        }
        else if((this.equals(EditorExpectationType.STRING_LIST)))
        {
            String value = any + "";
            return List.of(value.split(","));
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
        return false;
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
