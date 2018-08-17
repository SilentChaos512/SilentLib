/*
 * SilentLib - ItemGuideBookSL
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemGuideBookSL extends Item {
    private static final List<ItemGuideBookSL> bookList = new ArrayList<>();

    // Feel free to change any of these.
    public boolean giveBookOnFirstLogin = true;

    // Do not set yourself.
    @SideOnly(Side.CLIENT)
    public IGuidePage forcedPage;
    public final GuideBook book;
    public final int bookId;

    public ItemGuideBookSL(GuideBook book) {
        this.book = book;
        this.bookId = bookList.size();

        bookList.add(this);
    }

    @Nullable
    public static ItemGuideBookSL getBookById(int id) {
        return id >= 0 && id < bookList.size() ? bookList.get(id) : null;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            ItemStack blockStack = new ItemStack(block, 1, block.damageDropped(state));
            IGuidePage page = GuideBookUtils.findFirstPageForStack(book, blockStack);
            if (page != null) {
                if (world.isRemote) {
                    forcedPage = page;
                }
                this.onItemRightClick(world, player, hand);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.openGui(SilentLib.instance, bookId, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        // TODO
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.ITALIC + I18n.format(this.getTranslationKey(stack) + ".desc"));
    }
}
