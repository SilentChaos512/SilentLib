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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Function;

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

    class RegistrationHelper {
        private final String modId;

        public RegistrationHelper(String modId) {
            this.modId = modId;
        }

        /**
         * Registers items for the provided {@link IEnumItems} values.
         *
         * @param items The {@link IEnumItems} values
         */
        public void registerItems(IEnumItems... items) {
            for (IEnumItems item : items) {
                safeSetRegistryName(item.getItem(), item.getName());
                ForgeRegistries.ITEMS.register(item.getItem());
            }
        }

        /**
         * Registers blocks for any enum type, using the given functions to get the block and name.
         *
         * @param blockGetter Function that returns the block for the enum
         * @param nameGetter  Function that returns the registry name (minus namespace/mod ID) of
         *                    the block
         * @param enumClass   The enum type, E
         * @param <E>         Any enum type
         */
        public <E extends Enum<E>> void registerBlocksGenericEnum(Function<E, Block> blockGetter, Function<E, String> nameGetter, Class<E> enumClass) {
            for (E e : enumClass.getEnumConstants()) {
                String name = nameGetter.apply(e);

                Block block = blockGetter.apply(e);
                safeSetRegistryName(block, name);
                ForgeRegistries.BLOCKS.register(block);

                Item item = new ItemBlock(block, new Item.Builder());
                safeSetRegistryName(item, name);
                ForgeRegistries.ITEMS.register(item);
            }
        }

        /**
         * Registers items for any enum type, using the given functions to get the item and name.
         *
         * @param itemGetter Function that returns the item for the enum
         * @param name       Function that returns the registry name (minus namespace/mod ID) of the
         *                   item
         * @param enumClass  The enum type, E
         * @param <E>        Any enum type
         */
        public <E extends Enum<E>> void registerItemsGenericEnum(Function<E, Item> itemGetter, Function<E, String> name, Class<E> enumClass) {
            for (E e : enumClass.getEnumConstants()) {
                Item item = itemGetter.apply(e);
                safeSetRegistryName(item, name.apply(e));
                ForgeRegistries.ITEMS.register(item);
            }
        }

        private void safeSetRegistryName(IForgeRegistryEntry<?> obj, String name) {
            if (obj.getRegistryName() == null) {
                obj.setRegistryName(new ResourceLocation(modId, name));
            }
        }
    }
}
