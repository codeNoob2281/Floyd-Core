package com.floyd.core.command;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author floyd
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SubCommandHandler {

    /**
     * The root command, eg. {@code @CommandMethodHandler(rootCommand = "backpack")}
     *
     * @return the root command
     */
    String rootCommand();
}
