package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorEditing {

    private static HashMap<UUID, EditorEditing> editing = new HashMap<>();

    @Getter
    @Setter
    private String petId;
    @Getter
    @Setter
    private String mappedId;

    @Getter
    private HashMap<Integer, String> editorMapping = new HashMap<>();

    public static EditorEditing get(Player p)
    {
        if(!editing.containsKey(p.getUniqueId()))
            editing.put(p.getUniqueId(), new EditorEditing());
        return editing.get(p.getUniqueId());
    }

}
