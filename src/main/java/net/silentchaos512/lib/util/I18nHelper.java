/*
 * forge-1.12.2-SilentLib_main
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

package net.silentchaos512.lib.util;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class I18nHelper {
    private final String modId;
    private final boolean clientSide;
    private final LogHelper log;
    private final boolean logServerTranslationAttempts;
    private final Set<String> triedToTranslateOnServer = new HashSet<>();

    public I18nHelper(String modId, LogHelper log, boolean logServerTranslationAttempts) {
        this.modId = modId;
        this.log = log;
        this.logServerTranslationAttempts = logServerTranslationAttempts;
        this.clientSide = FMLCommonHandler.instance().getSide() == Side.SERVER;
    }

    public String translate(String key, Object... params) {
        if (!clientSide) {
            if (logServerTranslationAttempts && !triedToTranslateOnServer.contains(key)) {
                log.warn("Tried to translate text \"{}\" on server side");
                triedToTranslateOnServer.add(key);
            }
            return key;
        }

        return I18n.format(key, params);
    }

    public String translate(String prefix, String key, Object... params) {
        return translate(prefix + "." + modId + "." + key, params);
    }

    public String subText(IForgeRegistryEntry<?> object, String key, Object... parameters) {
        String prefix = getPrefixFor(object);
        ResourceLocation name = Objects.requireNonNull(object.getRegistryName());
        return translate(prefix + "." + name.getNamespace() + "." + name.getPath() + "." + key, parameters);
    }

    private String getPrefixFor(IForgeRegistryEntry<?> object) {
        if (object instanceof Item)
            return "item";
        if (object instanceof Block)
            return "tile";
        return object.getClass().getName().toLowerCase(Locale.ROOT);
    }
}
