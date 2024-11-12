package dev.dhyces.trimmed;

import dev.dhyces.trimmed.modhelper.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimmed {
    public static final Logger LOGGER = LoggerFactory.getLogger("Trimmed");

    public static void init() {}

    public static void logInDev(String str) {
        if (!Services.PLATFORM_HELPER.isProduction()) {
            LOGGER.info(str);
        }
    }
}
