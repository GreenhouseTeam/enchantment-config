package dev.greenhouseteam.enchantmentconfig.api.config.field;

import com.mojang.serialization.Codec;

import java.util.Objects;
import java.util.Optional;

public record ExtraFieldType<T>(Codec<T> codec) {
    public int hashCode() {
        return Objects.hash(this.codec());
    }

    // TODO: Document this.
    /**
     *
     * @param currentConfiguration
     * @param oldConfiguration
     * @param globalConfiguration
     * @param priority
     * @param oldPriority
     * @param globalPriority
     *
     * @return                                  A merged field of this type.
     * @throws UnsupportedOperationException    If not overridden.
     */
    public T merge(T currentConfiguration, T oldConfiguration, Optional<T> globalConfiguration, int priority, int oldPriority, int globalPriority) {
        throw new UnsupportedOperationException("ExtraFieldType#merge should be overridden.");
    }

}
