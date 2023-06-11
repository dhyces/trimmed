package dhyces.modhelper.services.helpers;

import com.google.gson.JsonObject;
import dhyces.modhelper.services.util.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegistryManager;

public final class ForgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public boolean isClientDist() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public boolean isProduction() {
        return FMLLoader.isProduction();
    }

    @Override
    public Platform getPlatform() {
        return Platform.FORGE;
    }

    @Override
    public String resolveRegistryPath(ResourceKey<? extends Registry<?>> resourceKey) {
        String registryNamespace = resourceKey.location().getNamespace();
        if (registryNamespace.equals("minecraft")) {
            return resourceKey.location().getPath();
        } else {
            return registryNamespace + "/" + resourceKey.location().getPath();
        }
    }

    @Override
    public <T> boolean modRegistryExists(ResourceKey<? extends Registry<T>> modRegistry) {
        return RegistryManager.ACTIVE.getRegistry(modRegistry) != null;
    }

    @Override
    public boolean shouldPassConditions(JsonObject jsonObject) {
        return CraftingHelper.processConditions(jsonObject, "conditions", ICondition.IContext.TAGS_INVALID);
    }

    @Override
    public boolean isLoadingStateValid() {
        return ModLoader.isLoadingStateValid();
    }

    @Override
    public <T> T getRegistryValue(ResourceKey<? extends Registry<T>> registry, ResourceLocation valueKey) {
        return RegistryManager.ACTIVE.getRegistry(registry).getValue(valueKey);
    }
}
