package com.floyd.core.i18n;

import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author floyd
 */
@Configuration
public class I18nConfiguration implements ApplicationContextAware {

    private static final Logger logger = ConsoleLoggerFactory.get(I18nConfiguration.class);

    @Bean
    public DefaultI18nMessageProvider i18nMessageProvider(I18nSettingManager settingsManager) {
        return new DefaultI18nMessageProvider(settingsManager);
    }

    @Bean
    public I18nSettingManager i18nSettingManager(List<I18nMessageHolder> i18nMessageHolders) {
        return new I18nSettingManagerImpl(i18nMessageHolders);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LocaleMessage.setI18nMessageProvider(applicationContext.getBean(DefaultI18nMessageProvider.class));
    }
}
