package dev.dhyces.trimmed.api.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.models.template.StringTemplate;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.types.AnyTrimItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.types.ComponentItemOverrideProvider;
import dev.dhyces.trimmed.api.data.model.override.ItemOverrideFile;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public abstract class ItemOverrideDataProvider implements DataProvider {
    protected final String modid;
    protected final PackOutput dataOutput;
    protected final PackOutput.PathProvider pathResolver;
    private final Map<ItemLike, ItemOverrideFile> providerMap = new Object2ObjectLinkedOpenHashMap<>();

    public ItemOverrideDataProvider(PackOutput output, String modid) {
        this.dataOutput = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, TrimmedReference.OVERRIDES_DIRECTORY);
        this.modid = modid;
    }

    protected abstract void addItemOverrides();

    protected void addComponentOverride(ItemLike item, UnaryOperator<DataComponentPatch.Builder> patchBuilder, ResourceLocation itemModelId) {
        addItemOverrides(item, new ComponentItemOverrideProvider(patchBuilder.apply(DataComponentPatch.builder()).build(), new ModelResourceLocation(itemModelId, "inventory")));
    }

    protected void addComponentOverride(ItemLike item, UnaryOperator<DataComponentPatch.Builder> patchBuilder, ModelResourceLocation modelId) {
        addItemOverrides(item, new ComponentItemOverrideProvider(patchBuilder.apply(DataComponentPatch.builder()).build(), modelId));
    }

    protected ArmorSetTrimBuilder anyTrimBuilder(ArmorSet armorSet) {
        return new ArmorSetTrimBuilder(armorSet);
    }

    protected void defaultAnyTrim(ArmorSet armorSet) {
        new ArmorSetTrimBuilder(armorSet).defaultTemplate().end();
    }

    protected void addAnyTrimOverride(ItemLike item, StringTemplate stringTemplate) {
        addItemOverrides(item, new AnyTrimItemOverrideProvider(stringTemplate));
    }

    protected void addAnyTrimOverride(ItemLike item, String stringTemplate) {
        addAnyTrimOverride(item, StringTemplate.of(stringTemplate));
    }

    protected void addAnyTrimOverride(ItemLike item) {
        addAnyTrimOverride(item, defaultTemplateString(item));
    }

    protected void addItemOverrides(ItemLike item, ItemOverrideProvider... providers) {
        for (ItemOverrideProvider provider : providers) {
            providerMap.computeIfAbsent(item, itemConvertible -> new ItemOverrideFile(new ObjectLinkedOpenHashSet<>(), false)).overrideProviders().add(provider);
        }
    }

    protected String defaultTemplateString(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).withPrefix("item/") + "_${material_suffix}_trim";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        addItemOverrides();
        return CompletableFuture.allOf(providerMap.entrySet().stream().map((entry) -> {
            DataResult<JsonElement> encoded = ItemOverrideFile.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            JsonElement json = encoded.getOrThrow();
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(entry.getKey().asItem());
            return DataProvider.saveStable(writer, json, pathResolver.json(id));
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ItemOverrideProvider for " + modid;
    }

    public record ArmorSet(ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {}

    public class ArmorSetTrimBuilder {
        private ArmorSet armorSet;
        private StringTemplate helmetTemplate;
        private StringTemplate chestplateTemplate;
        private StringTemplate leggingsTemplate;
        private StringTemplate bootsTemplate;

        ArmorSetTrimBuilder(ArmorSet armorSet) {
            this.armorSet = armorSet;
        }

        public ArmorSetTrimBuilder defaultTemplate() {
            helmetTemplate = StringTemplate.of(defaultTemplateString(armorSet.helmet));
            chestplateTemplate = StringTemplate.of(defaultTemplateString(armorSet.chestplate));
            leggingsTemplate = StringTemplate.of(defaultTemplateString(armorSet.leggings));
            bootsTemplate = StringTemplate.of(defaultTemplateString(armorSet.boots));
            return this;
        }

        public ArmorSetTrimBuilder helmetTemplate(String string) {
            this.helmetTemplate = StringTemplate.of(string);
            if (!helmetTemplate.getVariables().contains("material_suffix") || helmetTemplate.getVariables().size() != 1) {
                throw new IllegalStateException("Only \"material_suffix\" is a valid template variable for \"any_trim\" model overrides");
            }
            return this;
        }

        public ArmorSetTrimBuilder chestplateTemplate(String string) {
            this.chestplateTemplate = StringTemplate.of(string);
            if (!chestplateTemplate.getVariables().contains("material_suffix") || chestplateTemplate.getVariables().size() != 1) {
                throw new IllegalStateException("Only \"material_suffix\" is a valid template variable for \"any_trim\" model overrides");
            }
            return this;
        }

        public ArmorSetTrimBuilder leggingsTemplate(String string) {
            this.leggingsTemplate = StringTemplate.of(string);
            if (!leggingsTemplate.getVariables().contains("material_suffix") || leggingsTemplate.getVariables().size() != 1) {
                throw new IllegalStateException("Only \"material_suffix\" is a valid template variable for \"any_trim\" model overrides");
            }
            return this;
        }

        public ArmorSetTrimBuilder bootsTemplate(String string) {
            this.bootsTemplate = StringTemplate.of(string);
            if (!bootsTemplate.getVariables().contains("material_suffix") || bootsTemplate.getVariables().size() != 1) {
                throw new IllegalStateException("Only \"material_suffix\" is a valid template variable for \"any_trim\" model overrides");
            }
            return this;
        }

        public void end() {
            Objects.requireNonNull(helmetTemplate);
            Objects.requireNonNull(chestplateTemplate);
            Objects.requireNonNull(leggingsTemplate);
            Objects.requireNonNull(bootsTemplate);

            addItemOverrides(armorSet.helmet, new AnyTrimItemOverrideProvider(helmetTemplate));
            addItemOverrides(armorSet.chestplate, new AnyTrimItemOverrideProvider(chestplateTemplate));
            addItemOverrides(armorSet.leggings, new AnyTrimItemOverrideProvider(leggingsTemplate));
            addItemOverrides(armorSet.boots, new AnyTrimItemOverrideProvider(bootsTemplate));
        }
    }
}
