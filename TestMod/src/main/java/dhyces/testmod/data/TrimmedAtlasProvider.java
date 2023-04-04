package dhyces.testmod.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.testmod.TrimmedTest;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TrimmedAtlasProvider implements DataProvider {

    private final DataOutput.PathResolver pathProvider;

    public TrimmedAtlasProvider(DataOutput output) {
        this.pathProvider = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "atlases");
    }

    protected void gatherSpriteSources(Gatherer gatherer) {
        gatherer.addAtlas(new Identifier("armor_trims"))
                .addSource(new PalettedPermutationsAtlasSource(
                        List.of(
                                new Identifier("trims/models/armor/coast"),
                                new Identifier("trims/models/armor/coast_leggings"),
                                new Identifier("trims/models/armor/sentry"),
                                new Identifier("trims/models/armor/sentry_leggings"),
                                new Identifier("trims/models/armor/dune"),
                                new Identifier("trims/models/armor/dune_leggings"),
                                new Identifier("trims/models/armor/wild"),
                                new Identifier("trims/models/armor/wild_leggings"),
                                new Identifier("trims/models/armor/ward"),
                                new Identifier("trims/models/armor/ward_leggings"),
                                new Identifier("trims/models/armor/eye"),
                                new Identifier("trims/models/armor/eye_leggings"),
                                new Identifier("trims/models/armor/vex"),
                                new Identifier("trims/models/armor/vex_leggings"),
                                new Identifier("trims/models/armor/tide"),
                                new Identifier("trims/models/armor/tide_leggings"),
                                new Identifier("trims/models/armor/snout"),
                                new Identifier("trims/models/armor/snout_leggings"),
                                new Identifier("trims/models/armor/rib"),
                                new Identifier("trims/models/armor/rib_leggings"),
                                new Identifier("trims/models/armor/spire"),
                                new Identifier("trims/models/armor/spire_leggings")
                        ), new Identifier("trims/color_palettes/trim_palette"),
                        Util.make(new HashMap<>(), map -> {
                                    map.put("echo", new Identifier(TrimmedTest.MODID, "trim/color_palettes/echo"));
                                    map.put("blaze", new Identifier(TrimmedTest.MODID, "trim/color_palettes/blaze"));
                                    map.put("shell", new Identifier(TrimmedTest.MODID, "trim/color_palettes/shell"));
                                    map.put("prismarine", new Identifier(TrimmedTest.MODID, "trim/color_palettes/prismarine"));
                                    map.put("glow", new Identifier(TrimmedTest.MODID, "trim/color_palettes/glow"));
                                }
                        )
                )).finish();
        gatherer.addAtlas(new Identifier("blocks"))
                .addSource(new PalettedPermutationsAtlasSource(
                        List.of(
                                new Identifier("trims/items/leggings_trim"),
                                new Identifier("trims/items/chestplate_trim"),
                                new Identifier("trims/items/helmet_trim"),
                                new Identifier("trims/items/boots_trim")
                        ), new Identifier("trims/color_palettes/trim_palette"),
                        Util.make(new HashMap<>(), map -> {
                                    map.put("echo", new Identifier(TrimmedTest.MODID, "trim/color_palettes/echo"));
                                    map.put("blaze", new Identifier(TrimmedTest.MODID, "trim/color_palettes/blaze"));
                                    map.put("shell", new Identifier(TrimmedTest.MODID, "trim/color_palettes/shell"));
                                    map.put("prismarine", new Identifier(TrimmedTest.MODID, "trim/color_palettes/prismarine"));
                                    map.put("glow", new Identifier(TrimmedTest.MODID, "trim/color_palettes/glow"));
                                }
                        )
                )).finish();
    }

    @Override
    public CompletableFuture<?> run(DataWriter cachedOutput) {
        Gatherer gatherer = new Gatherer();
        gatherSpriteSources(gatherer);
        return CompletableFuture.allOf(gatherer.map.entrySet().stream().map(resourceLocationListEntry -> {
            DataResult<JsonElement> element = AtlasSourceManager.LIST_CODEC.encodeStart(JsonOps.INSTANCE, resourceLocationListEntry.getValue());
            if (element.result().isEmpty()) {
                throw new IllegalStateException("invalid atlas: %s".formatted(resourceLocationListEntry.getKey()));
            }
            Path path = pathProvider.resolveJson(resourceLocationListEntry.getKey());
            return DataProvider.writeToPath(cachedOutput, element.get().left().get(), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Atlases";
    }

    public class Gatherer {
        private Map<Identifier, List<AtlasSource>> map = new HashMap<>();

        public Builder addAtlas(Identifier location) {
            return new Builder(this, location);
        }
    }

    public class Builder {
        Gatherer parent;
        Identifier resourceLocation;
        List<AtlasSource> sources = new LinkedList<>();

        Builder(Gatherer parent, Identifier resourceLocation) {
            this.parent = parent;
            this.resourceLocation = resourceLocation;
        }

        public Builder addSource(AtlasSource source) {
            sources.add(source);
            return this;
        }

        public void finish() {
            parent.map.put(resourceLocation, sources);
        }
    }
}
