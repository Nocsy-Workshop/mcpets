package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Pet;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorPetEditing {

    private static HashMap<UUID, Pet> petediting = new HashMap<>();

    public static void register(Player p, Pet pet)
    {
        petediting.put(p.getUniqueId(), pet);
    }

    public static Pet get(Player p)
    {
        return petediting.get(p.getUniqueId());
    }

}
