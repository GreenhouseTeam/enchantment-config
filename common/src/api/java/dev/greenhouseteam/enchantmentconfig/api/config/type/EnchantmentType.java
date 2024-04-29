package dev.greenhouseteam.enchantmentconfig.api.config.type;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.ExtraFieldsCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.GlobalEnchantmentFields;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public class EnchantmentType<T extends EnchantmentConfiguration> {
    private final MapCodec<T> codec;
    private final ResourceLocation path;
    private final ResourceKey<Enchantment> enchantment;
    private final Map<String, ExtraFieldType<?>> extraFieldTypes = Maps.newHashMap();

    /**
     * Constructs an EnchantmentType based on an enchantment.
     *
     * @param codec         The codec for this EnchantmentType's associated config.
     * @param enchantment   The associated enchantment for this type.
     *                      The path in JSON will be the same as this
     *                      enchantment's id.
     */
    public EnchantmentType(MapCodec<T> codec, ResourceKey<Enchantment> enchantment) {
        this.codec = codec;
        this.enchantment = enchantment;
        this.path = enchantment.location();
    }

    public ResourceLocation getPath() {
        return this.path;
    }

    /**
     * Gets the associated enchantment's {@link ResourceKey}, or null if it doesn't have one.
     *
     * @return  The enchantment {@link ResourceKey}.
     */
    public ResourceKey<Enchantment> getEnchantment() {
        return this.enchantment;
    }

    public Map<String, ExtraFieldType<?>> getExtraFieldTypes() {
        return this.extraFieldTypes;
    }

    public void addExtraFieldType(String path, ExtraFieldType<?> extraFieldType) {
        this.extraFieldTypes.put(path, extraFieldType);
    }

    @ApiStatus.Internal
    public Codec<ConfiguredEnchantment<T, EnchantmentType<T>>> codec() {
        return RecordCodecBuilder.create(inst -> inst.group(
                codec.forGetter(ConfiguredEnchantment::getConfiguration),
                GlobalEnchantmentFields.CODEC.forGetter(ConfiguredEnchantment::getGlobalFields),
                new ExtraFieldsCodec(extraFieldTypes).forGetter(ConfiguredEnchantment::getExtraFields)
        ).apply(inst, (t1, t2, t3) -> new ConfiguredEnchantment<>(this, t1, t2, t3)));
    }

}
