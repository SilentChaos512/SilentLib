package net.silentchaos512.lib.network.internal;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.silentchaos512.lib.client.gui.nbt.DisplayNBTScreen;

public class SilentLibClientPayloadHandler {
    private static final SilentLibClientPayloadHandler INSTANCE = new SilentLibClientPayloadHandler();

    public static SilentLibClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static void handleData(final PlayPayloadContext ctx, Runnable handler) {
        ctx.workHandler().submitAsync(handler)
                .exceptionally(e -> {
                    ctx.packetHandler().disconnect(Component.translatable("network.silentlib.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleDisplayNbt(SPacketDisplayNbt data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            Player player = ctx.player().orElse(Minecraft.getInstance().player);
            if (player != null) {
                DisplayNBTScreen screen = new DisplayNBTScreen(data.nbt(), data.title());
                Minecraft.getInstance().setScreen(screen);
            }
        });
    }
}
