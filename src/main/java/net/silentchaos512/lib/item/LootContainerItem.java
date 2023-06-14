/*
 * Silent Lib -- LootContainerItem
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.util.LootUtils;
import net.silentchaos512.lib.util.PlayerUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * An item that gives the player items from a loot table when used, similar to a loot bag. A default
 * loot table must be specified, but ultimately an NBT tag is used to determine which loot table to
 * pull items from. This could be extended to not use loot tables (see {@link
 * #getLootDrops(ItemStack, ServerPlayerEntity)}).
 *
 * @author SilentChaos512
 * @since 3.0.2
 */
public class LootContainerItem extends Item {
    private static final String NBT_ROOT = SilentLib.MOD_ID + ".LootContainer";
    private static final String NBT_LOOT_TABLE = "LootTable";
    private static final boolean DEFAULT_LIST_ITEMS_RECEIVED = true;

    private final ResourceLocation defaultLootTable;
    private final boolean listItemsReceived;

    public LootContainerItem(ResourceLocation defaultLootTable) {
        this(defaultLootTable, DEFAULT_LIST_ITEMS_RECEIVED, new Item.Properties());
    }

    public LootContainerItem(ResourceLocation defaultLootTable, boolean listItemsReceived) {
        this(defaultLootTable, listItemsReceived, new Item.Properties());
    }

    public LootContainerItem(ResourceLocation defaultLootTable, Item.Properties properties) {
        this(defaultLootTable, DEFAULT_LIST_ITEMS_RECEIVED, properties);
    }

    public LootContainerItem(ResourceLocation defaultLootTable, boolean listItemsReceived, Item.Properties properties) {
        super(properties);
        this.defaultLootTable = defaultLootTable;
        this.listItemsReceived = listItemsReceived;
    }

    /**
     * Get a stack of this item with the default loot table.
     *
     * @return A stack with appropriate NBT tags set and stack size of one
     */
    public ItemStack getStack() {
        return getStack(this.defaultLootTable);
    }

    /**
     * Get a stack of this item with the specified loot table.
     *
     * @param lootTable The loot table to assign to the stack
     * @return A stack with appropriate NBT tags set and stack size of one
     */
    public ItemStack getStack(ResourceLocation lootTable) {
        ItemStack result = new ItemStack(this);
        getData(result).putString(NBT_LOOT_TABLE, lootTable.toString());
        return result;
    }

    protected static CompoundTag getData(ItemStack stack) {
        return stack.getOrCreateTagElement(NBT_ROOT);
    }

    /**
     * Get the loot table the item will use. If a loot table if specified in NBT and it is valid,
     * that table is returned. Otherwise, this returns {@link #defaultLootTable}.
     *
     * @param stack The item
     * @return The loot table which will be used
     */
    protected ResourceLocation getLootTable(ItemStack stack) {
        CompoundTag tags = getData(stack);
        if (tags.contains(NBT_LOOT_TABLE)) {
            String str = tags.getString(NBT_LOOT_TABLE);
            ResourceLocation table = ResourceLocation.tryParse(str);
            if (table != null) {
                return table;
            }
        }
        return this.defaultLootTable;
    }

    /**
     * Set the loot table for the given item stack.
     *
     * @param stack     The item
     * @param lootTable The loot table
     */
    public static void setLootTable(ItemStack stack, ResourceLocation lootTable) {
        getData(stack).putString(NBT_LOOT_TABLE, lootTable.toString());
    }

    /**
     * Get the items to give the player when used. By default, this uses the loot table specified in
     * the NBT of {@code heldItem}. Can be overridden for different behavior. This implementation is
     * similar to {@link net.minecraft.advancements.AdvancementRewards#grant(ServerPlayerEntity)}.
     *
     * @param heldItem The loot container item being used
     * @param player   The player using the item
     * @return A collection of items to give to the player
     */
    protected Collection<ItemStack> getLootDrops(ItemStack heldItem, ServerPlayer player) {
        return LootUtils.gift(getLootTable(heldItem), player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;

        Component textTableName = Component.literal(this.getLootTable(stack).toString()).withStyle(ChatFormatting.WHITE);
        tooltip.add(Component.translatable("item.silentlib.lootContainer.table", textTableName).withStyle(ChatFormatting.BLUE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (!(playerIn instanceof ServerPlayer))
            return InteractionResultHolder.success(heldItem);

        // Generate items from loot table, give to player.
        ServerPlayer playerMP = (ServerPlayer) playerIn;
        Collection<ItemStack> lootDrops = this.getLootDrops(heldItem, playerMP);

        if (lootDrops.isEmpty())
            SilentLib.LOGGER.warn("LootContainerItem has no drops? {}, table={}", heldItem, getLootTable(heldItem));

        lootDrops.forEach(stack -> {
            PlayerUtils.giveItem(playerMP, stack);
            if (this.listItemsReceived) {
                listItemReceivedInChat(playerMP, stack);
            }
        });

        // Play item pickup sound...
        float pitch = ((playerMP.getRandom().nextFloat() - playerMP.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
        playerMP.level().playSound(null, playerMP.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, pitch);
        heldItem.shrink(1);
        return InteractionResultHolder.success(heldItem);
    }

    private static void listItemReceivedInChat(ServerPlayer playerMP, ItemStack stack) {
        Component itemReceivedText = Component.translatable(
                "item.silentlib.lootContainer.itemReceived",
                stack.getCount(),
                stack.getHoverName());
        playerMP.sendSystemMessage(itemReceivedText);
    }
}
