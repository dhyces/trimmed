package dhyces.modhelper.services;

import dhyces.modhelper.services.helpers.ClientHelper;
import dhyces.modhelper.services.helpers.NetworkHelper;
import dhyces.modhelper.services.helpers.PlatformHelper;

import java.util.ServiceLoader;

public final class Services {

    public static final PlatformHelper PLATFORM_HELPER = loadService(PlatformHelper.class);
    public static final NetworkHelper NETWORK_HELPER = loadService(NetworkHelper.class);
    public static final ClientHelper CLIENT_HELPER = loadService(ClientHelper.class);

    static <T> T loadService(Class<T> t) {
        return ServiceLoader.load(t).findFirst().orElseThrow();
    }
}
