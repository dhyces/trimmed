package dhyces.trimmed.api.data.tags;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseClientTagDataProvider implements DataProvider {

    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final Map<ResourceLocation, TagBuilder> builders = new LinkedHashMap<>();

    public BaseClientTagDataProvider(PackOutput packOutput, String modid, String prefix) {
        this.packOutput = packOutput;
        this.modid = modid;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, prefix);
    }

    protected TagBuilder getOrCreateBuilder(ResourceLocation clientTagLocation) {
        return this.builders.computeIfAbsent(clientTagLocation, resourceLocation -> {
            return TagBuilder.create();
        });
    }
}
