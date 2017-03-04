package net.silentchaos512.lib.block;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;

public class BlockSL extends Block implements IRegistryObject, IHasSubtypes {

  protected final int subBlockCount;
  protected String blockName;
  protected String modId;

  public BlockSL(int subBlockCount, String modId, String name, Material material) {

    super(material);
    this.subBlockCount = subBlockCount;
    this.modId = modId.toLowerCase();
    setUnlocalizedName(name);
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
  public void addRecipes() {

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
  public List<ModelResourceLocation> getVariants() {

    if (hasSubtypes()) {
      List<ModelResourceLocation> models = Lists.newArrayList();
      for (int i = 0; i < subBlockCount; ++i) {
        models.add(new ModelResourceLocation(getFullName() + i, "inventory"));
      }
      return models;
    }
    return Lists.newArrayList(new ModelResourceLocation(getFullName(), "inventory"));
  }

  @Override
  public boolean registerModels() {

    return false;
  }

  // ===============
  // Block overrides
  // ===============

  @Override
  public int damageDropped(IBlockState state) {

    return hasSubtypes() ? getMetaFromState(state) : 0;
  }

  @Override
  public String getUnlocalizedName() {

    return "tile." + blockName;
  }

  @Override
  public Block setUnlocalizedName(String name) {

    this.blockName = name;
    return super.setUnlocalizedName(name);
  }

  // ==============================
  // Cross Compatibility (MC 10/11)
  // inspired by CompatLayer
  // ==============================

  @SuppressWarnings("deprecation")
  @Override
  public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos,
      AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity) {

    clAddCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity);
  }

  @SuppressWarnings("deprecation")
  protected void clAddCollisionBoxToList(IBlockState state, World world, BlockPos pos,
      AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity) {

    super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block,
      BlockPos p_189540_5_) {

    clOnNeighborChanged(state, world, pos, block);
  }

  protected void clOnNeighborChanged(IBlockState state, World world, BlockPos pos, Block block) {

  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
      EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

    return clOnBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
  }

  protected boolean clOnBlockActivated(World world, BlockPos pos, IBlockState state,
      EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

    return clGetStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
  }

  @SuppressWarnings("deprecation")
  protected IBlockState clGetStateForPlacement(World world, BlockPos pos, EnumFacing facing,
      float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

    return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

    clGetSubBlocks(item, tab, list);
  }

  protected void clGetSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

    if (hasSubtypes()) {
      for (int i = 0; i < subBlockCount; ++i) {
        list.add(new ItemStack(item, 1, i));
      }
    } else {
      list.add(new ItemStack(item));
    }
  }
}
