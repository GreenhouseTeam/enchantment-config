package dev.greenhouseteam.enchantmentconfig.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.platform.services.IEnchantmentConfigPlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@AutoService(IEnchantmentConfigPlatformHelper.class)
public class NeoEnchantmentConfigPlatformHelper implements IEnchantmentConfigPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Registry<EnchantmentType<?>> getEnchantmentTypeRegistry() {
        return EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY;
    }

    private final Map<String, DeferredRegister<EnchantmentType<?>>> deferredRegisterMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends EnchantmentType<?>> Holder<T> registerEnchantmentType(ResourceLocation typeId, T enchantmentType) {
        String modId = typeId.getNamespace();
        if (!deferredRegisterMap.containsKey(modId)) {
            DeferredRegister<EnchantmentType<?>> register = DeferredRegister.create(EnchantmentConfigRegistries.ENCHANTMENT_TYPE_KEY, modId);
            Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(modId);
            if (modContainer.isEmpty())
                throw new NullPointerException("Could not find mod container for id " + modId);
            final ModContainer cont = modContainer.get();
            if (cont instanceof FMLModContainer fmlModContainer) {
                register.register(Objects.requireNonNull(fmlModContainer.getEventBus()));
            } else {
                throw new ClassCastException("The container of the mod " + modId + " is not a FML one!");
            }
            deferredRegisterMap.put(modId, register);
        }
        return (Holder<T>) deferredRegisterMap.get(modId).register(typeId.getPath(), () -> enchantmentType);
    }
}