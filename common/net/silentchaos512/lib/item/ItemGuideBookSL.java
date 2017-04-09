package net.silentchaos512.lib.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;

public class ItemGuideBookSL extends ItemSL {

  private static final List<ItemGuideBookSL> bookList = Lists.newArrayList();

  // Feel free to change any of these.
  public boolean giveBookOnFirstLogin = true;

  // Do not set yourself.
  @SideOnly(Side.CLIENT)
  public IGuidePage forcedPage;
  public final GuideBook book;
  public final int bookId;

  public ItemGuideBookSL(GuideBook book) {

    this(book, "guide_book");
  }

  public ItemGuideBookSL(GuideBook book, String name) {

    super(1, book.getModId(), name);
    this.book = book;
    this.bookId = bookList.size();

    bookList.add(this);
  }

  public static @Nullable ItemGuideBookSL getBookById(int id) {

    if (id >= 0 && id < bookList.size())
      return bookList.get(id);
    return null;
  }

  @Override
  public EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
      EnumFacing facing, float hitX, float hitY, float hitZ) {

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
  public ActionResult<ItemStack> clOnItemRightClick(World world, EntityPlayer player,
      EnumHand hand) {

    player.openGui(SilentLib.instance, bookId, world, (int) player.posX, (int) player.posY, (int) player.posZ);
    // TODO
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
  }
}
