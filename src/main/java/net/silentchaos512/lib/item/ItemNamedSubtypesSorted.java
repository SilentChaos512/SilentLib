/*
 * SilentLib - ItemNamedSubtypesSorted
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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.util.ItemHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Same as ItemNamedSubtypes, but the order the subitems are listed can be changed with a sorted names list. You should
 * not change the order of the names list! Always add new items to the end of names, then add them in the desired
 * position in sortedNames.
 *
 * @author SilentChaos512
 *
 */
// TODO: Ready for removal?
@Deprecated
public class ItemNamedSubtypesSorted extends ItemNamedSubtypes {

  public final String[] sortedNames;

  public ItemNamedSubtypesSorted(String[] names, String[] sortedNames, String modId,
      String baseName) {

    super(names, modId, baseName);
    this.sortedNames = sortedNames;
    checkArrays();
  }

  protected void checkArrays() {

    // Make sure the names and sortedNames contain the same elements.
    if (names.length != sortedNames.length) {
      throw new IllegalArgumentException("names and sortedNames are different lengths!");
    }

    String[] array1 = (String[]) Arrays.copyOf(names, names.length);
    String[] array2 = (String[]) Arrays.copyOf(sortedNames, sortedNames.length);
    Arrays.sort(array1);
    Arrays.sort(array2);

    for (int i = 0; i < array1.length; ++i) {
      if (!array1[i].equals(array2[i])) {
        throw new IllegalArgumentException(
            "names and sortedNames don't contain the same elements!");
      }
    }
  }

  @Override
  public void clGetSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

    if (!ItemHelper.isInCreativeTab(item, tab))
      return;

    for (String name : sortedNames) {
      list.add(getStack(name));
    }
  }
}
