package dhyces.trimmed.impl.client.models.template;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ModelTemplateManager {
    private static final BiMap<ResourceLocation, PreProcessor> PRE_PROCESSORS = HashBiMap.create();

    private static Map<ResourceLocation, IoSupplier<BufferedReader>> rawTemplates;
    private static Multimap<ResourceLocation, Template> templates;

    private static final FileToIdConverter PRE_PROCESSOR_CONVERTER = FileToIdConverter.json("trimmed/pre_processors");

//    public static final Codec<PreProcessor> PRE_PROCESSOR_CODEC = CodecUtil.TRIMMED_IDENTIFIER.dispatch(PRE_PROCESSORS.inverse()::get, PRE_PROCESSORS::get);

    public static void init() {
    }

    private ModelTemplateManager() {}

    public static void addTemplateResource(ResourceLocation templatePath, IoSupplier<BufferedReader> resource) {
        if (rawTemplates == null) {
            rawTemplates = new HashMap<>();
        }
        rawTemplates.put(templatePath, resource);
    }

    public static void addTemplate(ResourceLocation templateId, Template template) {
        if (templates == null) {
            templates = HashMultimap.create();
        }
        templates.put(templateId, template);
    }

    public static void generateTemplates(BiConsumer<ResourceLocation, BlockModel> modelConsumer) {
        if (templates == null) {
            return;
        }
        for (Map.Entry<ResourceLocation, Template> entry : templates.entries()) {
            IoSupplier<BufferedReader> readerSupplier = rawTemplates.get(entry.getKey().withPrefix("models/").withPath(s -> s + ".trimmed_template.json"));
            if (readerSupplier == null) {
                throw new IllegalStateException("No template file found for " + entry.getKey());
            }
            try (BufferedReader reader = readerSupplier.get()) {
                entry.getValue().generate(reader, modelConsumer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void register(ResourceLocation id, PreProcessor processorCodec) {
        if (PRE_PROCESSORS.containsKey(id)) {
            throw new IllegalArgumentException("The processor " + id + " is already registered with " + PRE_PROCESSORS.get(id).getClass().getSimpleName() + "!");
        }
        PRE_PROCESSORS.put(id, processorCodec);
    }

//    @Override
//    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
//        return load(resourceManager).thenCompose(preparationBarrier::wait).thenAccept(unit -> Trimmed.logInDev("Template pre-processors are prepared!"));
//    }
//
//    private CompletableFuture<Unit> load(ResourceManager manager) {
//        for (Map.Entry<ResourceLocation, Resource> entry : PRE_PROCESSOR_CONVERTER.listMatchingResources(manager).entrySet()) {
//
//        }
//        return CompletableFuture.completedFuture(Unit.INSTANCE);
//    }
}
