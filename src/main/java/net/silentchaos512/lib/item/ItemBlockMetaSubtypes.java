/*
 * Silent Lib -- ItemBlockMetaSubtypes
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

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.block.BlockMetaSubtypes;

public class ItemBlockMetaSubtypes extends net.minecraft.item.ItemBlock {
    private final int subtypeCount;

    public ItemBlockMetaSubtypes(BlockMetaSubtypes block) {
        this(block, block.getSubtypeCount());
    }

    public ItemBlockMetaSubtypes(Block block, int subtypeCount) {
        super(block);
        this.subtypeCount = subtypeCount;
        setMaxDamage(0);
        setHasSubtypes(subtypeCount > 1);
    }

    @Override
    public int getMetadata(int damage) {
        return damage & 0xF;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + getMetadata(stack.getItemDamage());
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) return;
        for (int i = 0; i < this.subtypeCount; ++i)
            items.add(new ItemStack(this, 1, i));
    }
}
