package com.floyd.core.command;

import java.lang.annotation.*;

/**
 * Binds a command argument at a specific position to a method parameter or POJO field.
 *
 * @author floyd
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommandParam {

    /**
     * The position of the argument in the command argument array (0-based).
     * Position is calculated from the first argument after the sub-command path.
     * For example, in /backpack set-level Steve 10, Steve has index=0 and 10 has index=1.
     */
    int index();

    /**
     * Parameter description used for error messages.
     * For example, "target player name", "level".
     */
    String description() default "";

    /**
     * Whether this parameter is required. Defaults to true.
     * When a required parameter is missing, a usage message is sent to the player.
     */
    boolean required() default true;

    /**
     * Default value when the parameter is optional (required=false) and missing.
     * Only meaningful for non-object type parameters.
     */
    String defaultValue() default "";
}
