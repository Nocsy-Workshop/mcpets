package fr.nocsy.mcpets.data.editor;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Editor {

    @Getter
    private static HashMap<UUID, Editor> editors = new HashMap<>();

    @Getter
    private Player player;
    @Getter
    private EditorState state;


    private Editor(Player p)
    {
        this.player = p;
        this.state = EditorState.GLOBAL_EDITOR;

        editors.put(p.getUniqueId(), this);
    }

    public static Editor getEditor(Player p)
    {
        if(editors.containsKey(p.getUniqueId()))
            return editors.get(p.getUniqueId());
        return new Editor(p);
    }

    public static void refreshEditor(Player p)
    {
        editors.remove(p.getUniqueId());
    }

    public void setState(EditorState state)
    {
        this.state = state;
    }

    public void openEditor()
    {
        state.openView(player);
    }

}
