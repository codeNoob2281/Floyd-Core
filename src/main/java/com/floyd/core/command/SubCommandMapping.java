package com.floyd.core.command;

import java.lang.annotation.*;

/**
 * Sub command mapping annotation
 *
 * @author floyd
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SubCommandMapping {

    /**
     * the sub commands list, eg. {@code @SubCommandMapping(commands = {"reload"})} {@code @SubCommandMapping(commands = {"reload","confirm"})}
     *
     * @return the sub commands list
     */
    String[] commands() default {};

    /**
     * the permission of the sub command,eq. {@code @SubCommandMapping(commands = {"reload"}, permission = "fun.reload")}
     *
     * @return the permission of the sub command
     */
    String permission() default "";

    /**
     * whether is fallback
     *
     * @return whether is fallback
     */
    boolean isFallback() default false;
}
