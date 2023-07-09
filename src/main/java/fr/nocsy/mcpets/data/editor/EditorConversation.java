package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorConversation {

    private static HashMap<UUID, EditorConversation> conversations = new HashMap<>();

    @Getter
    private Player player;

    @Getter
    private EditorItems editorItem;

    public EditorConversation(Player p, EditorItems editorItem)
    {
        this.player = p;
        this.editorItem = editorItem;
    }

    public static EditorConversation getConversation(Player p)
    {
        return conversations.get(p.getUniqueId());
    }

    public void start()
    {
        conversations.put(player.getUniqueId(), this);
        player.sendMessage("§aPlease type in the chat the value you want to set for the \"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\".");

        if(editorItem.getType().equals(EditorExpectationType.STRING_LIST))
        {
            player.sendMessage("§eSeperate the different elements with a comma §c, §e like this example: §6SPELL,SHIELD");
        }
        player.sendMessage("§aType §cQuit§a if you don't want to change the value anymore.");
    }

    public boolean entryMatch(String entry)
    {
        return editorItem.getType().matches(entry);
    }

    public Object output(String entry)
    {
        return editorItem.getType().parse(entry);
    }

    public void quit()
    {
        conversations.remove(player.getUniqueId());
        player.sendMessage("§cThe value was left unchanged.");
    }

    public void end()
    {
        conversations.remove(player.getUniqueId());
        player.sendMessage("§aThe value for \"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\" was changed successfully!");
        player.sendMessage("§aDon't forget to §nreload§a MCPets for the changes to take effect.");
    }

}
