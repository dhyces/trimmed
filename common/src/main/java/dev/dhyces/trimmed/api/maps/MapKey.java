package dev.dhyces.trimmed.api.maps;

import com.google.common.base.Preconditions;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private final MapKey<K, V> baseKey;
    private final ResourceLocation id;

    private MapKey(MapType<K, V> mapType, @Nullable MapKey<K, V> baseKey, ResourceLocation id) {
        this.type = mapType;
        this.baseKey = baseKey;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> MapKey<K, V> baseKeyOf(MapType<K, V> mapType, ResourceLocation id) {
        return (MapKey<K, V>) INTERNER.intern(new MapKey<>(mapType, null, id));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> MapKey<K, V> fromBase(MapKey<K, V> baseKey, ResourceLocation id) {
        return (MapKey<K, V>) INTERNER.intern(new MapKey<>(baseKey.type, baseKey, id));
    }

    public static <K, V> MapKey<K, V> of(MapType<K, V> mapType, ResourceLocation id) {
        if (id.getPath().contains("/")) {
            int slashIndex = id.getPath().indexOf('/');
            return fromBase(MapKey.baseKeyOf(mapType, id.withPath(s -> s.substring(0, slashIndex))), id.withPath(s -> s.substring(slashIndex+1)));
        }
        return baseKeyOf(mapType, id);
    }

    public MapType<K, V> getType() {
        return type;
    }

    public ResourceLocation getMapId() {
        return id;
    }

    public MapKey<K, V> makeSubKey(ResourceLocation subId) {
        return fromBase(baseKey == null ? this : baseKey, subId);
    }

    public MapKey<K, V> getParentKey() {
        Preconditions.checkArgument(baseKey != null, "This map key is the base key");
        return fromBase(baseKey, id.withPath(s -> s.substring(0, s.lastIndexOf("/"))));
    }

    public MapKey<K, V> getBaseKey() {
        Preconditions.checkArgument(baseKey != null, "This map key is the base key");
        return baseKey;
    }

    public boolean isSubKey() {
        return baseKey != null;
    }

    public ResourceLocation compilePathAndIdNamespace() {
        if (baseKey == null) {
            return getMapId();
        }
        return id.withPrefix(baseKey.getMapId().getPath() + '/');
    }

    @Override
    public String toString() {
        return "MapKey[type: "+ type + ", id: " + compilePathAndIdNamespace() + "]";
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
