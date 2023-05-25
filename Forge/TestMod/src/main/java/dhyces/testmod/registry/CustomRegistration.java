package dhyces.testmod.registry;

import dhyces.testmod.TrimmedTest;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CustomRegistration {
    public static final DeferredRegister<CustomObj> CUSTOM_DEFERRED_REGISTRY = DeferredRegister.create(TrimmedTest.id("custom"), TrimmedTest.MODID);
    public static final Supplier<IForgeRegistry<CustomObj>> CUSTOM_REGISTRY = CUSTOM_DEFERRED_REGISTRY.makeRegistry(() -> new RegistryBuilder<CustomObj>().hasTags());

    public static final RegistryObject<CustomObj> OBJ = CUSTOM_DEFERRED_REGISTRY.register("test", () -> new CustomObj("Hello!", 42));
}
