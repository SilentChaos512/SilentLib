package net.silentchaos512.lib.item;

import java.util.Arrays;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Same as ItemNamedSubtypes, but the order the subitems are listed can be changed with a sorted names list. You should
 * not change the order of the names list! Always add new items to the end of names, then add them in the desired
 * position in sortedNames.
 * 
 * @author SilentChaos512
 *
 */
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
  public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

    for (String name : sortedNames) {
      list.add(getStack(name));
    }
  }
}
