package net.silentchaos512.lib.util;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public final class CapabilityUtils {
    private CapabilityUtils() {throw new IllegalAccessError("Utility class");}

    @Nullable
    public static <T> T getOrNull(ICapabilityProvider provider, Capability<T> cap) {
        LazyOptional<T> lazy = provider.getCapability(cap);
        if (lazy.isPresent()) {
            return lazy.orElseThrow(IllegalStateException::new);
        }
        return null;
    }
}
