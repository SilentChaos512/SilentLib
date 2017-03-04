package net.silentchaos512.lib.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemStackList extends AbstractList<ItemStack> {

  public static final ItemStackList EMPTY = ItemStackList.create(0);

  private final List<ItemStack> delegate;

  public static ItemStackList create() {

    return new ItemStackList();
  }

  public static ItemStackList create(int size) {

    ItemStack[] aobject = new ItemStack[size];
    Arrays.fill(aobject, null);
    return new ItemStackList(Arrays.asList(aobject));
  }

  protected ItemStackList() {

    this(new ArrayList<>());
  }

  protected ItemStackList(List<ItemStack> delegateIn) {
    this.delegate = delegateIn;
  }

  public ItemStack get(int idx) {

    return this.delegate.get(idx);
  }

  public ItemStack set(int idx, ItemStack stack) {

    return this.delegate.set(idx, stack);
  }

  public void add(int idx, ItemStack stack) {

    this.delegate.add(idx, stack);
  }

  public ItemStack remove(int idx) {

    return this.delegate.remove(idx);
  }

  public int size() {

    return this.delegate.size();
  }
}
