package dhyces.trimmed.impl.client.models.template;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dhyces.trimmed.Trimmed;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ModelTemplateManager implements PreparableReloadListener {
    private static ModelTemplateManager INSTANCE;

    private static Map<ResourceLocation, IoSupplier<BufferedReader>> rawTemplates;
    private static Multimap<ResourceLocation, Template> templates;

    private static final FileToIdConverter PRE_PROCESSOR_CONVERTER = FileToIdConverter.json("trimmed/pre_processors");

//    public static final Codec<PreProcessor> PRE_PROCESSOR_CODEC = CodecUtil.TRIMMED_IDENTIFIER.dispatch(PRE_PROCESSORS.inverse()::get, PRE_PROCESSORS::get);

    public static void init() {
    }

    public static ModelTemplateManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModelTemplateManager();
        }
        return INSTANCE;
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

    public static void generateTemplates(BiConsumer<ResourceLocation, Supplier<BlockModel>> modelConsumer) {
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
        rawTemplates.clear();
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.completedFuture(Unit.INSTANCE).thenCompose(preparationBarrier::wait).thenAccept(unit -> Trimmed.logInDev("Trimmed templates are prepared!"));
//        return load(resourceManager).thenCompose(preparationBarrier::wait).thenAccept(unit -> Trimmed.logInDev("Template pre-processors are prepared!"));
    }

//    private CompletableFuture<Unit> load(ResourceManager manager) {
//        for (Map.Entry<ResourceLocation, Resource> entry : PRE_PROCESSOR_CONVERTER.listMatchingResources(manager).entrySet()) {
//
//        }
//        return CompletableFuture.completedFuture(Unit.INSTANCE);
//    }
}
