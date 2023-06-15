package dhyces.trimmed.api.data.maps.appenders;

import dhyces.trimmed.api.data.maps.MapBuilder;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientRegistryMapAppender<T, V> extends BaseMapAppender<ClientRegistryMapKey<T>, V> {
    private final ResourceKey<? extends Registry<T>> registryKey;

    public ClientRegistryMapAppender(MapBuilder builder, Function<V, String> mappingFunction, ResourceKey<? extends Registry<T>> registryKey) {
        super(builder, mappingFunction);
        this.registryKey = registryKey;
    }

    public <S extends ClientRegistryMapAppender<T, V>> S put(ResourceKey<T> key, V value) {
        if (!key.registry().equals(registryKey.location())) {
            throw new IllegalArgumentException("Key " + key.location() + " is not for registry " + registryKey + "!");
        }
        return put(key.location(), value);
    }

    public <S extends ClientRegistryMapAppender<T, V>> S putOptional(ResourceKey<T> key, V value) {
        if (!key.registry().equals(registryKey.location())) {
            throw new IllegalArgumentException("Key " + key.location() + " is not for registry " + registryKey + "!");
        }
        return putOptional(key.location(), value);
    }

    @Override
    protected ResourceLocation keyToRL(ClientRegistryMapKey<T> key) {
        return key.getMapId();
    }

    public static final class RegistryAware<T, V> extends ClientRegistryMapAppender<T, V> {
        private final Map<T, ResourceLocation> lookup;

        public RegistryAware(MapBuilder builder, Function<V, String> mappingFunction, ResourceKey<? extends Registry<T>> registryResourceKey, HolderLookup.Provider lookupProvider) {
            super(builder, mappingFunction, registryResourceKey);
            this.lookup = lookupProvider.lookupOrThrow(registryResourceKey).listElements().map(tReference -> Map.entry(tReference.value(), tReference.key().location())).collect(Util.toMap());
        }

        public RegistryAware<T, V> put(T key, V value) {
            put(lookup.get(key), value);
            return self();
        }

        public RegistryAware<T, V> put(Supplier<T> key, V value) {
            return put(key.get(), value);
        }

        public RegistryAware<T, V> putOptional(T key, V value) {
            putOptional(lookup.get(key), value);
            return self();
        }

        public RegistryAware<T, V> putOptional(Supplier<T> key, V value) {
            return putOptional(key.get(), value);
        }
    }
}
