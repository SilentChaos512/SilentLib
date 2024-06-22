package net.silentchaos512.lib.network.internal;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.silentchaos512.lib.SilentLib;

@EventBusSubscriber(modid = SilentLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class SilentLibNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");

        registrar.playToServer(
                SwingItemPayload.TYPE,
                SwingItemPayload.STREAM_CODEC,
                (data, ctx) -> SilentLibServerPayloadHandler.getInstance().handleSwingItem(data, ctx)
        );
    }
}
