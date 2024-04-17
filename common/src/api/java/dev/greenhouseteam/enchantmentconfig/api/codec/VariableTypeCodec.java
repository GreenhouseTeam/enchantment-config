package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@ApiStatus.Internal
public class VariableTypeCodec extends MapCodec<VariableType<?>> {
    private static final Map<ResourceLocation, VariableType<?>> VARIABLE_TO_TYPE = new HashMap<>();
    public static final VariableTypeCodec INSTANCE = new VariableTypeCodec();

    protected VariableTypeCodec() {

    }

    @Override
    public <T> DataResult<VariableType<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
        var map = input.get("value_type");
        if (map == null) {
            var type = input.get("type");
            var typeResult = ops.getStringValue(type);
            if (typeResult.isError())
                return DataResult.error(() -> typeResult.error().get().message());
            ResourceLocation id = new ResourceLocation(typeResult.result().get());
            if (VARIABLE_TO_TYPE.containsKey(id))
                return DataResult.success(VARIABLE_TO_TYPE.get(id));
            return DataResult.error(() -> "Missing 'value_type' field.");
        }
        return DataResult.success(EnchantmentConfigRegistries.VARIABLE_TYPE.byNameCodec().decode(ops, map).result().map(Pair::getFirst).orElseThrow());
    }

    @Override
    public <T> RecordBuilder<T> encode(VariableType<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        ResourceLocation id = EnchantmentConfigRegistries.VARIABLE_TYPE.getKey(input);
        if (VARIABLE_TO_TYPE.containsKey(id))
            return prefix;
        return prefix.add("value_type", ops.createString(id.toString()));
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.of();
    }

    public static void addToVariableToTypeMap(ResourceLocation id, VariableType<?> type) {
        VARIABLE_TO_TYPE.put(id, type);
    }
}
