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
        player.sendMessage("§a请在聊天中输入要为\"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\"设置的值.");

        if(editorItem.getType().equals(EditorExpectationType.STRING_LIST))
        {
            player.sendMessage("§e使用逗号§c, §e分隔不同的元素,就像这个例子一样:§6SPELL,SHIELD");
        }
        player.sendMessage("§a如果您不想再更改值,请输入§cQuit§a.");
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
        player.sendMessage("§c值保持不变.");
    }

    public void end()
    {
        conversations.remove(player.getUniqueId());
        player.sendMessage("§a\"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\"的值已成功更改!");
        player.sendMessage("§a不要忘记§n重新加载§aMCPets以使更改生效.");
    }

}
