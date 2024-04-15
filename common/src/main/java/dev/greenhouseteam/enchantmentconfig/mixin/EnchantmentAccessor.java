package dev.greenhouseteam.enchantmentconfig.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Enchantment.class)
public interface EnchantmentAccessor {
    @Accessor("definition")
    Enchantment.EnchantmentDefinition getDefinition();
}
