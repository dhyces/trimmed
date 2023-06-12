package dhyces.trimmed.api.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public class ClientUtil {
    @Nullable
    public static RegistryAccess getRegistryAccess() {
        if (Minecraft.getInstance().level == null) {
            return null;
        }
        return Minecraft.getInstance().level.registryAccess();
    }
}
