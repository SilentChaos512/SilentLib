package net.silentchaos512.lib.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.component.LootContainer;
import net.silentchaos512.lib.util.LootUtils;
import net.silentchaos512.lib.util.PlayerUtils;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

/**
 * An item that gives the player items from a loot table when used, similar to a loot bag. The loot table is specified
 * by a {@link LootContainer} data component.
 *
 * @author SilentChaos512
 * @since 3.0.2
 */
public class LootContainerItem extends Item {
    private final boolean listItemsReceived;

    public LootContainerItem(boolean listItemsReceived) {
        this(listItemsReceived, new Item.Properties());
    }

    public LootContainerItem(boolean listItemsReceived, Item.Properties properties) {
        super(properties);
        this.listItemsReceived = listItemsReceived;
    }

    /**
     * Gets a stack with a {@link LootContainer} data component with the given loot table assigned.
     *
     * @param lootTable The loot table
     * @return A new stack of this item with the given loot table
     */
    public ItemStack getStack(ResourceKey<LootTable> lootTable) {
        ItemStack result = new ItemStack(this);
        result.set(SilentLib.LOOT_CONTAINER, new LootContainer(lootTable));
        return result;
    }

    /**
     * Get the loot table in the {@link LootContainer}, if present.
     *
     * @param stack The loot container item
     * @return The loot table, or null if there is none
     */
    @Nullable
    protected ResourceKey<LootTable> getLootTable(ItemStack stack) {
        var lootContainer = stack.get(SilentLib.LOOT_CONTAINER);
        if (lootContainer != null) {
            return lootContainer.lootTable();
        }
        return null;
    }

    protected Collection<ItemStack> getLootDrops(ItemStack heldItem, ServerPlayer player) {
        var lootTable = getLootTable(heldItem);
        if (lootTable == null) {
            return List.of();
        }
        return LootUtils.gift(lootTable, player);
    }

    @Override
    public InteractionResult use(@Nullable Level level, @Nullable Player player, @Nullable InteractionHand hand) {
        if (player == null || hand == null) {
            return InteractionResult.PASS;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        // Generate items from loot table, give to player.
        Collection<ItemStack> lootDrops = this.getLootDrops(heldItem, serverPlayer);

        if (lootDrops.isEmpty()) {
            SilentLib.LOGGER.warn("LootContainerItem has no drops? {}, table={}", heldItem, getLootTable(heldItem));
        }

        lootDrops.forEach(stack -> {
            PlayerUtils.giveItem(serverPlayer, stack);
            if (this.listItemsReceived) {
                listItemReceivedInChat(serverPlayer, stack);
            }
        });

        // Play item pickup sound...
        float pitch = ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, pitch);
        heldItem.shrink(1);

        return InteractionResult.SUCCESS;
    }

    private static void listItemReceivedInChat(ServerPlayer serverPlayer, ItemStack stack) {
        Component itemReceivedText = Component.translatable(
                "item.silentlib.lootContainer.itemReceived",
                stack.getCount(),
                stack.getHoverName());
        serverPlayer.sendSystemMessage(itemReceivedText);
    }
}
