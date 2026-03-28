package com.floyd.core.logging;

import lombok.Data;

/**
 * @author floyd
 * @date 2026/3/28
 */
@Data
public class LogConfig {
    private boolean logFileEnabled;
    private String logFileName;
}
