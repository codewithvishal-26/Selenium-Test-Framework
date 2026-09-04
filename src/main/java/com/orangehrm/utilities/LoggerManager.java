package com.orangehrm.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggerManager {

    private LoggerManager() {
        // Utility class - prevent object creation
    }

    /**
     * Returns a logger associated with the requested class.
     */
    public static Logger getLogger(Class<?> clazz) {

        return LogManager.getLogger(clazz);
    }
}