package dev.dhyces.trimmed.api.services;

import java.util.ServiceLoader;

public final class ApiServices {
    private ApiServices() {}

    public static final ModelHelper MODEL_HELPER = loadService(ModelHelper.class);
    public static final InternalCodecs INTERNAL_CODECS = loadService(InternalCodecs.class);

    static <T> T loadService(Class<T> t) {
        return ServiceLoader.load(t).findFirst().orElseThrow(() -> new RuntimeException("Must have mod dependency enabled. Api cannot work without an implementation."));
    }
}
