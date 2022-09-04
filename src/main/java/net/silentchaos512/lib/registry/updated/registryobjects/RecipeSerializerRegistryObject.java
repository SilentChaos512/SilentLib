package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RecipeSerializerRegistryObject<T extends Recipe<?>> extends UpdatedRegistryObjectWrapper<RecipeSerializer<?>> {
    public RecipeSerializerRegistryObject(DeferredRegister<RecipeSerializer<?>> deferredRegister, String modid, String name, Supplier<RecipeSerializer<T>> supplier) {
        super(deferredRegister, modid, name, supplier);
    }
}
