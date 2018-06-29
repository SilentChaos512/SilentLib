package net.silentchaos512.lib.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemEnumSubtypes<T extends Enum<T> & IStringSerializable> extends ItemSL {

    private final Map<String, T> nameToValue = new HashMap<>();
    private final String subtypeKey;

    public ItemEnumSubtypes(String modId, String name, String subtypeKey, Class<T> valueClass) {
        super(valueClass.getEnumConstants().length, modId, name);
        this.subtypeKey = subtypeKey;
        Arrays.stream(valueClass.getEnumConstants()).forEach(t -> nameToValue.put(t.getName(), t));
    }

    public ItemStack getStack(String typeName) {
        T type = nameToValue.get(typeName);
        return type == null ? ItemStack.EMPTY : getStack(type, 1);
    }

    public ItemStack getStack(String typeName, int count) {
        T type = nameToValue.get(typeName);
        return type == null ? ItemStack.EMPTY : getStack(type, count);
    }

    public ItemStack getStack(T type) {
        return getStack(type, 1);
    }

    public ItemStack getStack(T type, int count) {
        ItemStack stack = new ItemStack(this, count);
        NBTTagCompound tags = new NBTTagCompound();
        tags.setString(subtypeKey, type.getName());
        stack.setTagCompound(tags);
        return stack;
    }

    @Nullable
    public T getType(ItemStack stack) {
        return nameToValue.get(getTypeName(stack));
    }

    public String getTypeName(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != this) return "";
        NBTTagCompound tags = stack.getTagCompound();
        return tags == null ? "" : tags.getString(subtypeKey);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        nameToValue.values().stream().map(this::getStack).forEach(list::add);
    }
}
