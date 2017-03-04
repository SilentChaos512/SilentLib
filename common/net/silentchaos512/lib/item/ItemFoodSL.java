package net.silentchaos512.lib.item;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.util.LocalizationHelper;

public class ItemFoodSL extends ItemFood implements IRegistryObject {

  protected int subItemCount;
  protected String itemName;
  protected String modId;

  public ItemFoodSL(int subItemCount, String modId, String name, int amount, float saturation,
      boolean isWolfFood) {

    super(amount, saturation, isWolfFood);
    this.subItemCount = subItemCount;
    this.modId = modId.toLowerCase();
    setUnlocalizedName(name);
  }

  public ItemFoodSL(int subItemCount, String modId, String name) {

    this(subItemCount, modId, name, 0, 0f, false);
  }

  // =======================
  // IRegistryObject methods
  // =======================

  @Override
  public void addRecipes() {

  }

  @Override
  public void addOreDict() {

  }

  @Override
  public String getName() {

    return itemName;
  }

  @Override
  public String getFullName() {

    return modId + ":" + getName();
  }

  @Override
  public String getModId() {

    return modId;
  }

  @Override
  public List<ModelResourceLocation> getVariants() {

    if (hasSubtypes) {
      List<ModelResourceLocation> models = Lists.newArrayList();
      for (int i = 0; i < subItemCount; ++i) {
        models.add(new ModelResourceLocation(getFullName() + i, "inventory"));
      }
      return models;
    }
    return Lists.newArrayList(new ModelResourceLocation(getFullName(), "inventory"));
  }

  @Override
  public boolean registerModels() {

    // Let SRegistry handle model registration by default. Override if necessary.
    return false;
  }

  // ==============
  // Item overrides
  // ==============

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List<String> list,
      boolean advanced) {

    LocalizationHelper loc = SilentLib.instance.getLocalizationHelperForMod(modId);
    if (loc != null) {
      String name = getNameForStack(stack);
      list.addAll(loc.getItemDescriptionLines(name));
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return "item." + modId + ":" + getNameForStack(stack);
  }

  public String getNameForStack(ItemStack stack) {

    return itemName + (hasSubtypes ? stack.getItemDamage() : "");
  }

  @Override
  public Item setUnlocalizedName(String name) {

    this.itemName = name;
    return super.setUnlocalizedName(name);
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

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {

    clGetSubItems(itemIn, tab, subItems);
  }

  @SideOnly(Side.CLIENT)
  protected void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

    super.getSubItems(itemIn, tab, (NonNullList<ItemStack>) subItems);
  }
}
