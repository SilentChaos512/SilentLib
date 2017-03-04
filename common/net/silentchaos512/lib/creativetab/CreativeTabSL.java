package net.silentchaos512.lib.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabSL extends CreativeTabs {

  public CreativeTabSL(String label) {

    super(label);
  }

  protected abstract ItemStack getStack();

  @Override
  public Item getTabIconItem() {

    return getStack().getItem();
  }
}
