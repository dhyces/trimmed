package dhyces.trimmed.impl.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import java.util.function.Predicate;

public record OptionalId(ResourceLocation elementId, boolean isRequired) {
    public static OptionalId from(TagEntry tagEntry) {
        return new OptionalId(tagEntry.getId(), tagEntry.isRequired());
    }

    public static OptionalId required(ResourceLocation elementId) {
        return new OptionalId(elementId, true);
    }

    public static OptionalId optional(ResourceLocation elementId) {
        return new OptionalId(elementId, false);
    }

    public static boolean checkEither(ResourceLocation elementId, Predicate<OptionalId> checker) {
        return checker.test(OptionalId.required(elementId)) || checker.test(OptionalId.optional(elementId));
    }
}
