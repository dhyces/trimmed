package dhyces.trimmed.impl.client.maps.manager.delegates;

import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.util.ImmutableMap;

import java.util.Map;
import java.util.function.Function;

/**
 * Primary purpose of this class is to allow users of this API to map values from maps in a simple and easy way.
 * Users don't need to worry about keeping up to date with the current state of the map, as these handle syncing
 */
public abstract class BaseMapDelegate<K, V> implements ImmutableMap<K, V> {

    protected final Function<String, V> mappingFunction;

    BaseMapDelegate(Function<String, V> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    public static <K, V> HashMapDelegate<K, V> hash(Function<String, V> mappingFunction) {
        return new HashMapDelegate<>(mappingFunction);
    }

    public static <K, V> BiMapMapDelegate<K, V> biMap(Function<String, V> forwardMappingFunction, Function<String, K> inverseMappingFunction) {
        return new BiMapMapDelegate<>(forwardMappingFunction, inverseMappingFunction);
    }

    public static <K, V> LazyMapDelegate<K, V> lazy(Function<String, V> mappingFunction) {
        return new LazyMapDelegate<>(mappingFunction);
    }

    void onReload(Map<K, String> underlyingMap) {
        for (Map.Entry<K, String> entry : underlyingMap.entrySet()) {
            DataResult<V> mapResult = map(entry.getValue());
            mapResult.resultOrPartial(Trimmed.LOGGER::error).ifPresent(v -> onMapped(entry.getKey(), v));
        }
    }

    protected abstract void onMapped(K key, V mappedValue);

    protected final DataResult<V> map(String val) {
        try {
            return DataResult.success(mappingFunction.apply(val));
        } catch (Exception e) {
            return DataResult.error(e::getMessage);
        }
    }
}
