package dev.greenhouseteam.enchantmentconfig.mixin.api.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionalFieldCodec.class)
public interface OptionalFieldCodecAccessor<A> {
    @Accessor("name")
    @Final String enchantmentconfig$getName();
    @Accessor("elementCodec")
    @Final Codec<A> enchantmentconfig$getElementCodec();
}
