package com.floyd.core.permission;

import com.floyd.core.AbstractSpringTest;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

/**
 * @author floyd
 */
@ExtendWith(MockitoExtension.class)
public class PermissionAspectTest extends AbstractSpringTest {

    @Mock
    Player player;

    @Test
    public void testRequiredPermission() {
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
