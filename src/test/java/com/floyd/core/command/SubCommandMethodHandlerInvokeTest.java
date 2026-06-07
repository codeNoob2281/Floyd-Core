package com.floyd.core.command;

import com.floyd.core.command.param.SubCommandBody;
import com.floyd.core.command.param.SubCommandParam;
import com.floyd.core.logging.ConsoleLogger;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SubCommandMethodHandler full invocation chain tests
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SubCommandMethodHandlerInvokeTest {

    @Mock
    private CommandSender sender;

    @BeforeAll
    static void setup() {
        ConsoleLogger.initializeFirst(java.util.logging.Logger.getGlobal(), null);
    }

    @Test
    void testInvokeWithSubCommandParam() throws Exception {
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("setLevel", CommandSender.class, String.class, Integer.class);
        SubCommandMethodHandler methodHandler = new SubCommandMethodHandler(handler, method, new String[]{"set-level"}, "");

        when(sender.hasPermission(anyString())).thenReturn(true);

        SubCommandInvokeResult result = methodHandler.invoke(sender, new String[]{"Steve", "10"});

        assertTrue(result.isCommandValid());
        assertEquals("Steve", handler.lastTargetName);
        assertEquals(10, handler.lastLevel);
    }

    @Test
    void testInvokeWithMissingRequiredParam() throws Exception {
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("setLevel", CommandSender.class, String.class, Integer.class);
        SubCommandMethodHandler methodHandler = new SubCommandMethodHandler(handler, method, new String[]{"set-level"}, "");

        when(sender.hasPermission(anyString())).thenReturn(true);

        SubCommandInvokeResult result = methodHandler.invoke(sender, new String[]{});

        assertTrue(result.isCommandValid());
        verify(sender).sendMessage(any(net.kyori.adventure.text.Component.class));
    }

    @Test
    void testInvokeWithInvalidTypeConversion() throws Exception {
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("setLevel", CommandSender.class, String.class, Integer.class);
        SubCommandMethodHandler methodHandler = new SubCommandMethodHandler(handler, method, new String[]{"set-level"}, "");

        when(sender.hasPermission(anyString())).thenReturn(true);

        SubCommandInvokeResult result = methodHandler.invoke(sender, new String[]{"Steve", "abc"});

        assertTrue(result.isCommandValid());
        verify(sender).sendMessage(any(net.kyori.adventure.text.Component.class));
    }

    @Test
    void testInvokeWithOptionalParam() throws Exception {
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("give", CommandSender.class, String.class, Integer.class);
        SubCommandMethodHandler methodHandler = new SubCommandMethodHandler(handler, method, new String[]{"give"}, "");

        when(sender.hasPermission(anyString())).thenReturn(true);

        SubCommandInvokeResult result = methodHandler.invoke(sender, new String[]{"diamond_sword"});

        assertTrue(result.isCommandValid());
        assertEquals("diamond_sword", handler.lastItemId);
        assertEquals(1, handler.lastAmount); // 默认值
    }

    @Test
    void testInvokeWithSubCommandBody() throws Exception {
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("teleport", CommandSender.class, TeleportParam.class);
        SubCommandMethodHandler methodHandler = new SubCommandMethodHandler(handler, method, new String[]{"teleport"}, "");

        when(sender.hasPermission(anyString())).thenReturn(true);

        SubCommandInvokeResult result = methodHandler.invoke(sender, new String[]{"Steve", "100", "64", "200"});

        assertTrue(result.isCommandValid());
        assertNotNull(handler.lastTeleportParam);
        assertEquals("Steve", handler.lastTeleportParam.getTargetName());
        assertEquals(100, handler.lastTeleportParam.getX());
        assertEquals(64, handler.lastTeleportParam.getY());
        assertEquals(200, handler.lastTeleportParam.getZ());
    }

    @Test
    void testInvokeWithRawArgs() throws Exception {
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("legacy", CommandSender.class, String[].class);
        SubCommandMethodHandler methodHandler = new SubCommandMethodHandler(handler, method, new String[]{"legacy"}, "");

        when(sender.hasPermission(anyString())).thenReturn(true);

        String[] args = new String[]{"arg1", "arg2"};
        SubCommandInvokeResult result = methodHandler.invoke(sender, args);

        assertTrue(result.isCommandValid());
        assertArrayEquals(args, handler.lastArgs);
    }

    @Test
    void testInvokeWithMixedParams() throws Exception {
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("mixed", CommandSender.class, String.class, String[].class);
        SubCommandMethodHandler methodHandler = new SubCommandMethodHandler(handler, method, new String[]{"mixed"}, "");

        when(sender.hasPermission(anyString())).thenReturn(true);

        SubCommandInvokeResult result = methodHandler.invoke(sender, new String[]{"hello", "extra1", "extra2"});

        assertTrue(result.isCommandValid());
        assertEquals("hello", handler.lastName);
        // RAW_ARGS receives the original args array
        assertEquals(3, handler.lastArgs.length);
    }

    // Test handler class
    public static class TestHandler {
        public String lastTargetName;
        public int lastLevel;
        public String lastItemId;
        public int lastAmount;
        public TeleportParam lastTeleportParam;
        public String[] lastArgs;
        public String lastName;

        public void setLevel(
                CommandSender sender,
                @SubCommandParam(index = 0, description = "目标玩家") String targetName,
                @SubCommandParam(index = 1, description = "等级") Integer level) {
            this.lastTargetName = targetName;
            this.lastLevel = level;
        }

        public void give(
                CommandSender sender,
                @SubCommandParam(index = 0, description = "物品ID") String itemId,
                @SubCommandParam(index = 1, description = "数量", required = false, defaultValue = "1") Integer amount) {
            this.lastItemId = itemId;
            this.lastAmount = amount;
        }

        public void teleport(CommandSender sender, @SubCommandBody TeleportParam param) {
            this.lastTeleportParam = param;
        }

        public void legacy(CommandSender sender, String[] args) {
            this.lastArgs = args;
        }

        public void mixed(
                CommandSender sender,
                @SubCommandParam(index = 0) String name,
                String[] args) {
            this.lastName = name;
            this.lastArgs = args;
        }
    }

    // Test Body class
    public static class TeleportParam {
        @SubCommandParam(index = 0, description = "目标玩家")
        private String targetName;

        @SubCommandParam(index = 1, description = "X坐标")
        private Integer x;

        @SubCommandParam(index = 2, description = "Y坐标")
        private Integer y;

        @SubCommandParam(index = 3, description = "Z坐标")
        private Integer z;

        public String getTargetName() { return targetName; }
        public Integer getX() { return x; }
        public Integer getY() { return y; }
        public Integer getZ() { return z; }
    }
}
