package dhyces.trimmed.impl.client.models.template;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.util.function.BiConsumer;

public interface Template {
    void generate(BufferedReader reader, BiConsumer<ResourceLocation, BlockModel> modelConsumer);
}
