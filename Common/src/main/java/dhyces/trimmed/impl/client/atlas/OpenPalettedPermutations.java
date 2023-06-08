package dhyces.trimmed.impl.client.atlas;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dhyces.modhelper.services.Services;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public class OpenPalettedPermutations implements SpriteSource {
    public static final Codec<OpenPalettedPermutations> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("palette_key").forGetter(openPalettedPermutations -> openPalettedPermutations.paletteKey),
                    ClientMapKey.CODEC.fieldOf("permutations").forGetter(openPalettedPermutations -> openPalettedPermutations.permutations),
                    ClientTagKey.CODEC.fieldOf("textures").forGetter(openPalettedPermutations -> openPalettedPermutations.textures)
            ).apply(instance, OpenPalettedPermutations::new)
    );

    private final ResourceLocation paletteKey;
    private final ClientMapKey permutations;
    private final ClientTagKey textures;

    public OpenPalettedPermutations(ResourceLocation paletteKey, ClientMapKey permutations, ClientTagKey textures) {
        this.paletteKey = paletteKey;
        this.permutations = permutations;
        this.textures = textures;
    }

    @Override
    public void run(ResourceManager pResourceManager, Output pOutput) {
        Supplier<int[]> rawPaletteKeyImage = Suppliers.memoize(() ->
            PalettedPermutations.loadPaletteEntryFromImage(pResourceManager, paletteKey)
        );
        Map<String, OptionalSupplier> replacePixelsMap = new HashMap<>();
        // TODO: Handle error if the map doesn't exist
        TrimmedApi.MAP_API.getSafeUncheckedClientMap(permutations).ifPresent(optionalIdStringMap -> {
            optionalIdStringMap.forEach((optionalId, s) ->
                    replacePixelsMap.put(s,
                            new OptionalSupplier(optionalId.isRequired(), Suppliers.memoize(() ->
                                    PalettedPermutations.createPaletteMapping(rawPaletteKeyImage.get(), PalettedPermutations.loadPaletteEntryFromImage(pResourceManager, optionalId.elementId()))
                            ))
                    )
            );
        });

        // TODO: Handle error if the tag doesn't exist or if any elements don't exist
        TrimmedApi.TAG_API.getUncheckedTag(textures).forEach(optionalTagElement -> {
            Optional<Resource> imageOptional = pResourceManager.getResource(TEXTURE_ID_CONVERTER.idToFile(optionalTagElement.elementId()));
            if (imageOptional.isEmpty() && optionalTagElement.isRequired()) {
                Trimmed.LOGGER.error("Cannot locate required " + optionalTagElement.elementId());
            } else if (imageOptional.isPresent()) {
                LazyLoadedImage lazyloadedimage = new LazyLoadedImage(optionalTagElement.elementId(), imageOptional.get(), replacePixelsMap.size());

                for (Map.Entry<String, OptionalSupplier> entry : replacePixelsMap.entrySet()) {
                    ResourceLocation permutedId = optionalTagElement.elementId().withSuffix("_" + entry.getKey());
                    pOutput.add(permutedId, new OpenPalettedSpriteSupplier(lazyloadedimage, entry.getValue(), permutedId));
                }
            }
        });
    }

    @Override
    public SpriteSourceType type() {
        return TrimmedSpriteSourceTypes.OPEN_PALETTED_PERMUTATIONS;
    }

    public record OptionalSupplier(boolean isRequired, Supplier<IntUnaryOperator> mapper) {}

    public record OpenPalettedSpriteSupplier(LazyLoadedImage lazyLoadedImage, OptionalSupplier optionalSupplier, ResourceLocation permutedId) implements SpriteSupplier {

        @Override
        public SpriteContents get() {
            try {
                NativeImage image = lazyLoadedImage.get().mappedCopy(optionalSupplier.mapper.get());
                return Services.CLIENT_HELPER.createSpriteContents(permutedId, image);
            } catch (IOException e) {
                if (optionalSupplier.isRequired) {
                    Trimmed.LOGGER.error("Could not create paletted image for " + permutedId);
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