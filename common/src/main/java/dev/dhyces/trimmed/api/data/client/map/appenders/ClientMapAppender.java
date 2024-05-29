package dev.dhyces.trimmed.api.data.client.map.appenders;

import dev.dhyces.trimmed.api.data.map.MapBuilder;
import dev.dhyces.trimmed.api.data.map.appenders.BaseMapAppender;

public class ClientMapAppender<K, V> extends BaseMapAppender<K, V> {
    public ClientMapAppender(MapBuilder<K, V> builder) {
        super(builder);
    }
}
