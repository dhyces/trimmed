package dev.dhyces.trimmed.impl.client.models.template;

import java.util.function.Function;
import java.util.regex.Pattern;

public record GroovyTemplate(String template) implements Template {
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{[a-z_0-9]+\\}");

    @Override
    public String replace(Function<String, String> replacer) {
        return TEMPLATE_PATTERN.matcher(template)
                .replaceAll(match -> {
                    String key = match.group().substring(2, match.group().length()-1);
                    if (replacer.apply(key) == null) {
                        throw new IllegalArgumentException("Template replacement is invalid");
                    }
                    return replacer.apply(key);
                });
    }
}
