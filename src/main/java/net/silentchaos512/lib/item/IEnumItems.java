/*
 * Silent Lib
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.silentchaos512.lib.registry.SRegistry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * Used to map enum values to items for easy grouping. Intended to be implemented on the enum, but
 * any class would work (you would still have to make the enum though). Every value of the enum
 * should represent one and only one unique item. Remember to store the item, preferably in a
 * private field. This does not register the items for you, but the {@code registerItems} method can
 * be used. Enum values can be added or sorted with no ill effects.
 *
 * @param <E> The enum, which would typically implement this interface
 * @param <I> The Item class, provided to reduce the need for casting
 * @since 2.3.7
 */
public interface IEnumItems<E extends Enum<E>, I extends Item> extends IStringSerializable {

    /**
     * Gets the enum that represents the object. Typically, you would just {@code return this}. This
     * exists because the implementing class may not be an enum.
     *
     * @return An enum representing this object, usually the object itself
     */
    @Nonnull
    E getEnum();

    /**
     * Gets the {@code Item} associated with this object.
     *
     * @return The {code Item}
     */
    @Nonnull
    I getItem();

    /**
     * Convenience method to get an {@code ItemStack} of the {@code Item}. Calls {@code getItem}.
     *
     * @return An {@code ItemStack} with a size of 1
     */
    @Nonnull
    default ItemStack getStack() {
        return new ItemStack(getItem());
    }

    /**
     * Convenience method to get an {@code ItemStack} of the {@code Item}. Calls {@code getItem}.
     *
     * @param amount The stack size
     * @return An {@code ItemStack} with a size of {code amount}
     */
    @Nonnull
    default ItemStack getStack(int amount) {
        return new ItemStack(getItem(), amount);
    }

    /**
     * Gets the name for the {@code Item} (excluding mod ID). Should contain only lowercase letters
     * and underscores ([a-z_]+).
     *
     * @return A unique name for the {@code Item}, in snake_case
     */
    @Nonnull
    @Override
    default String getName() {
        String prefix = getNamePrefix();
        return (!prefix.isEmpty() ? prefix + "_" : "") + getEnum().name().toLowerCase(Locale.ROOT);
    }

    /**
     * Gets a prefix for all item names. If non-empty, {@code getName} will prepend the prefix
     * followed by an underscore to every name.
     *
     * @return The name prefix, or an empty string if a prefix should not be used (default empty)
     */
    @Nonnull
    default String getNamePrefix() {
        return "";
    }

    /**
     * Registers items for the provided {@code IEnumItems} values. Call when registering your items.
     *
     * @param array    The values, typically {@code YourEnumClass.values()}
     * @param registry The mod's SRegistry
     */
    static void registerItems(IEnumItems[] array, SRegistry registry) {
        registerItems(Arrays.asList(array), registry);
    }

    /**
     * Registers items for the provided {@code IEnumItems} values. Call when registering your items.
     *
     * @param list     The values
     * @param registry The mod's SRegistry
     */
    static void registerItems(Collection<? extends IEnumItems> list, SRegistry registry) {
        list.forEach(e -> registry.registerItem(e.getItem(), e.getName()));
    }
}
