package com.floyd.core.permission;

import com.floyd.core.AbstractSpringTest;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.lang.reflect.Field;

/**
 * @author floyd
 * @date 2026/3/28
 */
@ExtendWith(MockitoExtension.class)
public class PermissionAspectTest extends AbstractSpringTest {

    @Mock
    Player player;

    @Test
    public void testNeedPermission() {
        when(player.hasPermission("floyd-plugin.demo")).thenReturn(false);
        doAnswer(invocation -> {
            net.kyori.adventure.text.Component message = (net.kyori.adventure.text.Component) invocation.getArguments()[0];
            Field contentField = message.getClass().getDeclaredField("content");
            contentField.setAccessible(true);
            System.out.println(contentField.get(message));
            return null;
        }).when(player).sendMessage((net.kyori.adventure.text.Component) any());
        DemoCommandExecutor executor = applicationContext.getBean(DemoCommandExecutor.class);
        executor.onCommand(player, null, null, null);
    }
}
