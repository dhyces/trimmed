package dev.dhyces.trimmed.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import dev.dhyces.trimmed.api.TrimmedClientApi;
import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.model.RegistryAwareItemModelShaper;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class NeoClientHelper implements ClientHelper {
    @Override
    public List<ModApiConsumer<TrimmedClientApiEntrypoint>> getClientApiConsumers() {
        return ModList.get().getAllScanData().stream()
                .flatMap(modFileScanData -> modFileScanData.getAnnotatedBy(TrimmedClientApi.class, ElementType.TYPE))
                .map(annotationData -> {
                    String modid = (String) annotationData.annotationData().get("value");
                    Class<?> clazz;
                    try {
                        clazz = getClass().getClassLoader().loadClass(annotationData.clazz().getClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e); // Should not be possible
                    }
                    TrimmedClientApiEntrypoint apiEntrypoint;
                    try {
                        apiEntrypoint = (TrimmedClientApiEntrypoint)clazz.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException("Could not find valid api constructor for mod id " + modid + ". Make sure the constructor has no parameters and is public.", e);
                    } catch (ClassCastException e) {
                        throw new IllegalStateException("Client api entrypoint for " + modid + " must implement TrimmedClientRegistration!", e);
                    }
                    return new ModApiConsumer<>(modid, apiEntrypoint);
                }).toList();
    }

    @Override
    public SpriteContents createSpriteContents(ResourceLocation id, NativeImage image) {
        return new SpriteContents(id, new FrameSize(image.getWidth(), image.getHeight()), image, ResourceMetadata.EMPTY);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return ((RegistryAwareItemModelShaper) Minecraft.getInstance().getItemRenderer().getItemModelShaper()).getLocation(itemStack);
    }
}
