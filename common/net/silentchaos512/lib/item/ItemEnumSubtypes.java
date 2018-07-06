/*
 * SilentLib - ItemEnumSubtypes
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

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An Item with an enum to specify subtypes. (Experimental)
 * @param <T> An enum that implements {@link IStringSerializable}
 */
public class ItemEnumSubtypes<T extends Enum<T> & IStringSerializable> extends Item {

    private final Map<String, T> nameToValue = new LinkedHashMap<>();
    private final String subtypeKey;

    public ItemEnumSubtypes(Class<T> valueClass) {
        this("Subtype", valueClass);
    }

    public ItemEnumSubtypes(String subtypeKey, Class<T> valueClass) {
        super();
        this.subtypeKey = subtypeKey;
        Arrays.stream(valueClass.getEnumConstants()).forEach(t -> nameToValue.put(t.getName(), t));
        setHasSubtypes(true);
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

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        T type = getType(stack);
        return super.getUnlocalizedName() + (type != null ? "_" + type.getName() : "");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        T type = getType(stack);
        if (type != null) {
            tooltip.add("*" + this.subtypeKey + ": " + type.getName());
        }
    }
}
