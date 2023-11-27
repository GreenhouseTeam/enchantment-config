package dev.greenhouseteam.enchantmentconfig.api.config.field;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

import java.util.Optional;

public record ItemAndTagMix(Optional<Holder<Item>> item, Optional<CompoundTag> tag) {

    public static final Codec<ItemAndTagMix> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(BuiltInRegistries.ITEM.holderByNameCodec(), "item").forGetter(ItemAndTagMix::item),
            ExtraCodecs.strictOptionalField(CompoundTag.CODEC, "tag").forGetter(ItemAndTagMix::tag)
    ).apply(inst, ItemAndTagMix::new));

}
