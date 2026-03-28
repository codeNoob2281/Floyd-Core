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
     * 权限标识，例如：floyd-backpack.clear
     *
     * @return
     */
    String value();

    /**
     * 权限提示信息
     *
     * @return
     */
    String message() default "you don't have permission to execute this command.";

    /**
     * 是否提示权限标识，默认为false
     *
     * @return
     */
    boolean tipPermValue() default false;
}
