package com.floyd.core;

import com.floyd.core.spring.SpringTestConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author floyd
 * @date 2026/3/28
 */
public abstract class AbstractSpringTest {

    protected AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTestConfig.class);

}
