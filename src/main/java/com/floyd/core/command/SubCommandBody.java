package com.floyd.core.command;

import java.lang.annotation.*;

/**
 * Binds command arguments to a POJO object, where internal fields are bound via @SubCommandParam annotations.
 *
 * @author floyd
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommandBody {
}
