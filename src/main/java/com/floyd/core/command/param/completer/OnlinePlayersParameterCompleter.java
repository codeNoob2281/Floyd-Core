package com.floyd.core.command.param.completer;

import com.floyd.core.command.param.ParameterCompleter;
import com.floyd.core.command.param.ParameterCompleterFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author floyd
 */
@Component(ParameterCompleterFactory.ONLINE_PLAYERS)
public class OnlinePlayersParameterCompleter implements ParameterCompleter {

    @Override
    public List<String> complete(CommandSender commandSender, String partial) {
        String lowerCasePartial = partial.toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(lowerCasePartial))
                .toList();
    }
}
