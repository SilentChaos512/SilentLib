package net.silentchaos512.lib.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;

import java.util.List;

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
  public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

    if (hasSubtypes()) {
      for (int i = 0; i < subBlockCount; ++i) {
        list.add(new ItemStack(item, 1, i));
      }
    } else {
      list.add(new ItemStack(item));
    }
  }

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
}
