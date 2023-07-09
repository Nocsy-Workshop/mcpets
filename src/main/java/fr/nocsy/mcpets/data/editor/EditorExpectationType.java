package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;

public enum EditorExpectationType {

    INT("integer"),
    FLOAT("float"),
    STRING("string"),
    MYTHICMOB("mythicmob"),
    BOOLEAN("boolean");

    public static int ERROR_PARSE = -808757986;

    @Getter
    private String name;

    EditorExpectationType(String name)
    {
        this.name = name;
    }

    public Object parse(Object any)
    {
        if(name.equalsIgnoreCase("string"))
            return any + "";
        else if(name.equalsIgnoreCase("float"))
        {
            float value = parseFloat(any + "");
            if(value == (float)ERROR_PARSE)
                return null;
            return value;
        }
        else if(name.equalsIgnoreCase("integer"))
        {
            int value = parseInt(any + "");
            if(value == ERROR_PARSE)
                return null;
            return value;
        }
        return null;
    }

    public boolean matches(Object any)
    {
        if(name.equalsIgnoreCase("string"))
        {
            return true;
        }
        else if(name.equalsIgnoreCase("integer"))
        {
            try {
                Integer.parseInt(any + "");
                return true;
            } catch (NumberFormatException ex)
            {
                return false;
            }
        }
        else if(name.equalsIgnoreCase("float"))
        {
            try {
                Float.parseFloat(any + "");
                return true;
            } catch (NumberFormatException ex)
            {
                return false;
            }
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
