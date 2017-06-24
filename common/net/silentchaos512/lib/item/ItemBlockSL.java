package net.silentchaos512.lib.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.block.BlockSL;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.util.LocalizationHelper;

public class ItemBlockSL extends ItemBlock {

  protected String blockName = "null";
  protected String unlocalizedName = "null";
  protected String modId = "null";

  public ItemBlockSL(Block block) {

    super(block);
    setMaxDamage(0);

    if (block instanceof IHasSubtypes) {
      setHasSubtypes(((IHasSubtypes) block).hasSubtypes());
    }
    if (block instanceof IRegistryObject) {
      IRegistryObject obj = (IRegistryObject) block;
      blockName = obj.getName();
      unlocalizedName = "tile." + obj.getFullName();
      modId = obj.getModId();
    }
  }

  @Override
  public int getMetadata(int meta) {

    return meta & 0xF;
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {

    if (block instanceof BlockSL) {
      return ((BlockSL) block).getRarity(stack.getItemDamage());
    }
    return super.getRarity(stack);
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return unlocalizedName + (hasSubtypes ? stack.getItemDamage() & 0xF : "");
  }

  public String getNameForStack(ItemStack stack) {

    return blockName + (hasSubtypes ? stack.getItemDamage() & 0xF : "");
  }

  // ==============================
  // Cross Compatibility (MC 10/11)
  // inspired by CompatLayer
  // ==============================

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn,
      EnumHand hand) {

    return clOnItemRightClick(worldIn, playerIn, hand);
  }

  protected ActionResult<ItemStack> clOnItemRightClick(World worldIn, EntityPlayer playerIn,
      EnumHand hand) {

    return super.onItemRightClick(worldIn, playerIn, hand);
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
      EnumFacing facing, float hitX, float hitY, float hitZ) {

    return clOnItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }

  protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos,
      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {

    clGetSubItems(this, tab, subItems);
  }

  protected void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

    super.getSubItems(tab, (NonNullList<ItemStack>) subItems);
  }

  // ==============================
  // Cross Compatibility (MC 12)
  // inspired by CompatLayer
  // ==============================

  @Override
  public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {

    clAddInformation(stack, world, list, flag == TooltipFlags.ADVANCED);
  }

  public void clAddInformation(ItemStack stack, World world, List<String> list, boolean advanced) {

    // Get tooltip from block? (New method)
    int length = list.size();
    // FIXME
    // block.addInformation(stack, player, list, advanced);

    // If block doesn't add anything, use the old method.
    if (length == list.size()) {
      LocalizationHelper loc = SilentLib.instance.getLocalizationHelperForMod(modId);
      if (loc != null) {
        String name = getNameForStack(stack);
        list.addAll(loc.getBlockDescriptionLines(name));
      }
    }
  }
}
