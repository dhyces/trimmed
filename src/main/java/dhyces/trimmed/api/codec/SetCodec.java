package dhyces.trimmed.api.codec;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Do not use for serialization, does not preserve order and will cause the cache to update every time datagen is run
 */
public class SetCodec<A> implements Codec<Set<A>> {
    private final Codec<A> elementCodec;

    public SetCodec(Codec<A> codec) {
        this.elementCodec = codec;
    }

    @Override
    public <T> DataResult<Pair<Set<A>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(consumerConsumer -> {
            ImmutableSet.Builder<A> builder = new ImmutableSet.Builder<>();
            Stream.Builder<T> failed = Stream.builder();
            AtomicReference<DataResult<Unit>> ref = new AtomicReference<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            consumerConsumer.accept(t -> {
                DataResult<Pair<A, T>> result = elementCodec.decode(ops, t);
                result.error().ifPresent(e -> failed.add(t));
                ref.setPlain(ref.getPlain().apply2stable((unit, o) -> {
                    builder.add(o.getFirst());
                    return unit;
                }, result));
            });

            Pair<Set<A>, T> pair = Pair.of(builder.build(), ops.createList(failed.build()));
            return ref.getPlain().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public <T> DataResult<T> encode(Set<A> input, DynamicOps<T> ops, T prefix) {
        ListBuilder<T> builder = ops.listBuilder();

        for (A a : input) {
            builder.add(elementCodec.encodeStart(ops, a));
        }

        return builder.build(prefix);
    }
}