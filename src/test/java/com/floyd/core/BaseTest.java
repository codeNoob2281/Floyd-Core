package com.floyd.core;

import com.floyd.core.logging.ConsoleLogger;
import org.junit.jupiter.api.BeforeAll;

import java.util.logging.Logger;

/**
 * @author floyd
 */
public abstract class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        ConsoleLogger.initializeFirst(Logger.getGlobal(), null);
    }

}
