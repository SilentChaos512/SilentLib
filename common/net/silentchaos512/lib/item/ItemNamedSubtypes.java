package net.silentchaos512.lib.item;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.util.StackHelper;

/**
 * While ItemSL assigns numbers to subitems, this class will allow you to assign names instead, essentially packing
 * multiple items into one ID. Good for "crafting material" items.
 * 
 * Note that you should not change the order of the names list, because that will change the meta of the item! Use
 * ItemNamedSubtypesSorted if you want to reorganize the order of the items without changing their meta.
 * 
 * @author SilentChaos512
 *
 */
public class ItemNamedSubtypes extends ItemSL {

  public final String[] names;

  public ItemNamedSubtypes(String[] names, String modId, String baseName) {

    super(names.length, modId, baseName);
    this.names = names;
  }

  public int getMetaFor(String name) {

    return getStack(name).getItemDamage();
  }

  public @Nonnull ItemStack getStack(String name) {

    return getStack(name, 1);
  }

  public @Nonnull ItemStack getStack(String name, int count) {

    for (int meta = 0; meta < names.length; ++meta)
      if (name.equals(names[meta]))
        return new ItemStack(this, count, meta);
    return StackHelper.empty();
  }

  @Override
  public void getModels(Map<Integer, ModelResourceLocation> models) {

    int i = 0;
    for (String name : names) {
      models.put(i++, new ModelResourceLocation(modId + ":" + name, "inventory"));
    }
  }

  public String getNameForStack(ItemStack stack) {

    int meta = stack.getItemDamage();
    if (meta >= 0 && meta < names.length)
      return names[meta];
    return super.getNameForStack(stack);
  }
}
