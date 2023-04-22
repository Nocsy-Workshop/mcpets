package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ArgumentClearStats extends AArgument {

    public ArgumentClearStats(CommandSender sender, String[] args)
    {
        super("clearStats", new int[]{3, 2}, sender, args);
    }

    @Override
    public boolean additionalConditions()
    {
        return sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        if(args.length == 2)
        {
            // Either it's a player clear
            String playerName = args[1];
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            if(player != null || !player.hasPlayedBefore())
            {
                PlayerData pd = PlayerData.get(player.getUniqueId());

                PetStats.remove(player.getUniqueId());
                Language.STATS_CLEARED.sendMessage(sender);
                pd.save();
                return;
            }
            else
            {
                // Or it's a pet clear
                String petId = args[1];
                Pet pet = Pet.getFromId(petId);
                if(pet != null)
                {
                    PetStats.remove(petId);
                    Language.STATS_CLEARED_FOR_PET.sendMessageFormated(sender, new FormatArg("%petId%", petId));
                    PlayerData.saveDB();
                    return;
                }

                // In that case it's not a pet clear so he probably failed to give a player name
                sender.sendMessage(Language.PLAYER_OR_PET_DOESNT_EXIST.getMessage());
                return;
            }
        }

        else if(args.length == 3)
        {
            String petId = args[2];
            Pet pet = Pet.getFromId(petId);
            if(pet != null)
            {
                String playerName = args[1];
                OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
                if(player != null || !player.hasPlayedBefore())
                {
                    // Start by loading the player data
                    PlayerData pd = PlayerData.get(player.getUniqueId());

                    PetStats.remove(petId, player.getUniqueId());
                    Language.STATS_CLEARED_FOR_PET_FOR_PLAYER.sendMessageFormated(sender, new FormatArg("%petId%", petId),
                            new FormatArg("%player%", playerName));

                    pd.save();
                    return;
                }
            }

            sender.sendMessage(Language.PLAYER_OR_PET_DOESNT_EXIST.getMessage());
            return;
        }

    }

}
