package dev.greenhouseteam.enchantmentconfig.api.config.field;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.entrypoint.IEnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.util.IEnchantmentConfigGetter;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Optional;

/**
 * A class that specifies the type of extra field and its related codec.
 * Used in {@link IEnchantmentConfigAssigner#addExtraField(ResourceKey, String, ExtraFieldType)}.
 * Developers are able to get their extra fields from {@link IEnchantmentConfigGetter#}
 *
 * @param codec The codec for this extra field type, used for serializing and deserializing.
 * @param <T>   The type of this extra field.
 */
public record ExtraFieldType<T>(Codec<T> codec) {

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public Codec<Object> objectCodec() {
        return this.codec().xmap(t -> t, object -> (T)object);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;

        if (!(other instanceof ExtraFieldType<?> extraFieldType))
            return false;

        return this.codec().equals(extraFieldType.codec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codec());
    }

    // TODO: Document this.
    /**
     *
     * @param currentConfiguration
     * @param oldConfiguration
     * @param priority
     * @param oldPriority
     *
     * @return                                  A merged field of this type.
     * @throws UnsupportedOperationException    If not overridden.
     */
    public T merge(T currentConfiguration, T oldConfiguration, int priority, int oldPriority) {
        throw new UnsupportedOperationException("ExtraFieldType#merge should be overridden.");
    }

}
