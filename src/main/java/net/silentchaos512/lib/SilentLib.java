package net.silentchaos512.lib;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.base.IModBase;
import net.silentchaos512.lib.command.internal.CommandDataDump;
import net.silentchaos512.lib.gui.GuiHandlerLibF;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.network.internal.MessageLeftClick;
import net.silentchaos512.lib.proxy.internal.SilentLibCommonProxy;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(modid = SilentLib.MOD_ID, name = SilentLib.MOD_NAME, version = SilentLib.VERSION, dependencies = SilentLib.DEPENDENCIES)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class SilentLib implements IModBase {
    public static final String MOD_ID = "silentlib";
    public static final String MOD_NAME = "Silent Lib";
    public static final String VERSION = "3.0.5";
    public static final int BUILD_NUM = 0;
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2669,);";

    public static NetworkHandlerSL network;
    public static LogHelper logHelper = new LogHelper(MOD_NAME, BUILD_NUM);
    public static I18nHelper i18n = new I18nHelper(MOD_ID, logHelper, true);

    @Instance(MOD_ID)
    public static SilentLib instance;

    @SidedProxy(
            clientSide = "net.silentchaos512.lib.proxy.internal.SilentLibClientProxy",
            serverSide = "net.silentchaos512.lib.proxy.internal.SilentLibServerProxy")
    public static SilentLibCommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = new NetworkHandlerSL(MOD_ID);
        network.register(MessageLeftClick.class, Side.SERVER);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerLibF());

        // Make sure advancement triggers get registered...
        LibTriggers.init();

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDataDump());
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
