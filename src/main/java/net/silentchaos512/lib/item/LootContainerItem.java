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

import com.google.common.collect.ImmutableList;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
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

    protected static CompoundNBT getData(ItemStack stack) {
        return stack.getOrCreateChildTag(NBT_ROOT);
    }

    /**
     * Get the loot table the item will use. If a loot table if specified in NBT and it is valid,
     * that table is returned. Otherwise, this returns {@link #defaultLootTable}.
     *
     * @param stack The item
     * @return The loot table which will be used
     */
    protected ResourceLocation getLootTable(ItemStack stack) {
        CompoundNBT tags = getData(stack);
        if (tags.contains(NBT_LOOT_TABLE)) {
            String str = tags.getString(NBT_LOOT_TABLE);
            ResourceLocation table = ResourceLocation.tryCreate(str);
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
     * similar to {@link net.minecraft.advancements.AdvancementRewards#apply(ServerPlayerEntity)}.
     *
     * @param heldItem The loot container item being used
     * @param player   The player using the item
     * @return A collection of items to give to the player
     */
    protected Collection<ItemStack> getLootDrops(ItemStack heldItem, ServerPlayerEntity player) {
        return LootUtils.gift(getLootTable(heldItem), player);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;

        ITextComponent textTableName = new StringTextComponent(this.getLootTable(stack).toString()).applyTextStyle(TextFormatting.WHITE);
        tooltip.add(new TranslationTextComponent("item.silentlib.lootContainer.table", textTableName).applyTextStyle(TextFormatting.BLUE));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (!(playerIn instanceof ServerPlayerEntity))
            return ActionResult.func_226248_a_(heldItem);

        // Generate items from loot table, give to player.
        ServerPlayerEntity playerMP = (ServerPlayerEntity) playerIn;
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
        float pitch = ((playerMP.getRNG().nextFloat() - playerMP.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F;
        playerMP.world.playSound(null, playerMP.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, pitch);
        heldItem.shrink(1);
        return ActionResult.func_226248_a_(heldItem);
    }

    private static void listItemReceivedInChat(ServerPlayerEntity playerMP, ItemStack stack) {
        ITextComponent itemReceivedText = new TranslationTextComponent(
                "item.silentlib.lootContainer.itemReceived",
                stack.getCount(),
                stack.getDisplayName());
        playerMP.sendMessage(itemReceivedText);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(this.getStack());
        }
    }
}
