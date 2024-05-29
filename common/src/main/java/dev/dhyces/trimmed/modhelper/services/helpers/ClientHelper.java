package dev.dhyces.trimmed.modhelper.services.helpers;

import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.ModApiConsumer;

import java.util.List;

public interface ClientHelper {
    List<ModApiConsumer<TrimmedClientApiEntrypoint>> getClientApiConsumers();
}
