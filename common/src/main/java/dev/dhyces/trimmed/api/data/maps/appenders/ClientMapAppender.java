package dev.dhyces.trimmed.api.data.maps.appenders;

import dev.dhyces.trimmed.api.data.maps.MapBuilder;

public class ClientMapAppender<K, V> extends BaseMapAppender<K, V> {
    public ClientMapAppender(MapBuilder<K, V> builder) {
        super(builder);
    }
}
