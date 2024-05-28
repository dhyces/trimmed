package dev.dhyces.trimmed.api.maps;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class MapKey<K, V> {
    public static final MapCodec<MapKey<?, ?>> REGISTERED_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ClientMapTypes.CODEC.fieldOf("map_type").forGetter(MapKey::getType),
                    CodecUtil.TRIMMED_IDENTIFIER.fieldOf("map_key").forGetter(MapKey::getMapId)
            ).apply(instance, MapKey::of)
    );
    public static <K, V> Codec<MapKey<K, V>> codec(MapType<K, V> mapType) {
        return ResourceLocation.CODEC.xmap(resourceLocation -> MapKey.of(mapType, resourceLocation), MapKey::getMapId);
    }

    private static final Interner<MapKey<?, ?>> INTERNER = Interners.newWeakInterner();

    private final MapType<K, V> type;
    private final ResourceLocation id;
    private final boolean isSubKey;

    private MapKey(MapType<K, V> mapType, ResourceLocation id) {
        this.type = mapType;
        this.id = id;
        this.isSubKey = id.getPath().contains("/");
    }

    @SuppressWarnings("unchecked")
    public static <K, V> MapKey<K, V> of(MapType<K, V> mapType, ResourceLocation id) {
        return (MapKey<K, V>) INTERNER.intern(new MapKey<>(mapType, id));
    }

    public MapType<K, V> getType() {
        return type;
    }

    public ResourceLocation getMapId() {
        return id;
    }

    public MapKey<K, V> makeSubKey(String subId) {
        return of(getType(), getMapId().withSuffix("/" + subId));
    }

    public MapKey<K, V> makeSubKeyFromPath(ResourceLocation subId) {
        return makeSubKey(subId.getPath().replace(id.getPath(), ""));
    }

    public MapKey<K, V> getParentKey() {
        return of(type, id.withPath(s -> s.substring(0, s.lastIndexOf("/"))));
    }

    public MapKey<K, V> getBaseKey() {
        return of(type, id.withPath(s -> s.substring(0, s.indexOf("/"))));
    }

    public boolean isSubKey() {
        return isSubKey;
    }

    @Override
    public String toString() {
        return "MapKey[type: "+ type + ", id: " + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapKey<?, ?> mapKey = (MapKey<?, ?>) o;
        return Objects.equals(type, mapKey.type) && Objects.equals(id, mapKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
