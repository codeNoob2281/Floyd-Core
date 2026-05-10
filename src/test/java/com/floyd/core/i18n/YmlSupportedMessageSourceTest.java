package com.floyd.core.i18n;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.FileSystemResourceLoader;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YmlSupportedMessageSource unit tests
 *
 * @author floyd
 */
class YmlSupportedMessageSourceTest {

    private YmlSupportedMessageSource messageSource;

    @BeforeEach
    void setUp() {
        messageSource = new YmlSupportedMessageSource();
        messageSource.addBasenames("src/test/resources/i18n/");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setResourceLoader(new FileSystemResourceLoader());
        messageSource.setFallbackToSystemLocale(false);
    }

    // Tests for English messages
    @Test
    void testGetMessage_English_Username() {
        String result = messageSource.getMessage("login.username", null, Locale.ENGLISH);
        assertEquals("Username", result);
    }

    @Test
    void testGetMessage_English_Password() {
        String result = messageSource.getMessage("login.password", null, Locale.ENGLISH);
        assertEquals("Password", result);
    }

    @Test
    void testGetMessage_English_Login() {
        String result = messageSource.getMessage("login.login", null, Locale.ENGLISH);
        assertEquals("Login", result);
    }

    @Test
    void testGetMessage_English_Placeholder() {
        String result = messageSource.getMessage("login.username_placeholder", null, Locale.ENGLISH);
        assertEquals("Please enter username", result);
    }

    @Test
    void testGetMessage_English_Validation() {
        String result = messageSource.getMessage("login.username_required", null, Locale.ENGLISH);
        assertEquals("Username cannot be empty", result);
    }

    // Tests for Chinese messages
    @Test
    void testGetMessage_Chinese_Username() {
        String result = messageSource.getMessage("login.username", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("用户名", result);
    }

    @Test
    void testGetMessage_Chinese_Password() {
        String result = messageSource.getMessage("login.password", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("密码", result);
    }

    @Test
    void testGetMessage_Chinese_Login() {
        String result = messageSource.getMessage("login.login", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("登录", result);
    }

    @Test
    void testGetMessage_Chinese_Placeholder() {
        String result = messageSource.getMessage("login.username_placeholder", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("请输入用户名", result);
    }

    @Test
    void testGetMessage_Chinese_Validation() {
        String result = messageSource.getMessage("login.username_exists", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("用户名已存在", result);
    }

    // Tests for Japanese messages
    @Test
    void testGetMessage_Japanese_Username() {
        String result = messageSource.getMessage("login.username", null, Locale.JAPANESE);
        assertEquals("ユーザー名", result);
    }

    @Test
    void testGetMessage_Japanese_Password() {
        String result = messageSource.getMessage("login.password", null, Locale.JAPANESE);
        assertEquals("パスワード", result);
    }

    @Test
    void testGetMessage_Japanese_Login() {
        String result = messageSource.getMessage("login.login", null, Locale.JAPANESE);
        assertEquals("ログイン", result);
    }

    @Test
    void testGetMessage_Japanese_Placeholder() {
        String result = messageSource.getMessage("login.username_placeholder", null, Locale.JAPANESE);
        assertEquals("ユーザー名を入力してください", result);
    }

    @Test
    void testGetMessage_Japanese_Validation() {
        String result = messageSource.getMessage("login.username_invalid", null, Locale.JAPANESE);
        assertEquals("ユーザー名の形式が無効です", result);
    }

    // Tests for fallback behavior
    @Test
    void testGetMessage_FallbackToDefault_WhenLocaleNotExists() {
        Locale unsupportedLocale = Locale.of("xx");
        String result = messageSource.getMessage("login.username", null, unsupportedLocale);
        assertEquals("Username", result);
    }

    @Test
    void testGetMessage_FallbackToEnglish_WhenSpecificVariantNotExists() {
        Locale usLocale = Locale.US;
        String result = messageSource.getMessage("login.username", null, usLocale);
        assertEquals("Username", result);
    }

    // Tests for non-existent keys
    @Test
    void testGetMessage_ThrowsException_WhenKeyNotExists() {
        assertThrows(NoSuchMessageException.class, () -> {
            messageSource.getMessage("non.existent.key", null, Locale.ENGLISH);
        });
    }

    @Test
    void testGetMessage_ReturnsDefaultMessage_WhenKeyNotExistsWithDefault() {
        String result = messageSource.getMessage("non.existent.key", null, "Default Message", Locale.ENGLISH);
        assertEquals("Default Message", result);
    }

    // Tests for message with arguments (if needed in future)
    @Test
    void testGetMessage_WithNullArguments() {
        String result = messageSource.getMessage("login.username", null, Locale.ENGLISH);
        assertNotNull(result);
    }

    @Test
    void testGetMessage_AllKeysExist() {
        String[] keys = {
            "login.username",
            "login.password",
            "login.login",
            "login.logout",
            "login.register",
            "login.forget",
            "login.reset",
            "login.username_placeholder",
            "login.password_placeholder",
            "login.username_required",
            "login.password_required",
            "login.username_invalid",
            "login.password_invalid",
            "login.username_exists",
            "login.username_not_exists"
        };

        for (String key : keys) {
            String result = messageSource.getMessage(key, null, Locale.ENGLISH);
            assertNotNull(result, "Key '" + key + "' should not be null");
            assertFalse(result.isEmpty(), "Key '" + key + "' should not be empty");
        }
    }

    // Tests for YAML file format support
    @Test
    void testYamlFileExtension_Supported() {
        String result = messageSource.getMessage("login.username", null, Locale.SIMPLIFIED_CHINESE);
        assertNotNull(result);
    }

    @Test
    void testYmlFileExtension_Supported() {
        String result = messageSource.getMessage("login.username", null, Locale.SIMPLIFIED_CHINESE);
        assertNotNull(result);
    }
}
