package net.silentchaos512.lib.network.internal;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SilentLibClientPayloadHandler {
    private static final SilentLibClientPayloadHandler INSTANCE = new SilentLibClientPayloadHandler();

    public static SilentLibClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static void handleData(final IPayloadContext ctx, Runnable handler) {
        ctx.enqueueWork(handler)
                .exceptionally(e -> {
                    ctx.disconnect(Component.translatable("network.silentlib.failure", e.getMessage()));
                    return null;
                });
    }
}
