package dhyces.trimmed.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.TrimmedClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public final class CodecUtil {
    public static final Codec<Identifier> TRIMMED_IDENTIFIER = Codec.STRING.xmap(
            s -> Identifier.tryParse(s.contains(":") ? s : TrimmedClient.MODID + ":" + s),
            Identifier::toString
    );

    public static final Codec<ModelIdentifier> MODEL_IDENTIFIER_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                if (s.contains("#")) {
                    String[] identifierModelSplit = s.split("#");
                    try {
                        Identifier id = new Identifier(identifierModelSplit[0]);
                        return DataResult.success(new ModelIdentifier(id, identifierModelSplit[1]));
                    } catch (InvalidIdentifierException e) {
                        return DataResult.error(e::getMessage);
                    }
                }
                return DataResult.success(new ModelIdentifier(new Identifier(s), "inventory"));
            },
            ModelIdentifier::toString
    );
}
