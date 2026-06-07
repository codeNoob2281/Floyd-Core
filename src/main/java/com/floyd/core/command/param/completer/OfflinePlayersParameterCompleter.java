package com.floyd.core.command.param.completer;

import com.floyd.core.command.param.ParameterCompleter;
import com.floyd.core.command.param.ParameterCompleterFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author floyd
 */
@Component(ParameterCompleterFactory.OFFLINE_PLAYERS)
public class OfflinePlayersParameterCompleter implements ParameterCompleter {

    @Override
    public List<String> complete(CommandSender commandSender, String partial) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .filter(name -> name != null && name.startsWith(partial))
                .toList();
    }
}
