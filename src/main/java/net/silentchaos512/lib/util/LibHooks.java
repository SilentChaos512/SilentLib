package net.silentchaos512.lib.util;

import net.minecraft.block.ComposterBlock;
import net.minecraft.util.IItemProvider;
import net.silentchaos512.lib.SilentLib;

/**
 * Adds some ways to access certain things (like registering items for use in the composter). I
 * assume Forge will eventually add their own hooks, but I can put up with access transformers until
 * then.
 */
public final class LibHooks {
    private LibHooks() {
        throw new IllegalAccessError("Utility class");
    }

    public static void registerCompostable(float chance, IItemProvider item) {
        synchronized (ComposterBlock.CHANCES) {
            try {
                ComposterBlock.CHANCES.put(item, chance);
            } catch (Exception ex) {
                SilentLib.LOGGER.error("Failed to register compostable item: {}, chance {}", item.asItem().getRegistryName(), chance);
                SilentLib.LOGGER.catching(ex);
            }
        }
    }
}
