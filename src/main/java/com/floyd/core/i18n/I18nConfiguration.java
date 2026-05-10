package com.floyd.core.i18n;

import com.floyd.core.settings.PluginSettingsManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author floyd
 */
@Configuration
public class I18nConfiguration implements ApplicationContextAware {

    @Bean
    DefaultI18nMessageProvider i18nMessageProvider(PluginSettingsManager settingsManager) {
        return new DefaultI18nMessageProvider(settingsManager);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LocaleMessage.setI18nMessageProvider(applicationContext.getBean(DefaultI18nMessageProvider.class));
    }
}
