package net.silentchaos512.lib.registry.updated.deferredregisters;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.registry.updated.registryobjects.RecipeSerializerRegistryObject;
import net.silentchaos512.lib.registry.updated.registryobjects.RecipeTypeRegistryObject;
import net.silentchaos512.lib.registry.updated.registryobjects.UpdatedRegistryObjectWrapper;

import java.util.function.Supplier;

public class RecipeTypeDeferredRegister extends DeferredRegisterWrapper<RecipeType<?>> {
    private final DeferredRegister<RecipeSerializer<?>> serializerDeferredRegister = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, super.modid);

    public RecipeTypeDeferredRegister(String modid) {
        super(modid, ForgeRegistries.RECIPE_TYPES);
    }

    @Deprecated
    @Override
    public <U extends RecipeType<?>> UpdatedRegistryObjectWrapper<?> register(String name, Supplier<U> supplier) {
        throw new IllegalStateException("Please use another implementation for registration");
    }

    public <T extends Recipe<?>> RecipeTypeRegistryObject<T> register(String name) {
        return new RecipeTypeRegistryObject<>(this, name, () -> RecipeType.simple(SilentLib.getId(name)));
    }

    public <T extends Recipe<?>>RecipeSerializerRegistryObject<T> registerSerializer(String name, Supplier<RecipeSerializer<T>> serializerSupplier) {
        return new RecipeSerializerRegistryObject<>(this.serializerDeferredRegister, super.modid, name, serializerSupplier);
    }

    @Override
    public void registerBus(IEventBus eventBus) {
        super.registerBus(eventBus);
        this.serializerDeferredRegister.register(eventBus);
    }
}
