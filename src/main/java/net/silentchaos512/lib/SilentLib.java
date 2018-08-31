package net.silentchaos512.lib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.base.IModBase;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.lib.event.ServerTicks;
import net.silentchaos512.lib.event.SilentLibClientEvents;
import net.silentchaos512.lib.event.SilentLibCommonEvents;
import net.silentchaos512.lib.gui.GuiHandlerLibF;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.network.internal.MessageLeftClick;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

@Mod(modid = SilentLib.MOD_ID, name = SilentLib.MOD_NAME, version = SilentLib.VERSION, dependencies = SilentLib.DEPENDENCIES)
public final class SilentLib implements IModBase {
    public static final String MOD_ID = "silentlib";
    public static final String MOD_NAME = "Silent Lib";
    public static final String VERSION = "3.0.3";
    public static final int BUILD_NUM = 0;
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2669,);";

    public static NetworkHandlerSL network;
    public static LogHelper logHelper = new LogHelper(MOD_NAME, BUILD_NUM);
    public static I18nHelper i18n = new I18nHelper(MOD_ID, logHelper, true);

    @Instance(MOD_ID)
    public static SilentLib instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = new NetworkHandlerSL(MOD_ID);
        network.register(MessageLeftClick.class, Side.SERVER);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerLibF());

        // Make sure advancement triggers get registered...
        LibTriggers.init();

        // Common events
        MinecraftForge.EVENT_BUS.register(new SilentLibCommonEvents());
        if (event.getSide().isClient()) {
            // Client-only
            MinecraftForge.EVENT_BUS.register(new SilentLibClientEvents());
            MinecraftForge.EVENT_BUS.register(ClientTicks.INSTANCE);
        } else if (event.getSide().isServer()) {
            // Server-only
            MinecraftForge.EVENT_BUS.register(ServerTicks.INSTANCE);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public String getModName() {
        return MOD_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public int getBuildNum() {
        return BUILD_NUM;
    }
}
