package net.silentchaos512.lib.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.*;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.stream.Stream;

public final class TagUtils {
    private TagUtils() {throw new IllegalAccessError("Utility class");}

    public static boolean contains(TagKey<Item> tag, ItemStack stack) {
        return contains(ForgeRegistries.ITEMS, tag, stack.getItem());
    }

    public static boolean contains(TagKey<Block> tag, BlockState state) {
        return contains(ForgeRegistries.BLOCKS, tag, state.getBlock());
    }

    public static boolean contains(TagKey<EntityType<?>> tag, Entity entity) {
        return contains(ForgeRegistries.ENTITIES, tag, entity.getType());
    }

    public static <T extends IForgeRegistryEntry<T>> boolean contains(TagKey<T> tag, T obj) {
        ForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(obj.getRegistryName());
        return contains(registry, tag, obj);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean contains(IForgeRegistry<T> registry, TagKey<T> tag, T obj) {
        ITagManager<T> tagManager = registry.tags();
        return tagManager != null && tagManager.getTag(tag).contains(obj);
    }

    public static Stream<Item> getItemsInTag(TagKey<Item> tag) {
        return getObjectsInTag(ForgeRegistries.ITEMS, tag);
    }

    public static Stream<Block> getBlocksInTag(TagKey<Block> tag) {
        return getObjectsInTag(ForgeRegistries.BLOCKS, tag);
    }

    public static <T extends IForgeRegistryEntry<T>> Stream<T> getObjectsInTag(IForgeRegistry<T> registry, TagKey<T> tag) {
        ITagManager<T> tagManager = registry.tags();
        if (tagManager != null) {
            return tagManager.getTag(tag).stream();
        }
        return Stream.of();
    }
}