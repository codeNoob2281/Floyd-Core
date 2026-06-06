package com.floyd.core.command;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ParameterResolver unit tests
 */
class ParameterResolverTest {

    @Test
    void testResolveCommandSender() throws Exception {
        Method method = TestHandler.class.getMethod("handler1", CommandSender.class);
        List<ParameterBinding> bindings = ParameterResolver.resolve(method);

        assertEquals(1, bindings.size());
        assertEquals(ParameterBinding.BindingType.SENDER, bindings.get(0).getBindingType());
        assertEquals(0, bindings.get(0).getParameterIndex());
    }

    @Test
    void testResolveRawArgs() throws Exception {
        Method method = TestHandler.class.getMethod("handler2", CommandSender.class, String[].class);
        List<ParameterBinding> bindings = ParameterResolver.resolve(method);

        assertEquals(2, bindings.size());
        assertEquals(ParameterBinding.BindingType.SENDER, bindings.get(0).getBindingType());
        assertEquals(ParameterBinding.BindingType.RAW_ARGS, bindings.get(1).getBindingType());
    }

    @Test
    void testResolveSubCommandParam() throws Exception {
        Method method = TestHandler.class.getMethod("handler3", CommandSender.class, String.class, Integer.class);
        List<ParameterBinding> bindings = ParameterResolver.resolve(method);

        assertEquals(3, bindings.size());
        assertEquals(ParameterBinding.BindingType.SENDER, bindings.get(0).getBindingType());

        ParameterBinding param1 = bindings.get(1);
        assertEquals(ParameterBinding.BindingType.SINGLE_PARAM, param1.getBindingType());
        assertEquals(0, param1.getArgIndex());
        assertEquals(String.class, param1.getTargetType());
        assertEquals("目标玩家", param1.getDescription());
        assertTrue(param1.isRequired());

        ParameterBinding param2 = bindings.get(2);
        assertEquals(ParameterBinding.BindingType.SINGLE_PARAM, param2.getBindingType());
        assertEquals(1, param2.getArgIndex());
        assertEquals(Integer.class, param2.getTargetType());
        assertEquals("等级", param2.getDescription());
        assertFalse(param2.isRequired());
        assertEquals("1", param2.getDefaultValue());
    }

    @Test
    void testResolveSubCommandBody() throws Exception {
        Method method = TestHandler.class.getMethod("handler4", CommandSender.class, TestBody.class);
        List<ParameterBinding> bindings = ParameterResolver.resolve(method);

        assertEquals(2, bindings.size());
        assertEquals(ParameterBinding.BindingType.SENDER, bindings.get(0).getBindingType());

        ParameterBinding bodyBinding = bindings.get(1);
        assertEquals(ParameterBinding.BindingType.BODY, bodyBinding.getBindingType());
        assertEquals(TestBody.class, bodyBinding.getBodyType());
    }

    @Test
    void testResolveMixed() throws Exception {
        Method method = TestHandler.class.getMethod("handler5", CommandSender.class, String.class, TestBody.class, String[].class);
        List<ParameterBinding> bindings = ParameterResolver.resolve(method);

        assertEquals(4, bindings.size());
        assertEquals(ParameterBinding.BindingType.SENDER, bindings.get(0).getBindingType());
        assertEquals(ParameterBinding.BindingType.SINGLE_PARAM, bindings.get(1).getBindingType());
        assertEquals(ParameterBinding.BindingType.BODY, bindings.get(2).getBindingType());
        assertEquals(ParameterBinding.BindingType.RAW_ARGS, bindings.get(3).getBindingType());
    }

    @Test
    void testResolveUnsupportedType() throws Exception {
        Method method = TestHandler.class.getMethod("handler6", CommandSender.class, Integer.class);
        assertThrows(IllegalArgumentException.class, () ->
                ParameterResolver.resolve(method));
    }

    // Test handler class
    public static class TestHandler {
        public void handler1(CommandSender sender) {}

        public void handler2(CommandSender sender, String[] args) {}

        public void handler3(
                CommandSender sender,
                @SubCommandParam(index = 0, description = "目标玩家") String targetName,
                @SubCommandParam(index = 1, description = "等级", required = false, defaultValue = "1") Integer level) {}

        public void handler4(CommandSender sender, @SubCommandBody TestBody body) {}

        public void handler5(
                CommandSender sender,
                @SubCommandParam(index = 0) String name,
                @SubCommandBody TestBody body,
                String[] args) {}

        public void handler6(CommandSender sender, Integer unsupported) {}
    }

    // Test Body class
    public static class TestBody {
        @SubCommandParam(index = 0, description = "名称")
        private String name;

        @SubCommandParam(index = 1, description = "数量", required = false, defaultValue = "1")
        private Integer amount;
    }
}
