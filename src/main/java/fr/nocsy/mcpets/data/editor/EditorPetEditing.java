package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorPetEditing {

    private static HashMap<UUID, EditorPetEditing> petediting = new HashMap<>();

    @Getter
    private Pet pet;
    @Getter
    @Setter
    private PetLevel level;
    @Getter
    @Setter
    private PetSkin skin;

    @Getter
    private HashMap<Integer, PetLevel> editorPetLevelMapping = new HashMap<>();
    @Getter
    private HashMap<Integer, PetSkin> editorPetSkinMapping = new HashMap<>();

    public EditorPetEditing(Pet pet)
    {
        this.pet = pet;
    }

    public static void register(Player p, Pet pet)
    {
        petediting.put(p.getUniqueId(), new EditorPetEditing(pet));
    }

    public static EditorPetEditing get(Player p)
    {
        return petediting.get(p.getUniqueId());
    }

}
