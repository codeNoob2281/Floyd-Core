package com.floyd.core.i18n;

import com.floyd.core.AbstractSpringTest;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author floyd
 */
public class I18nMessageProviderTest extends AbstractSpringTest {

    @Test
    public void test123() throws Exception {
        DefaultI18nMessageProvider i18nMessageProvider =
                (DefaultI18nMessageProvider) applicationContext.getBean(I18nMessageProvider.class);
        i18nMessageProvider.setCurrentLocale(Locale.CHINA);

        assertEquals("登出", UserLoginMsg.LOGOUT.content());
        assertEquals("test_not_configed message: 你好", UserLoginMsg.TEST_NOT_CONFIGED.content("你好"));
    }


}
