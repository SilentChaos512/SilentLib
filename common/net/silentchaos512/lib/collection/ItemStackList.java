package net.silentchaos512.lib.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.util.StackHelper;

public class ItemStackList extends NonNullList<ItemStack> {

  public static final ItemStackList EMPTY = ItemStackList.create(0);

  public static ItemStackList create(int size) {

    Validate.notNull(StackHelper.empty());
    ItemStack[] aobject = new ItemStack[size];
    Arrays.fill(aobject, StackHelper.empty());
    return new ItemStackList(Arrays.asList(aobject), StackHelper.empty());
  }

  public static ItemStackList create() {

    return new ItemStackList(new ArrayList<>(), StackHelper.empty());
  }

  public ItemStackList(List<ItemStack> delegateIn, @Nullable ItemStack stack) {
    super(delegateIn, stack);
  }
}
