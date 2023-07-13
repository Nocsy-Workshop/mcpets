package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorEditing {

    private static HashMap<UUID, EditorEditing> editing = new HashMap<>();

    @Getter
    private Pet pet;
    @Getter
    @Setter
    private PetLevel level;
    @Getter
    @Setter
    private PetSkin skin;
    @Getter
    @Setter
    private Category category;
    @Getter
    @Setter
    private String itemId;

    @Getter
    private HashMap<Integer, PetLevel> editorPetLevelMapping = new HashMap<>();
    @Getter
    private HashMap<Integer, PetSkin> editorPetSkinMapping = new HashMap<>();
    @Getter
    private HashMap<Integer, Category> editorCategoryMapping = new HashMap<>();
    @Getter
    private HashMap<Integer, String> editorItemMapping = new HashMap<>();

    public EditorEditing(Pet pet)
    {
        this.pet = pet;
    }

    public static void register(Player p, Pet pet)
    {
        editing.put(p.getUniqueId(), new EditorEditing(pet));
    }

    public static EditorEditing get(Player p)
    {
        if(!editing.containsKey(p.getUniqueId()))
            editing.put(p.getUniqueId(), new EditorEditing(null));
        return editing.get(p.getUniqueId());
    }

}
