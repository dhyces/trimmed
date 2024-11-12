package dev.dhyces.trimmed.api.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.BaseMapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class LenientUnboundedMapCodec<K, V> implements BaseMapCodec<K, V>, Codec<Map<K, V>> {

    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    public LenientUnboundedMapCodec(Codec<K> keyCodec, Codec<V> elementCodec) {
        this.keyCodec = keyCodec;
        this.valueCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(tMapLike -> decode(ops, tMapLike)).map(kvMap -> Pair.of(kvMap, input));
    }

    public <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        Map<K, V> successful = new Object2ObjectOpenHashMap<>();
        Stream.Builder<Pair<T, T>> errors = Stream.builder();

        DataResult<Unit> result = input.entries().reduce(
                DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
                (dataResult, ttPair) -> {
                    DataResult<K> keyResult = keyCodec.parse(ops, ttPair.getFirst());
                    DataResult<V> valueResult = valueCodec.parse(ops, ttPair.getSecond());

                    DataResult<Pair<K, V>> entry = keyResult.apply2stable(Pair::of, valueResult);
                    Optional<Pair<K, V>> entryOptional = entry.resultOrPartial();
                    if (entryOptional.isPresent()) {
                        final V existingValue = successful.putIfAbsent(entryOptional.get().getFirst(), entryOptional.get().getSecond());
                        if (existingValue != null) {
                            errors.add(ttPair);
                            return dataResult.apply2stable((u, p) -> u, DataResult.error(() -> "Duplicate entry for key: '" + entryOptional.get().getFirst() + "'"));
                        }
                    }
                    if (entry.isError()) {
                        if (!valueResult.isSuccess() || !shouldSkipKey(keyResult, valueResult.getOrThrow())) {
                            errors.add(ttPair);
                        }
                    }
                    return dataResult.apply2stable((unit, o) -> unit, entry);
        }, (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2));

        Map<K, V> finished = Collections.unmodifiableMap(successful);

        return result.map(unit -> finished).setPartial(finished).mapError(s -> s + " missed input: " + ops.createMap(errors.build()));
    }

    public abstract boolean shouldSkipKey(DataResult<K> keyParse, V value);

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public Codec<K> keyCodec() {
        return keyCodec;
    }

    @Override
    public Codec<V> elementCodec() {
        return valueCodec;
    }
}
