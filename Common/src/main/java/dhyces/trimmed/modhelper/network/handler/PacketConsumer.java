package dhyces.trimmed.modhelper.network.handler;

import commonnetwork.networking.data.PacketContext;
import dhyces.trimmed.modhelper.network.packet.CommonPacket;

@FunctionalInterface
public interface PacketConsumer {
    <T extends CommonPacket<T>> void handle(PacketContext<T> context);

    interface Typed<T extends CommonPacket<T>> extends PacketConsumer {
        @Override
        default <T extends CommonPacket<T>> void handle(PacketContext<T> context) {
            handleTyped(cast(context));
        }

        private <T> T cast(Object o) {
            return (T) o;
        }

        void handleTyped(PacketContext<T> context);
    }
}
