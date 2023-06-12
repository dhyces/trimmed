package dhyces.trimmed.impl.client.maps.manager.delegates;

import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.manager.BaseMapHandler;
import dhyces.trimmed.impl.util.ImmutableMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Function;

/**
 * Primary purpose of this class is to allow users of this API to map values from maps in a simple and easy way.
 * Users don't need to worry about keeping up to date with the current state of the map, as these handle syncing
 */
public abstract class BaseMapDelegate<K, V> implements ImmutableMap<K, V>, BaseMapHandler.MapLoadListener<K> {

    protected final Function<String, DataResult<V>> mappingFunction;

    public BaseMapDelegate(Function<String, DataResult<V>> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @ApiStatus.Internal
    public void onReload(Map<K, MapValue> underlyingMap) {
        for (Entry<K, MapValue> entry : underlyingMap.entrySet()) {
            DataResult<V> mapResult = map(entry.getValue().value());
            // TODO: handle cases in which there should be an irrecoverable error
            mapResult.resultOrPartial(Trimmed.LOGGER::error).ifPresent(v -> onMapped(entry.getKey(), v));
        }
    }

    protected abstract void onMapped(K key, V mappedValue);

    protected final DataResult<V> map(String val) {
        try {
            return mappingFunction.apply(val);
        } catch (Exception e) {
            return DataResult.error(e::getMessage);
        }
    }
}
