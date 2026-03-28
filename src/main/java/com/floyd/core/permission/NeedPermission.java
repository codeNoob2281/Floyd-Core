package com.floyd.core.permission;

import java.lang.annotation.*;

/**
 * @author floyd
 * @date 2026/3/28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedPermission {

    /**
     * 权限标识，例如：floyd-backpack.clear
     *
     * @return
     */
    String value();
}
