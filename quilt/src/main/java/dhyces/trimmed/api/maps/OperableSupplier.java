package dhyces.trimmed.api.maps;

import java.util.function.Function;
import java.util.function.Supplier;

public interface OperableSupplier<T> extends Supplier<T> {
    default <V> V mapOrElse(Function<T, V> mapper, V otherValue) {
        if (get() == null) {
            return otherValue;
        }
        return mapper.apply(get());
    }
}
