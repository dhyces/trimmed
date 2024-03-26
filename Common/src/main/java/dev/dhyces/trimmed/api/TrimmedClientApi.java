package dev.dhyces.trimmed.api;

import dev.dhyces.trimmed.impl.TrimmedClientApiImpl;

public interface TrimmedClientApi {
    TrimmedClientApi INSTANCE = new TrimmedClientApiImpl();
}
