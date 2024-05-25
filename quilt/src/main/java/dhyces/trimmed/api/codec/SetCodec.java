package dhyces.trimmed.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class SetCodec<A> implements Codec<Set<A>> {
    private final Codec<A> elementCodec;

    public SetCodec(Codec<A> codec) {
        this.elementCodec = codec;
    }

    @Override
    public <T> DataResult<Pair<Set<A>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(consumerConsumer -> {
            Set<A> linkedSet = new ObjectLinkedOpenHashSet<>(); // Preserve order
            Stream.Builder<T> failed = Stream.builder();
            AtomicReference<DataResult<Unit>> ref = new AtomicReference<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            consumerConsumer.accept(t -> {
                DataResult<Pair<A, T>> result = elementCodec.decode(ops, t);
                result.error().ifPresent(e -> failed.add(t));
                ref.setPlain(ref.getPlain().apply2stable((unit, o) -> {
                    linkedSet.add(o.getFirst());
                    return unit;
                }, result));
            });

            Pair<Set<A>, T> pair = Pair.of(Collections.unmodifiableSet(linkedSet), ops.createList(failed.build()));
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