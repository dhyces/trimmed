package dhyces.modhelper.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Function;

public interface CommonPacket<T extends CommonPacket<T>> {
    void writeTo(FriendlyByteBuf buf);

    Type<?> getType();

    @FunctionalInterface
    interface Factory<T extends CommonPacket<T>> extends Function<FriendlyByteBuf, T> {
    }

    record Type<T extends CommonPacket<T>>(Class<T> clazzType, ResourceLocation id, Factory<T> factory) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Type<?> type = (Type<?>) o;
            return Objects.equals(clazzType, type.clazzType) && Objects.equals(id, type.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazzType, id);
        }
    }
}
