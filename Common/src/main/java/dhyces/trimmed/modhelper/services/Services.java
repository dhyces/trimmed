package dhyces.trimmed.modhelper.services;

import dhyces.trimmed.modhelper.services.helpers.ClientHelper;
import dhyces.trimmed.modhelper.services.helpers.PlatformHelper;

import java.util.ServiceLoader;

public final class Services {

    public static final PlatformHelper PLATFORM_HELPER = loadService(PlatformHelper.class);
    public static final ClientHelper CLIENT_HELPER = loadService(ClientHelper.class);

    static <T> T loadService(Class<T> t) {
        return ServiceLoader.load(t).findFirst().orElseThrow();
    }
}
