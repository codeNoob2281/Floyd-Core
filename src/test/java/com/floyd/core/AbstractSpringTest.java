package com.floyd.core;

import com.floyd.core.spring.SpringTestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Paths;

import static org.mockito.Mockito.mockStatic;

/**
 * @author floyd
 */
public abstract class AbstractSpringTest {

    static {
        ClassPathResource resource = new ClassPathResource("config.yml");
        String folder = "";
        try {
            folder = resource.getFile().getParent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mockStatic(FloydPlugin.class)
                .when(FloydPlugin::getPluginDataPath)
                .thenReturn(Paths.get(folder));
    }

    protected AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTestConfig.class);

}
