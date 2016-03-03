package net.silentchaos512.lib.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

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
  }

  @Override
  public void getSubItems(Item item, CreativeTabs tab, List list) {

    for (String name : sortedNames) {
      list.add(getStack(name));
    }
  }
}
