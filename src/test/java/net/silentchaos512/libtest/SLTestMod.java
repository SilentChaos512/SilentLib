/*
 * SilentLib - SLTestMod
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

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.registry.SRegistry;

import java.util.Random;

@Mod(modid = "sltestmod", name = "Silent Lib Test Mod", version = "0.0.0", dependencies = "required-after:silentlib")
public class SLTestMod {

    public static final Random random = new Random();

    public static SRegistry registry = new SRegistry("sltestmod");

    @Mod.Instance
    public static SLTestMod instance;

    @SidedProxy(clientSide = "net.silentchaos512.libtest.SLTestClientProxy", serverSide = "net.silentchaos512.libtest.SLTestCommonProxy")
    public static SLTestCommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(registry, event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(registry, event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(registry, event);
    }
}
