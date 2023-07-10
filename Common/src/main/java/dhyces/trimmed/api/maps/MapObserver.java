package dhyces.trimmed.api.maps;

import java.util.Map;

public interface MapObserver<K, V> {
    void onUpdated(Map<K, V> map);
}
