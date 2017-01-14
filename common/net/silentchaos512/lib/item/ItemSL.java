package net.silentchaos512.lib.item;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.util.LocalizationHelper;

public class ItemSL extends Item implements IRegistryObject {

  protected int subItemCount;
  protected String itemName;
  protected String modId;

  public ItemSL(int subItemCount, String modId, String name) {

    this.subItemCount = subItemCount;
    this.modId = modId.toLowerCase();
    setHasSubtypes(subItemCount > 1);
    setUnlocalizedName(name);
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
  public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

    if (hasSubtypes) {
      for (int i = 0; i < subItemCount; ++i) {
        list.add(new ItemStack(item, 1, i));
      }
    } else {
      list.add(new ItemStack(this));
    }
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {

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
}
