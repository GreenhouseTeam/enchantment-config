package dev.greenhouseteam.enchantmentconfig.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.DamageEnchantmentConfiguration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {
    @Shadow @Final private Optional<TagKey<EntityType<?>>> targets;

    @ModifyReturnValue(method = "getDamageBonus", at = @At("RETURN"))
    private float enchantmentconfig$modifyDamageValue(float original, int level, @Nullable EntityType<?> targetType) {
        ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig((DamageEnchantment)(Object)this);
        if (configured == null)
            return original;

        // TODO: Set up removals within config format.
        if (configured.getConfiguration() instanceof DamageEnchantmentConfiguration damageConfig && (damageConfig.affectedEntities().isEmpty() && (targets.isEmpty() || targetType != null && targetType.is(targets.get())) || targetType != null && damageConfig.affectedEntities().isPresent() && damageConfig.affectedEntities().get().stream().anyMatch(targetType::is)))
            return damageConfig.getBonusDamageAmount(level, original);

        return original;
    }
}
