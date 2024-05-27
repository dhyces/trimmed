package dev.dhyces.trimmed.impl.client.models.template;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringTemplate {
    public static final Codec<StringTemplate> CODEC = Codec.STRING.xmap(StringTemplate::of, StringTemplate::asOriginalString);
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{[a-z_0-9]+\\}");
    private final List<String> staticStrings;
    private final Int2ObjectMap<String> indexedStrings;

    StringTemplate(List<String> staticStrings, Int2ObjectMap<String> indexedStrings) {
        this.staticStrings = staticStrings;
        this.indexedStrings = indexedStrings;
    }

    public static StringTemplate of(String rawData) {
        ObjectList<String> staticStrings = new ObjectArrayList<>();
        Int2ObjectMap<String> replacementKeys = new Int2ObjectArrayMap<>();
        AtomicInteger cursor = new AtomicInteger();
        TEMPLATE_PATTERN.matcher(rawData).results().forEach(matchResult -> {
                    staticStrings.add(rawData.substring(cursor.getPlain(), matchResult.start()));
                    cursor.setPlain(matchResult.end());
                    String key = matchResult.group().substring(2, matchResult.group().length()-1);
                    replacementKeys.put(replacementKeys.size(), key);
                });
        staticStrings.add(rawData.substring(cursor.getPlain()));
        return new StringTemplate(ObjectLists.unmodifiable(staticStrings), Int2ObjectMaps.unmodifiable(replacementKeys));
    }

    public Collection<String> getFragments() {
        return staticStrings;
    }

    public Collection<String> getVariables() {
        return indexedStrings.values().stream().distinct().toList();
    }

    public String process(Function<String, String> replacer) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < staticStrings.size(); i++) {
            builder.append(staticStrings.get(i));
            String key = indexedStrings.get(i);
            if (key != null) {
                String replacement = replacer.apply(key);
                if (replacement == null) {
                    throw new IllegalStateException("Cannot get replacement for \"" + key + "\"");
                }
                builder.append(replacement);
            }
        }
        return builder.toString();
    }

    public String asOriginalString() {
        return process(s -> "${" + s + "}");
    }
}
