package net.silentchaos512.lib;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(SilentLib.MOD_ID)
public final class SilentLib {
    public static final String MOD_ID = "silentlib";
    public static final String MOD_NAME = "Silent Lib";
    public static final String VERSION = "4.0.4";
    public static final int BUILD_NUM = 0;

    public static NetworkHandlerSL network;

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    private static SilentLib INSTANCE;
    private static SideProxy PROXY;

    public SilentLib() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());
    }

    public static String getVersion() {
        return getVersion(false);
    }

    public static String getVersion(boolean correctInDev) {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            String str = o.get().getModInfo().getVersion().toString();
            if (correctInDev && "NONE".equals(str))
                return VERSION;
            return str;
        }
        return "0.0.0";
    }

    public static boolean isDevBuild() {
        // TODO: Is there a better way? Guess it works though...
        String version = getVersion(false);
        return "NONE".equals(version);
    }
}
