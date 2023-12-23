package dev.dhyces.trimmed.impl.mixin.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteSources.class)
public interface AtlasSourceManagerAccessor {

    @Invoker
    static SpriteSourceType invokeRegister(String pName, Codec<? extends SpriteSource> pCodec) {
        throw new AssertionError();
    }
}
