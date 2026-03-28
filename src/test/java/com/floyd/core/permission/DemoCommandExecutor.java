package com.floyd.core.permission;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

/**
 * @author floyd
 * @date 2026/3/28
 */
@Component
@Slf4j
public class DemoCommandExecutor implements CommandExecutor {


    @Override
    @RequiredPermission("floyd-plugin.demo")
    public boolean onCommand(CommandSender player, Command command,
                             String label, String[] args) {
        System.out.println("执行命令demo");
        return true;
    }
}
