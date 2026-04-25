package com.floyd.core;

import com.floyd.core.spring.SpringTestConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author floyd
 */
public abstract class AbstractSpringTest {

    protected AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTestConfig.class);

}
