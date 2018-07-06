/*
 * SilentLib - ItemNamedSubtypes
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import java.util.Map;

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
