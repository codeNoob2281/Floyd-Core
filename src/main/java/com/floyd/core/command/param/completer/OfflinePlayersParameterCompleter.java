package com.floyd.core.command.param.completer;

import com.floyd.core.command.param.ParameterCompleter;
import com.floyd.core.command.param.ParameterCompleterFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author floyd
 */
@Component(ParameterCompleterFactory.OFFLINE_PLAYERS)
public class OfflinePlayersParameterCompleter implements ParameterCompleter {

    @Override
    public List<String> complete(CommandSender commandSender, String partial) {
        // todo Calling Bukkit.getOfflinePlayers() on the main thread is a major performance bottleneck because it loads all historical player profiles from disk/database.
        //  On large servers, this can cause severe lag spikes (TPS drops) or even freeze the server.
        //  Consider caching offline player names asynchronously
        String lowerPartial = partial.toLowerCase();
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .filter(name -> name != null && name.toLowerCase().startsWith(lowerPartial))
                .toList();
    }
}
