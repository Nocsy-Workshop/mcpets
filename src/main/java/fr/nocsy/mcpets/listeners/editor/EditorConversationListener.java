package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.editor.Editor;
import fr.nocsy.mcpets.data.editor.EditorConversation;
import fr.nocsy.mcpets.data.editor.EditorItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EditorConversationListener implements Listener {

    public void syncOpenEditor(Player p)
    {
        final UUID uuid = p.getUniqueId();
        // Run it sync otherwise it will not open
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null)
                    return;
                Editor editor = Editor.getEditor(player);
                editor.openEditor();
            }
        }.runTask(MCPets.getInstance());
    }

    @EventHandler
    public void listen_conversation(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();
        EditorConversation conversation = EditorConversation.getConversation(p);

        if(conversation == null)
            return;

        e.setCancelled(true);

        String entry = e.getMessage();

        // If they wanna abandon the process
        if(entry.equalsIgnoreCase("quit"))
        {
            conversation.quit();
            syncOpenEditor(p);
            return;
        }

        // If the output type isn't respected, ask for another one.
        if(!conversation.entryMatch(entry))
        {
            p.sendMessage("§cThe expected type for this parameter is §6" + conversation.getEditorItem().getType().getName());
            p.sendMessage("§7Please try again with the right type.");
            return;
        }

        // This time we have the right value, let's parse it
        Object value = conversation.output(entry);

        // Fetch the edited field and save it
        EditorItems editorItem = conversation.getEditorItem();
        editorItem.setValue(value);
        editorItem.save();

        conversation.end();

        // Open back the editor
        syncOpenEditor(p);

    }

}
