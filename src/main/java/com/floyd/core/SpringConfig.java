package com.floyd.core;

import com.floyd.core.permission.PermissionAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author floyd
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true) // Enable AOP
public class SpringConfig {

    @Bean
    PermissionAspect permissionAspect() {
        return new PermissionAspect();
    }
}
