package dhyces.trimmed.impl.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import java.util.function.Predicate;

public record OptionalTagElement(ResourceLocation elementId, boolean isRequired) {
    public static OptionalTagElement from(TagEntry tagEntry) {
        return new OptionalTagElement(tagEntry.getId(), tagEntry.isRequired());
    }

    public static boolean checkEither(ResourceLocation elementId, Predicate<OptionalTagElement> checker) {
        return checker.test(new OptionalTagElement(elementId, true)) || checker.test(new OptionalTagElement(elementId, false));
    }
}
