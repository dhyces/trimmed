package dhyces.trimmed.impl.client.models.template;

import com.mojang.datafixers.util.Pair;

import java.util.List;

public final class GroovyReplacer {
    private GroovyReplacer() {}

    public static String replace(String in, List<Pair<String, String>> replacements) {
        String ret = in;
        for (Pair<String, String> pair : replacements) {
            ret = ret.replace("${" + pair.getFirst() + "}", pair.getSecond());
        }
        return ret;
    }
}
