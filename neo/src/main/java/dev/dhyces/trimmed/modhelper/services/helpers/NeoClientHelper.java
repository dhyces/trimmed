package dev.dhyces.trimmed.modhelper.services.helpers;

import dev.dhyces.trimmed.api.TrimmedClientApiConsumer;
import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class NeoClientHelper implements ClientHelper {
    @Override
    public List<ModApiConsumer<TrimmedClientApiEntrypoint>> getClientApiConsumers() {
        return ModList.get().getAllScanData().stream()
                .flatMap(modFileScanData -> modFileScanData.getAnnotatedBy(TrimmedClientApiConsumer.class, ElementType.TYPE))
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
    public BakedModel getModel(ResourceLocation resourceId) {
        return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(resourceId));
    }
}
