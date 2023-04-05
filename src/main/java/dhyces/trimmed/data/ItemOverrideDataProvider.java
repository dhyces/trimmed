package dhyces.trimmed.data;

import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.util.concurrent.CompletableFuture;

public abstract class ItemOverrideDataProvider implements DataProvider {

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
