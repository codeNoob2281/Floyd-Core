package com.floyd.core.command;

import lombok.Builder;
import lombok.Data;

/**
 * @author floyd
 */
@Data
@Builder
public class SubCommandInvokeResult {

    /**
     * Whether the command is valid
     */
    private boolean commandValid;
}
