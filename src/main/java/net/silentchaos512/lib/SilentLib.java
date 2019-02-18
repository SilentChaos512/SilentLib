package net.silentchaos512.lib;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SilentLib.MOD_ID)
public final class SilentLib {
    public static final String MOD_ID = "silentlib";
    public static final String MOD_NAME = "Silent Lib";
    public static final String VERSION = "4.0.0";
    public static final int BUILD_NUM = 0;

    public static NetworkHandlerSL network;

    public static final Logger LOGGER = LogManager.getLogger();

    private static SilentLib INSTANCE;
    private static SideProxy PROXY;

    public SilentLib() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());
    }
}
