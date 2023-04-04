package dhyces.trimmed;

import dhyces.trimmed.client.override.provider.ItemOverrideProviderRegistry;
import dhyces.trimmed.client.override.ItemOverrideReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrimmedClient implements ClientModInitializer {
    public static final String MODID = "trimmed";
    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("Trimmed");

    @Override
    public void onInitializeClient() {
        ItemOverrideProviderRegistry.init();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ItemOverrideReloadListener());
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            ItemOverrideReloadListener.getModelsToBake().forEach(out);
        });
    }
}
