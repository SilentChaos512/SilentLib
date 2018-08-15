package net.silentchaos512.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.LocalizationHelper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Deprecated
public class BlockSL extends Block implements IRegistryObject, IHasSubtypes {

  protected final int subBlockCount;
  protected String blockName;
  protected String modId;

  public BlockSL(int subBlockCount, String modId, String name, Material material) {

    super(material);
    this.subBlockCount = subBlockCount;
    this.modId = modId.toLowerCase(Locale.ROOT);
    setTranslationKey(name);
  }

  public boolean hasSubtypes() {

    return subBlockCount > 1;
  }

  public EnumRarity getRarity(int meta) {

    return EnumRarity.COMMON;
  }

  // =======================
  // IRegistryObject methods
  // =======================

  @Override
  public void addRecipes(RecipeMaker recipes) {

  }

  @Override
  public void addOreDict() {

  }

  @Override
  public String getName() {

    return blockName;
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
  public void getModels(Map<Integer, ModelResourceLocation> models) {

    if (hasSubtypes()) {
      for (int i = 0; i < subBlockCount; ++i) {
        models.put(i, new ModelResourceLocation(getFullName() + i, "inventory"));
      }
    } else {
      models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }
  }

  // ===============
  // Block overrides
  // ===============

  @Override
  public int damageDropped(IBlockState state) {

    return hasSubtypes() ? getMetaFromState(state) : 0;
  }

  @Override
  public String getTranslationKey() {

    return "tile." + blockName;
  }

  @Override
  public Block setTranslationKey(String name) {

    this.blockName = name;
    return super.setTranslationKey(name);
  }

  // ==============================
  // Cross Compatibility (MC 10/11)
  // inspired by CompatLayer
  // ==============================

  @Override
  public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity,
      boolean par7) {

    clAddCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity);
  }

  @Deprecated
  protected void clAddCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
      Entity entity) {

    super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, true);
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos p_189540_5_) {

    clOnNeighborChanged(state, world, pos, block);
  }

  @Deprecated
  protected void clOnNeighborChanged(IBlockState state, World world, BlockPos pos, Block block) {

  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
      float hitZ) {

    return clOnBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
  }

  @Deprecated
  protected boolean clOnBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX,
      float hitY, float hitZ) {

    return false;
  }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

    return clGetStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
  }

  @Deprecated
  protected IBlockState clGetStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
      EntityLivingBase placer) {

    return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {

    clGetSubBlocks(Item.getItemFromBlock(this), tab, list);
  }

  @Deprecated
  protected void clGetSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

    if (hasSubtypes()) {
      for (int i = 0; i < subBlockCount; ++i) {
        list.add(new ItemStack(item, 1, i));
      }
    } else {
      list.add(new ItemStack(item));
    }
  }

  // ===========================
  // Cross Compatibility (MC 12)
  // ===========================

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {

    clAddInformation(stack, world, list, flag == TooltipFlags.ADVANCED);
  }

  @SideOnly(Side.CLIENT)
  @Deprecated
  public void clAddInformation(ItemStack stack, World world, List<String> list, boolean advanced) {

    LocalizationHelper loc = SilentLib.instance.getLocalizationHelperForMod(modId);
    if (loc != null) {
      list.addAll(loc.getBlockDescriptionLines(blockName));
    }
  }

  @Override
  public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

    drops.addAll(clGetDrops(world, pos, state, fortune));
  }

  @Deprecated
  public List<ItemStack> clGetDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

    NonNullList<ItemStack> list = NonNullList.create();
    super.getDrops(list, world, pos, state, fortune);
    return list;
  }
}