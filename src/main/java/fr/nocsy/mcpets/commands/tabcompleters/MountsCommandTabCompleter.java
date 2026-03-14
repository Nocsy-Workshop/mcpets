package fr.nocsy.mcpets.commands.tabcompleters;

import fr.nocsy.mcpets.data.Pet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MountsCommandTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender,
                                                @NotNull Command cmd,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        ArrayList<String> completed = new ArrayList<>();

        if (!(commandSender instanceof Player))
            return completed;

        Player p = (Player) commandSender;

        if (args.length == 1) {
            completed.add("menu");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("menu")) {
            completed.addAll(Pet.getActivePetsForOwner(p.getUniqueId())
                    .stream()
                    .filter(Pet::isMountable)
                    .map(Pet::getId)
                    .collect(Collectors.toList()));
        }

        Collections.sort(completed);

        String partial = args[args.length - 1].toLowerCase();
        return completed.stream()
                .filter(s -> s.toLowerCase().startsWith(partial))
                .collect(Collectors.toList());
    }
}
