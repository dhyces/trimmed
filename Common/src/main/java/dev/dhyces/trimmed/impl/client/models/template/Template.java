package dev.dhyces.trimmed.impl.client.models.template;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Template {
    String replace(Function<String, String> replacer);
}
