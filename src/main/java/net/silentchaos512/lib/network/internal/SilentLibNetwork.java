package net.silentchaos512.lib.network.internal;

import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public final class SilentLibNetwork {
    public static void register(IPayloadRegistrar registrar) {
        registrar.play(CPacketSwingItem.ID, CPacketSwingItem::new,
                handler -> handler.server(SilentLibServerPayloadHandler.getInstance()::handleSwingItem));
        registrar.play(SPacketDisplayNbt.ID, SPacketDisplayNbt::new,
                handler -> handler.client(SilentLibClientPayloadHandler.getInstance()::handleDisplayNbt));
    }
}
