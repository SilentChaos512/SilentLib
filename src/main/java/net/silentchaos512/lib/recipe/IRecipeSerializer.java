/*
 * Silent Lib -- IRecipeSerializer
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

package net.silentchaos512.lib.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

/**
 * Determines how a recipe is serialized to JSON. See {@link RecipeJsonHell} for usage. Two basic
 * serializers are provided, {@link net.silentchaos512.lib.recipe.RecipeJsonHell.ShapedSerializer}
 * and {@link net.silentchaos512.lib.recipe.RecipeJsonHell.ShapelessSerializer}, which handle both
 * vanilla and ore dictionary recipes.
 *
 * <p>One possible solution to implementing a serializer is to take the output from one of the two
 * provided serializers and build on top of it. For example, {@code ItemEnchantmentToken} in
 * Silent's Gems does just that.</p>
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface IRecipeSerializer {
    JsonObject serialize(ItemStack result, Object... components);
}
