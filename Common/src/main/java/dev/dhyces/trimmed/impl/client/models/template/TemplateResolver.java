package dev.dhyces.trimmed.impl.client.models.template;

import java.util.function.Function;

public interface TemplateResolver {
    String replace(Function<String, String> replacer);
}
