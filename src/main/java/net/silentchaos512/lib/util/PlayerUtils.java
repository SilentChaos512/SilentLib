package net.silentchaos512.lib.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PlayerUtils {
    private PlayerUtils() {}

    /**
     * Gives the player an item. If it can't be given directly, it spawns an EntityItem. Spawns 1
     * block above player's feet.
     *
     * @param player The player
     * @param stack  The item
     */
    public static void giveItem(Player player, ItemStack stack) {
        ItemStack copy = stack.copy();
        if (!player.getInventory().add(copy)) {
            ItemEntity entityItem = new ItemEntity(player.level(), player.getX(), player.getY(0.5), player.getZ(), copy);
            entityItem.setNoPickUpDelay();
            entityItem.setThrower(player);
            player.level().addFreshEntity(entityItem);
        }
    }

    /**
     * Gets a tag compound from the player's persisted data NBT compound, or creates it if it does
     * not exist. This can be used to save additional data to a player.
     *
     * @param player         The player
     * @param subCompoundKey The key for the tag compound (ideally should contain mod ID)
     * @return The tag compound, creating it if it does not exist.
     */
    public static CompoundTag getPersistedDataSubcompound(Player player, String subCompoundKey) {
        CompoundTag forgeData = player.getPersistentData();
        if (!forgeData.contains(Player.PERSISTED_NBT_TAG)) {
            forgeData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }

        CompoundTag persistedData = forgeData.getCompound(Player.PERSISTED_NBT_TAG).orElseThrow();
        if (!persistedData.contains(subCompoundKey)) {
            persistedData.put(subCompoundKey, new CompoundTag());
        }

        return persistedData.getCompound(subCompoundKey).orElseThrow();
    }
}
