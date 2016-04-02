package net.silentchaos512.lib.block;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;

public class BlockContainerSL extends BlockContainer implements IRegistryObject, IHasSubtypes {

  protected final int subBlockCount;
  protected boolean hasSubtypes = false;
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

    if (hasSubtypes) {
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
  public void getSubBlocks(Item item, CreativeTabs tab, List list) {

    if (hasSubtypes) {
      for (int i = 0; i < subBlockCount; ++i) {
        list.add(new ItemStack(item, 1, i));
      }
    } else {
      list.add(new ItemStack(item));
    }
  }

  @Override
  public int damageDropped(IBlockState state) {

    return hasSubtypes ? getMetaFromState(state) : 0;
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
}
