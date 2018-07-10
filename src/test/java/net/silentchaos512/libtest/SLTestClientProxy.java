/*
 * SilentLib - SLTestClientProxy
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

package net.silentchaos512.libtest;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.client.gui.DebugRenderOverlay;
import net.silentchaos512.lib.registry.SRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SLTestClientProxy extends SLTestCommonProxy {

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        super.preInit(registry, event);

//        makeTestDebugOverlay();
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        super.init(registry, event);
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        super.postInit(registry, event);
    }

    private void makeTestDebugOverlay() {
        MinecraftForge.EVENT_BUS.register(new DebugRenderOverlay() {
            @Nonnull
            @Override
            public List<String> getDebugText() {
                List<String> list = new ArrayList<>();
                list.add("fps=" + Minecraft.getDebugFPS());
                list.add("foobars=" + SLTestMod.random.nextInt(200));
                list.add("happiness=" + Integer.MIN_VALUE);
                list.add("This is a non-split line. It is kind of long too.");
                return list;
            }

            @Override
            public float getTextScale() {
                return 0.7f;
            }

            @Override
            public boolean isHidden() {
                return false;
            }
        });
    }
}
