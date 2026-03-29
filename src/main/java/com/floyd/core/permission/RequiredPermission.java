package com.floyd.core.permission;

import java.lang.annotation.*;

/**
 * @author floyd
 * @date 2026/3/28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredPermission {

    /**
     * Permission value, for example: floyd-backpack.clear
     *
     * @return
     */
    String value();
    
    /**
     * Permission message
     *
     * @return
     */
    String message() default "you don't have permission to execute this command.";
    
    /**
     * Whether to show permission value, default is false
     *
     * @return
     */
    boolean tipPermValue() default false;
}
