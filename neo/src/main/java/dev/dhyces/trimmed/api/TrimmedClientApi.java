package dev.dhyces.trimmed.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TrimmedClientApi {
    /**
     * Must be a valid mod id for your mod
     * @return A valid mod id
     */
    String value();
}
