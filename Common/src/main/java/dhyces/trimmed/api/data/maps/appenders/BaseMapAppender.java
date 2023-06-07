package dhyces.trimmed.api.data.maps.appenders;

import dhyces.trimmed.api.data.maps.MapBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class BaseMapAppender<K, V> {
    protected final MapBuilder builder;
    protected final Function<V, String> mappingFunction;

    protected <S extends BaseMapAppender<K, V>> S self() {
        return (S) this;
    }

    public BaseMapAppender(MapBuilder builder, Function<V, String> mappingFunction) {
        this.builder = builder;
        this.mappingFunction = mappingFunction;
    }

    public <S extends BaseMapAppender<K, V>> S put(ResourceLocation key, V value) {
        builder.put(key, mappingFunction.apply(value));
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S putOptional(ResourceLocation key, V value) {
        builder.putOptional(key, mappingFunction.apply(value));
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S append(ResourceLocation clientMapKey) {
        builder.append(clientMapKey);
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S append(K clientMapKey) {
        builder.append(keyToRL(clientMapKey));
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S appendOptional(ResourceLocation clientMapKey) {
        builder.appendOptional(clientMapKey);
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S appendOptional(K clientMapKey) {
        builder.appendOptional(keyToRL(clientMapKey));
        return self();
    }

    protected abstract ResourceLocation keyToRL(K key);

    public <S extends BaseMapAppender<K, V>> S replaces() {
        builder.setReplace(true);
        return self();
    }
}
