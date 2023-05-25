package dhyces.modhelper.services.helpers;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import dhyces.modhelper.services.helpers.PlatformHelper;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
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
    public <T> boolean modRegistryExists(ResourceKey<? extends Registry<T>> modRegistry) {
        return RegistryManager.ACTIVE.getRegistry(modRegistry) != null;
    }

    @Override
    public boolean shouldPassConditions(JsonObject jsonObject) {
        return CraftingHelper.processConditions(jsonObject, "conditions", ICondition.IContext.TAGS_INVALID);
    }

    @Override
    public <T> T getRegistryValue(ResourceKey<? extends Registry<T>> registry, ResourceLocation valueKey) {
        return RegistryManager.ACTIVE.getRegistry(registry).getValue(valueKey);
    }
}
