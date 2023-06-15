package dhyces.trimmed;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import java.util.function.Consumer;

public class QuiltTrimmedClient implements ClientModInitializer {

    public static void init() {
        TrimmedSpriteSourceTypes.bootstrap();
        ItemOverrideProviderRegistry.init();
    }

    public static void registerClientReloadListener(Consumer<PreparableReloadListener> eventConsumer) {
        eventConsumer.accept(new ItemOverrideReloadListener());
    }

    public static void injectListenersAtBeginning() {
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientMapManager());
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientTagManager());
    }

    public static void addModels(Consumer<ModelResourceLocation> eventConsumer) {
        ItemOverrideReloadListener.getModelsToBake().forEach(eventConsumer);
    }

    public static void onTagsSynced(RegistryAccess registryAccess, boolean shouldUpdateStatic) {
        if (shouldUpdateStatic) { //TODO: Disabled the toast for now. Use toast later when a datapack registry queued
//            if (Minecraft.getInstance().player != null) {
//                Minecraft.getInstance().getToasts().addToast(InfoToast.reloadClientInfo());
//            }
            ClientTagManager.updateDatapacksSynced(registryAccess);
        }
    }

    @Override
    public void onInitializeClient(ModContainer mod) {

    }
}
