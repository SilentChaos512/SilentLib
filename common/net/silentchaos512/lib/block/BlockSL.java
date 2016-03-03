package net.silentchaos512.lib.block;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.registry.IRegistryObject;

public class BlockSL extends Block implements IRegistryObject {

  protected int subBlockCount;
  protected boolean hasSubtypes = false;
  protected String blockName;
  protected String modId;

  public BlockSL(int subBlockCount, String modId, String name, Material material) {

    super(material);
    this.modId = modId.toLowerCase();
    setUnlocalizedName(name);
    setHasSubtypes(subBlockCount > 1);
  }

  public boolean getHasSubtypes() {

    return hasSubtypes;
  }

  public Block setHasSubtypes(boolean value) {

    hasSubtypes = value;
    return this;
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

    // TODO Auto-generated method stub
    return false;
  }

  // ===============
  // Block overrides
  // ===============

  @Override
  public void getSubBlocks(Item item, CreativeTabs tab, List list) {

    if (hasSubtypes) {
      for (int i = 0; i < subBlockCount; ++i) {
        list.add(new ItemStack(this, 1, i));
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
