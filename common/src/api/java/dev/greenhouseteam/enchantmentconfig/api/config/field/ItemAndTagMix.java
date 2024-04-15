package dev.greenhouseteam.enchantmentconfig.api.config.field;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

import java.util.Optional;

public record ItemAndTagMix(Optional<Holder<Item>> item, Optional<CompoundTag> tag) {

    public static final Codec<ItemAndTagMix> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BuiltInRegistries.ITEM.holderByNameCodec().optionalFieldOf("item").forGetter(ItemAndTagMix::item),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(ItemAndTagMix::tag)
    ).apply(inst, ItemAndTagMix::new));

}
