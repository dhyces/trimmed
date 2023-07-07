package dhyces.trimmed.impl.client.models.template;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface TemplateData {
    List<Pair<String, String>> getData();
    String getRawId();
}
