package dev.dhyces.trimmed.impl.util;

import dev.dhyces.trimmed.impl.mixin.TagEntryAccessor;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public record OptionalId(ResourceLocation elementId, boolean isRequired) {
    public static OptionalId from(TagEntryAccessor tagEntry) {
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
