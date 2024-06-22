package net.silentchaos512.lib.network.internal;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.item.ILeftClickItem;

public class SilentLibServerPayloadHandler {
    private static final SilentLibServerPayloadHandler INSTANCE = new SilentLibServerPayloadHandler();

    public static SilentLibServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static void handleData(final IPayloadContext ctx, Runnable handler) {
        ctx.enqueueWork(handler)
                .exceptionally(e -> {
                    ctx.disconnect(Component.translatable("network.silentlib.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleSwingItem(final SwingItemPayload data, final IPayloadContext ctx) {
        handleData(ctx, () -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                ItemStack heldItem = serverPlayer.getMainHandItem();

                if (!heldItem.isEmpty() && heldItem.getItem() instanceof ILeftClickItem item) {
                    if (data.target() == ILeftClickItem.Target.EMPTY) {
                        item.onItemLeftClickSL(serverPlayer.level(), serverPlayer);
                    } else if (data.target() == ILeftClickItem.Target.BLOCK) {
                        item.onItemLeftClickBlockSL(serverPlayer.level(), serverPlayer);
                    } else {
                        SilentLib.LOGGER.error("Unknown ILeftClickItem.Target: {}", data.target());
                    }
                }
            }
        });
    }
}
