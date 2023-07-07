package dhyces.trimmed.api.client.override.provider.providers;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.client.UncheckedClientMaps;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.api.client.override.provider.SimpleItemOverrideProvider;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.ImmutableEntry;
import dhyces.trimmed.impl.client.models.template.GroovyReplacer;
import dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnyTrimItemOverrideProvider extends SimpleItemOverrideProvider {
    public static final Codec<AnyTrimItemOverrideProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("template").forGetter(anyTrimItemOverrideProvider -> anyTrimItemOverrideProvider.templateId),
                    ResourceLocation.CODEC.fieldOf("trim_texture").forGetter(anyTrimItemOverrideProvider -> anyTrimItemOverrideProvider.trimTexture)
            ).apply(instance, AnyTrimItemOverrideProvider::new)
    );

    private final ResourceLocation templateId;
    private final ResourceLocation trimTexture;
    private ResourceLocation id;

    public AnyTrimItemOverrideProvider(ResourceLocation templateId, ResourceLocation trimTexture) {
        this.templateId = templateId;
        this.trimTexture = trimTexture;
    }

    @Override
    public Optional<ModelResourceLocation> getModelLocation(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        Optional<ResourceLocation> materialId = ArmorTrim.getTrim(world.registryAccess(), itemStack)
                .map(ArmorTrim::material)
                .flatMap(Holder::unwrapKey)
                .map(ResourceKey::location);
        if (materialId.isPresent()) {
            // TODO: this wouldn't get armor material overrides though
            ResourceLocation key = materialId.get().withPath(s -> "trims/color_palettes/" + s);
            MapValue value = TrimmedClientMapApi.INSTANCE.map(UncheckedClientMaps.ALL_TRIM_PERMUTATIONS).get(key);
            if (value != null) {
                return Optional.of(new ModelResourceLocation(id.getNamespace(), id.getPath() + "_" + value.value() + "_trim", "inventory"));
            }
        }
        return Optional.empty();
    }

    @Override
    public void finish(ResourceLocation id) {
        this.id = id;
        ModelTemplateManager.addTemplate(templateId, (reader, modelConsumer) -> {
            String rawData = reader.lines().collect(Collectors.joining());
            Pair<String, String> item = Pair.of("item_texture", id.withPrefix("item/").toString());

            for (ImmutableEntry<ResourceLocation, MapValue> material : TrimmedClientMapApi.INSTANCE.map(UncheckedClientMaps.ALL_TRIM_PERMUTATIONS)) {
                List<Pair<String, String>> replacers = new ArrayList<>();
                replacers.add(item);
                replacers.add(Pair.of("material", material.getValue().value()));
                replacers.add(Pair.of("trim_texture", trimTexture.toString()));

                String replacedModel = GroovyReplacer.replace(rawData, replacers);
                modelConsumer.accept(id.withSuffix("_" + material.getValue().value() + "_trim"), BlockModel.fromString(replacedModel));
            }
        });
    }

//    @Override
//    public Stream<ModelResourceLocation> getModelsToBake() {
//        return TrimmedClientMapApi.INSTANCE.map(UncheckedClientMaps.ALL_TRIM_PERMUTATIONS).stream()
//                .map(entry -> new ModelResourceLocation(id.withSuffix("_" + entry.getValue().value() + "_trim"), "inventory"));
//    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.ANY_TRIM;
    }
}
