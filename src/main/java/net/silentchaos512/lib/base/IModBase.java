package net.silentchaos512.lib.base;

import net.silentchaos512.lib.util.GameUtil;
import net.silentchaos512.lib.util.LogHelper;

public interface IModBase {
    String getModId();

    String getModName();

    String getVersion();

    int getBuildNum();

    default boolean isDevBuild() {
        return 0 == getBuildNum() || GameUtil.isDeobfuscated();
    }

    default LogHelper getLog() {
        return LogHelper.getRegisteredLogger(getModName()).orElse(new LogHelper(getModName(), getBuildNum()));
    }
}
