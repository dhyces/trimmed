package dev.dhyces.trimmed.impl.client.atlas;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.client.TrimmedClientTagApi;
import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.api.client.tag.TagHolder;
import dev.dhyces.trimmed.api.maps.MapHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public class OpenPalettedPermutations implements SpriteSource {
    public static final MapCodec<OpenPalettedPermutations> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("palette_key").forGetter(openPalettedPermutations -> openPalettedPermutations.paletteKey),
                    TrimmedClientMapApi.getInstance().simpleCodec(ClientMapTypes.TEXTURE_SUFFIX).fieldOf("permutation_map").forGetter(openPalettedPermutations -> openPalettedPermutations.permutations),
                    TrimmedClientTagApi.getInstance().tagCodec(ClientKeyResolvers.TEXTURE).fieldOf("texture_set").forGetter(openPalettedPermutations -> openPalettedPermutations.textures)
            ).apply(instance, OpenPalettedPermutations::new)
    );

    private final ResourceLocation paletteKey;
    private final MapHolder<ResourceLocation, String> permutations;
    private final TagHolder<ResourceLocation> textures;

    public OpenPalettedPermutations(ResourceLocation paletteKey, MapHolder<ResourceLocation, String> permutations, TagHolder<ResourceLocation> textures) {
        this.paletteKey = paletteKey;
        this.permutations = permutations;
        this.textures = textures;
    }

    @Override
    public void run(ResourceManager pResourceManager, Output pOutput) {
        Supplier<int[]> rawPaletteKeyImage = Suppliers.memoize(() ->
            PalettedPermutations.loadPaletteEntryFromImage(pResourceManager, paletteKey)
        );
        Map<ResourceLocation, OptionalSupplier> replacePixelsMap = new Object2ObjectOpenHashMap<>();

        permutations.getMap().forEach((id, permuteString) -> {
            replacePixelsMap.put(id.withPath(permuteString),
                    new OptionalSupplier(permutations.isRequired(id), Suppliers.memoize(() ->
                            PalettedPermutations.createPaletteMapping(rawPaletteKeyImage.get(), PalettedPermutations.loadPaletteEntryFromImage(pResourceManager, id))
                    ))
            );
        });

        textures.getSet().forEach(texture -> {
            Optional<Resource> imageOptional = pResourceManager.getResource(TEXTURE_ID_CONVERTER.idToFile(texture));
            if (imageOptional.isEmpty() && textures.isRequired(texture)) {
                Trimmed.LOGGER.error("Cannot locate required {}", texture);
            } else if (imageOptional.isPresent()) {
                LazyLoadedImage lazyloadedimage = new LazyLoadedImage(texture, imageOptional.get(), replacePixelsMap.size());

                for (Map.Entry<ResourceLocation, OptionalSupplier> entry : replacePixelsMap.entrySet()) {
                    ResourceLocation permutedId = texture.withSuffix("_" + entry.getKey().getPath());
                    pOutput.add(permutedId, new OpenPalettedSpriteSupplier(lazyloadedimage, entry.getValue(), permutedId));
                }
            }
        });
    }

    @Override
    public SpriteSourceType type() {
        return TrimmedSpriteSourceTypes.OPEN_PALETTED_PERMUTATIONS.get();
    }

    public record OptionalSupplier(boolean isRequired, Supplier<IntUnaryOperator> mapper) {}

    public record OpenPalettedSpriteSupplier(LazyLoadedImage lazyLoadedImage, OptionalSupplier optionalSupplier, ResourceLocation permutedId) implements SpriteSupplier {

        @Override
        public SpriteContents apply(SpriteResourceLoader spriteResourceLoader) {
            try {
                NativeImage image = lazyLoadedImage.get().mappedCopy(optionalSupplier.mapper.get());
                return new SpriteContents(permutedId, new FrameSize(image.getWidth(), image.getHeight()), image, ResourceMetadata.EMPTY);
            } catch (IOException e) {
                if (optionalSupplier.isRequired) {
                    Trimmed.LOGGER.error("Could not create paletted image for {}", permutedId);
                }
            } finally {
                lazyLoadedImage.release();
            }
            return null;
        }

        @Override
        public void discard() {
            lazyLoadedImage.release();
        }
    }
}
