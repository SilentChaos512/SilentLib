/*
 * Silent Lib -- IEnchantedGlowFix
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

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.silentchaos512.lib.event.ClientTicks;

import java.awt.*;

/**
 * Implement on items to override the rendering of the enchanted effect. In addition to allowing the
 * control of the effect color, this fixes the "stacking effect" bug that has existed for
 * multi-layer models since Minecraft 1.8, if the item model extends {@link
 * net.silentchaos512.lib.client.model.LayeredBakedModel}. The mod's {@link
 * net.silentchaos512.lib.registry.SRegistry} will set the TEISR automatically.
 * <p>Also see {@link net.silentchaos512.lib.client.render.TEISRCustomEnchantedEffect}</p>
 *
 * @author SilentChaos512
 * @since 3.0.4
 */
public interface ICustomEnchantColor {
    // Some NBT keys used to control color.
    String NBT_LIB_EFFECT_COLOR = "SilentLib:EffectColor";
    String NBT_QUARK_RUNE_ATTACHED = "Quark:RuneAttached";
    String NBT_QUARK_RUNE_COLOR = "Quark:RuneColor";

    /**
     * Get the untruncated enchanted effect color. This defaults to checking for two NBT tags, one
     * added by Silent Lib and the other for Quark runes. See the constants above.
     *
     * @param stack The item
     * @return The effect color
     */
    default int getEffectColor(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();

        if (tagCompound != null) {
            if (tagCompound.hasKey(NBT_LIB_EFFECT_COLOR)) {
                // Tag for possible future use
                return tagCompound.getInteger(NBT_LIB_EFFECT_COLOR);
            } else if (tagCompound.hasKey(NBT_QUARK_RUNE_ATTACHED) && tagCompound.hasKey(NBT_QUARK_RUNE_COLOR)) {
                // Quark runes - stored int is either dye metadata or 16 for rainbow
                int value = tagCompound.getInteger(NBT_QUARK_RUNE_COLOR);
                if (value > 15)
                    return Color.HSBtoRGB(ClientTicks.totalTicks * 0.005f, 1f, 0.6f);
                else if (value >= 0)
                    return EnumDyeColor.byMetadata(value).getColorValue();
            }
        }

        return 0xFFFFFF;
    }

    /**
     * Whether or not the effect color should have its brightness truncated. Without this, the
     * effect would be fully opaque.
     *
     * @param stack The item
     * @return If brightness should be truncated
     */
    default boolean shouldTruncateBrightness(ItemStack stack) {
        return true;
    }

    /**
     * Get the max brightness of the effect, if {@link #shouldTruncateBrightness(ItemStack)} returns
     * true. Default value (396) is roughly equivalent to vanilla.
     *
     * @param stack The item
     * @return The max brightness
     */
    default int getEffectMaxBrightness(ItemStack stack) {
        return 396;
    }
}
