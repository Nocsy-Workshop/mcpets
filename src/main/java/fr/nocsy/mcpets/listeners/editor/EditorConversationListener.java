package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.editor.*;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.utils.PetAnnouncement;
import fr.nocsy.mcpets.utils.PetMath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditorConversationListener implements Listener {

    public void syncOpenEditor(Player p, EditorState newState)
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
                if(newState != null)
                    editor.setState(newState);
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
            syncOpenEditor(p, null);
            return;
        }

        // If the output type isn't respected, ask for another one.
        if(!conversation.entryMatch(entry))
        {
            if(conversation.getEditorItem().getType().equals(EditorExpectationType.MYTHICMOB))
            {
                p.sendMessage("§cmythicmob §e" + entry + "§c 不存在.请检查您的mythicmob文件以获取正确的ID.");
            }
            else if(conversation.getEditorItem().getType().equals(EditorExpectationType.SKILL))
            {
                p.sendMessage("§c技能 §e" + entry + "§c 不存在.请检查您的Mythicmob技能以获取正确的ID.");
            }
            else if(conversation.getEditorItem().getType().equals(EditorExpectationType.PET_CREATE))
            {
                p.sendMessage("§c宠物 §e" + entry + "§c 已经存在.请使用其他ID.");
            }
            else
            {
                p.sendMessage("§c该参数的预期类型为 §6" + conversation.getEditorItem().getType().getName().replace("_", " "));
                p.sendMessage("§c请使用正确的类型再次尝试.");
                if(conversation.getEditorItem().getType().equals(EditorExpectationType.ITEM_ID_OR_MATERIAL))
                {
                    p.sendMessage("§b可能的物品ID有:");
                    p.sendMessage("§7" + ItemsListConfig.getInstance().getItems().keySet());
                }
                else if(conversation.getEditorItem().getType().equals(EditorExpectationType.PET_ID) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD) ||
                        conversation.getEditorItem().getType().equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE))
                {
                    p.sendMessage("§b可能的宠物ID有:");
                    p.sendMessage("§7" + Pet.getObjectPets().stream().map(Pet::getId).collect(Collectors.toList()));
                }
                else if(conversation.getEditorItem().getType().equals(EditorExpectationType.OPERATOR_TYPE) )
                {
                    p.sendMessage("§b可能的运算符有:");
                    p.sendMessage("§7" + Arrays.toString(PetMath.values()));
                }
                else if(conversation.getEditorItem().getType().equals(EditorExpectationType.MOUNT_TYPE) )
                {
                    p.sendMessage("§b可能的骑乘类型有:");
                    p.sendMessage("§7[walking, flying]");
                }
                else if(conversation.getEditorItem().getType().equals(EditorExpectationType.ANNOUNCEMENT_TYPE))
                {
                    p.sendMessage("§b可能的公告类型有:");
                    p.sendMessage("§7" + Arrays.toString(PetAnnouncement.values()));
                }
                else if(conversation.getEditorItem().getType().equals(EditorExpectationType.PETFOOD_TYPE))
                {
                    p.sendMessage("§b可能的宠物食物类型有:");
                    p.sendMessage("§7" + Arrays.toString(PetFoodType.values()));
                }
            }
            return;
        }

        // This time we have the right value, let's parse it
        Object value = conversation.output(entry);

        // Fetch the edited field and save it
        EditorItems editorItem = conversation.getEditorItem();
        editorItem.setValue(value);
        editorItem.save(p);

        conversation.end();

        // If we just created a pet, we reload MCPets
        if(conversation.getEditorItem().getType().equals(EditorExpectationType.PET_CREATE))
        {
            Pet pet = Pet.getFromId(value.toString());
            if(pet == null)
                return;
            EditorEditing editing = EditorEditing.get(p);
            editing.setPetId(pet.getId());
            syncOpenEditor(p, EditorState.PET_EDITOR_EDIT);
            return;
        }

        // Open back the editor
        syncOpenEditor(p, null);
    }

}
