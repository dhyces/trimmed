package dhyces.testmod.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.testmod.TrimmedTest;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TrimmedAtlasProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public TrimmedAtlasProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }

    protected void gatherSpriteSources(Gatherer gatherer) {
        gatherer.addAtlas(new ResourceLocation("armor_trims"))
                .addSource(new PalettedPermutations(
                        List.of(
                                new ResourceLocation("trims/models/armor/coast"),
                                new ResourceLocation("trims/models/armor/coast_leggings"),
                                new ResourceLocation("trims/models/armor/sentry"),
                                new ResourceLocation("trims/models/armor/sentry_leggings"),
                                new ResourceLocation("trims/models/armor/dune"),
                                new ResourceLocation("trims/models/armor/dune_leggings"),
                                new ResourceLocation("trims/models/armor/wild"),
                                new ResourceLocation("trims/models/armor/wild_leggings"),
                                new ResourceLocation("trims/models/armor/ward"),
                                new ResourceLocation("trims/models/armor/ward_leggings"),
                                new ResourceLocation("trims/models/armor/eye"),
                                new ResourceLocation("trims/models/armor/eye_leggings"),
                                new ResourceLocation("trims/models/armor/vex"),
                                new ResourceLocation("trims/models/armor/vex_leggings"),
                                new ResourceLocation("trims/models/armor/tide"),
                                new ResourceLocation("trims/models/armor/tide_leggings"),
                                new ResourceLocation("trims/models/armor/snout"),
                                new ResourceLocation("trims/models/armor/snout_leggings"),
                                new ResourceLocation("trims/models/armor/rib"),
                                new ResourceLocation("trims/models/armor/rib_leggings"),
                                new ResourceLocation("trims/models/armor/spire"),
                                new ResourceLocation("trims/models/armor/spire_leggings"),
                                new ResourceLocation("trims/models/armor/wayfinder"),
                                new ResourceLocation("trims/models/armor/wayfinder_leggings"),
                                new ResourceLocation("trims/models/armor/shaper"),
                                new ResourceLocation("trims/models/armor/shaper_leggings"),
                                new ResourceLocation("trims/models/armor/silence"),
                                new ResourceLocation("trims/models/armor/silence_leggings"),
                                new ResourceLocation("trims/models/armor/raiser"),
                                new ResourceLocation("trims/models/armor/raiser_leggings"),
                                new ResourceLocation("trims/models/armor/host"),
                                new ResourceLocation("trims/models/armor/host_leggings")
                        ), new ResourceLocation("trims/color_palettes/trim_palette"),
                        Util.make(new HashMap<>(), map -> {
                                    map.put("echo", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/echo"));
                                    map.put("blaze", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/blaze"));
                                    map.put("shell", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/shell"));
                                    map.put("prismarine", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/prismarine"));
                                    map.put("glow", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/glow"));
                                }
                        )
                )).finish();
        gatherer.addAtlas(new ResourceLocation("blocks"))
                .addSource(new PalettedPermutations(
                        List.of(
                                new ResourceLocation("trims/items/leggings_trim"),
                                new ResourceLocation("trims/items/chestplate_trim"),
                                new ResourceLocation("trims/items/helmet_trim"),
                                new ResourceLocation("trims/items/boots_trim")
                        ), new ResourceLocation("trims/color_palettes/trim_palette"),
                        Util.make(new HashMap<>(), map -> {
                                    map.put("echo", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/echo"));
                                    map.put("blaze", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/blaze"));
                                    map.put("shell", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/shell"));
                                    map.put("prismarine", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/prismarine"));
                                    map.put("glow", new ResourceLocation(TrimmedTest.MODID, "trim/color_palettes/glow"));
                                }
                        )
                )).finish();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Gatherer gatherer = new Gatherer();
        gatherSpriteSources(gatherer);
        return CompletableFuture.allOf(gatherer.map.entrySet().stream().map(resourceLocationListEntry -> {
            DataResult<JsonElement> element = SpriteSources.FILE_CODEC.encodeStart(JsonOps.INSTANCE, resourceLocationListEntry.getValue());
            if (element.result().isEmpty()) {
                throw new IllegalStateException("invalid atlas: %s".formatted(resourceLocationListEntry.getKey()));
            }
            Path path = pathProvider.json(resourceLocationListEntry.getKey());
            return DataProvider.saveStable(cachedOutput, element.get().left().get(), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Atlases";
    }

    public class Gatherer {
        private Map<ResourceLocation, List<SpriteSource>> map = new HashMap<>();

        public Builder addAtlas(ResourceLocation location) {
            return new Builder(this, location);
        }
    }

    public class Builder {
        Gatherer parent;
        ResourceLocation resourceLocation;
        List<SpriteSource> sources = new LinkedList<>();

        Builder(Gatherer parent, ResourceLocation resourceLocation) {
            this.parent = parent;
            this.resourceLocation = resourceLocation;
        }

        public Builder addSource(SpriteSource source) {
            sources.add(source);
            return this;
        }

        public void finish() {
            parent.map.put(resourceLocation, sources);
        }
    }
}
