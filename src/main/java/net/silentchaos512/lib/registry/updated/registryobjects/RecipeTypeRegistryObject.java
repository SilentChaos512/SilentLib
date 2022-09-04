package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.silentchaos512.lib.registry.updated.deferredregisters.DeferredRegisterWrapper;

import java.util.function.Supplier;

public class RecipeTypeRegistryObject<T extends Recipe<?>> extends UpdatedRegistryObjectWrapper<RecipeType<?>> {
    public RecipeTypeRegistryObject(DeferredRegisterWrapper<RecipeType<?>> deferredRegister, String name, Supplier<RecipeType<T>> supplier) {
        super(deferredRegister, name, supplier);
    }
}
