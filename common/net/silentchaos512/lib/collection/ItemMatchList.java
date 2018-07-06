/*
 * SilentLib - ItemMatchList
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

package net.silentchaos512.lib.collection;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;

/**
 * Can be used as a blacklist/whitelist for Items. Matches to the Item's registry name.
 */
public class ItemMatchList extends AbstractMatchList<Item> {

    public ItemMatchList(boolean whitelist, boolean allowUserToChangeType, String... defaultValues) {
        super(whitelist, allowUserToChangeType, defaultValues);
    }

    public ItemMatchList(boolean whitelist, boolean allowUserToChangeType, Item... defaultValues) {
        super(whitelist, allowUserToChangeType, Arrays.stream(defaultValues).map(Item::getRegistryName)
                .filter(Objects::nonNull).map(ResourceLocation::toString).toArray(String[]::new));
    }

    @Override
    protected boolean contains(Item item) {
        ResourceLocation name = item.getRegistryName();
        return name != null && containsKey(name.toString());
    }
}
