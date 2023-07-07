package dhyces.trimmed.impl.client.models.template;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

public interface PreProcessor {

    void process(String raw, ResourceLocation templateId, List<Pair<String, String>> replacers, String rawId, BiConsumer<ResourceLocation, String> consumer);

    record TemplateContext(String raw, ResourceLocation templateId) {}
}
