package dhyces.trimmed.impl.client.atlas;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

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
        Map<String, Supplier<IntUnaryOperator>> replacePixelsMap = new HashMap<>();
        ClientMapManager.getUnchecked(permutations).ifPresent(resourceLocationStringMap -> {
            resourceLocationStringMap.forEach((resourceLocation, s) ->
                    replacePixelsMap.put(s, Suppliers.memoize(() ->
                            PalettedPermutations.createPaletteMapping(rawPaletteKeyImage.get(), PalettedPermutations.loadPaletteEntryFromImage(pResourceManager, resourceLocation))
                    ))
            );
        });

        ClientTagManager.getUncheckedHandler().streamValues(textures).forEach(resourceLocation -> {
            Optional<Resource> imageOptional = pResourceManager.getResource(TEXTURE_ID_CONVERTER.idToFile(resourceLocation));
            if (imageOptional.isEmpty()) {
                Trimmed.LOGGER.error("Cannot locate " + resourceLocation);
            } else {
                LazyLoadedImage lazyloadedimage = new LazyLoadedImage(resourceLocation, imageOptional.get(), replacePixelsMap.size());

                for (Map.Entry<String, Supplier<IntUnaryOperator>> entry : replacePixelsMap.entrySet()) {
                    ResourceLocation permutedId = resourceLocation.withSuffix("_" + entry.getKey());
                    pOutput.add(permutedId, new PalettedPermutations.PalettedSpriteSupplier(lazyloadedimage, entry.getValue(), permutedId));
                }
            }
        });
    }

    @Override
    public SpriteSourceType type() {
        return TrimmedSpriteSourceTypes.OPEN_PALETTED_PERMUTATIONS;
    }
}
