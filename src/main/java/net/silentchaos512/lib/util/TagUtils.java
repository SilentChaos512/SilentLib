package net.silentchaos512.lib.util;

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
     */
    public static boolean isInSafe(ItemStack stack, ITag<Item> tag) {
        return isInSafe(stack, tag, true);
    }

    private static boolean isInSafe(ItemStack stack, ITag<Item> tag, boolean firstAttempt) {
        try {
            return stack.getItem().isIn(tag);
        } catch (IllegalStateException ex) {
            SilentLib.PROXY.tryFetchTagsHack();
        }
        return firstAttempt && isInSafe(stack, tag, false);
    }
}
