package net.silentchaos512.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.silentchaos512.lib.SilentLib;

public final class TagUtils {
    private TagUtils() {throw new IllegalAccessError("Utility class");}

    /**
     * Check if the item is in the given tag and also prevents and works around "tag not bound"
     * crashes. If the workaround hack fails, this will return false.
     *
     * @param stack The item
     * @param tag   The tag
     * @return True if the item is in the tag, false if it is not or the workaround hack fails.
     * @deprecated Use {@link #containsSafe(ITag, ItemStack)} instead (renamed for consistency)
     */
    @Deprecated
    public static boolean isInSafe(ItemStack stack, ITag<Item> tag) {
        return containsSafe(tag, stack.getItem(), true);
    }

    public static boolean containsSafe(ITag<Item> tag, ItemStack stack) {
        return containsSafe(tag, stack.getItem());
    }

    public static boolean containsSafe(ITag<Block> tag, BlockState state) {
        return containsSafe(tag, state.getBlock());
    }

    public static boolean containsSafe(ITag<EntityType<?>> tag, Entity entity) {
        return containsSafe(tag, entity.getType());
    }

    /**
     * Checks if the given tag contains the given object and also works around the "tag used before
     * bound" crash that some mod packs produce.
     * <p>
     * Also see the other {@code containsSafe} methods, which may save you some keystrokes.
     *
     * @param tag The tag
     * @param obj The object (Item, Block, EntityType, etc.)
     * @param <T> The object type
     * @return True if the tag contains the object, false if it does not or the workaround hack
     * fails to fetch unbound tags
     */
    public static <T> boolean containsSafe(ITag<T> tag, T obj) {
        return containsSafe(tag, obj, true);
    }

    private static <T> boolean containsSafe(ITag<T> tag, T obj, boolean firstAttempt) {
        try {
            return tag.contains(obj);
        } catch (IllegalStateException ex) {
            SilentLib.PROXY.tryFetchTagsHack();
        }
        return firstAttempt && containsSafe(tag, obj, false);
    }
}
