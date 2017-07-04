package net.silentchaos512.lib.block;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

public class BlockContainerSL extends BlockContainer implements IRegistryObject, IHasSubtypes {

  protected final int subBlockCount;
  protected String blockName;
  protected String modId;

  protected BlockContainerSL(int subBlockCount, String modId, String name, Material material) {

    super(material);
    this.subBlockCount = subBlockCount;
    this.modId = modId.toLowerCase();
    setUnlocalizedName(name);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasSubtypes() {

    return subBlockCount > 1;
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
        models.put(i, new ModelResourceLocation(getFullName().toLowerCase() + i, "inventory"));
      }
    } else {
      models.put(0, new ModelResourceLocation(getFullName().toLowerCase(), "inventory"));
    }
  }

  @Override
  public boolean registerModels() {

    return false;
  }

  // ========================
  // BlockContainer overrides
  // ========================

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {

    TileEntity tile = world.getTileEntity(pos);

    if (tile != null && tile instanceof IInventory) {
      InventoryHelper.dropInventoryItems(world, pos, (IInventory) tile);
      world.updateComparatorOutputLevel(pos, this);
    }

    super.breakBlock(world, pos, state);
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
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {

    clOnNeighborChanged(state, world, pos, block);
  }

  protected void clOnNeighborChanged(IBlockState state, World world, BlockPos pos, Block block) {

  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
      EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY,
      float hitZ) {

    return clOnBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
  }

  protected boolean clOnBlockActivated(World world, BlockPos pos, IBlockState state,
      EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX,
      float hitY, float hitZ, int meta, EntityLivingBase placer) {

    return clGetStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
  }

  @SuppressWarnings("deprecation")
  protected IBlockState clGetStateForPlacement(World world, BlockPos pos, EnumFacing facing,
      float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

    return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

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
