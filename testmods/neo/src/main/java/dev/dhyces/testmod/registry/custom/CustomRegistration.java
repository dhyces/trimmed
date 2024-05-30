package dev.dhyces.testmod.registry.custom;

import dev.dhyces.testmod.TrimmedTest;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomRegistration {
    public static final ResourceKey<Registry<CustomObj>> KEY = ResourceKey.createRegistryKey(TrimmedTest.id("custom"));
    public static final DeferredRegister<CustomObj> CUSTOM_DEFERRED_REGISTRY = DeferredRegister.create(KEY, TrimmedTest.MODID);
    public static final Registry<CustomObj> CUSTOM_REGISTRY = CUSTOM_DEFERRED_REGISTRY.makeRegistry(builder -> {});

    public static final DeferredHolder<CustomObj, CustomObj> OBJ = CUSTOM_DEFERRED_REGISTRY.register("test", () -> new CustomObj("Hello!", 42));
}
