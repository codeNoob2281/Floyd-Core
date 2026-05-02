package com.floyd.core.permission;

import com.floyd.core.util.StrUtil;
import org.bukkit.command.CommandSender;

/**
 * @author floyd
 */
public class PermissionUtil {

    public static boolean hasPermission(CommandSender commandSender, String permValue) {
        if (StrUtil.isBlank(permValue)) {
            return true;
        }
        return commandSender.hasPermission(permValue);
    }
}
