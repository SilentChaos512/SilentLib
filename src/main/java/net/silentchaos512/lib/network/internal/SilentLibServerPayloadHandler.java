package net.silentchaos512.lib.network.internal;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.item.ILeftClickItem;

public class SilentLibServerPayloadHandler {
    private static final SilentLibServerPayloadHandler INSTANCE = new SilentLibServerPayloadHandler();

    public static SilentLibServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static void handleData(final PlayPayloadContext ctx, Runnable handler) {
        ctx.workHandler().submitAsync(handler)
                .exceptionally(e -> {
                    ctx.packetHandler().disconnect(Component.translatable("network.silentlib.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleSwingItem(CPacketSwingItem data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            ctx.player().ifPresent(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    ItemStack heldItem = serverPlayer.getItemInHand(data.hand());

                    if (!heldItem.isEmpty() && heldItem.getItem() instanceof ILeftClickItem) {
                        ILeftClickItem item = (ILeftClickItem) heldItem.getItem();

                        if (data.clickType() == ILeftClickItem.ClickType.EMPTY) {
                            item.onItemLeftClickSL(serverPlayer.level(), serverPlayer, data.hand());
                        } else if (data.clickType() == ILeftClickItem.ClickType.BLOCK) {
                            item.onItemLeftClickBlockSL(serverPlayer.level(), serverPlayer, data.hand());
                        } else {
                            SilentLib.LOGGER.error("Unknown ILeftClickItem.ClickType: {}", data.clickType());
                        }
                    }
                }
            });
        });
    }
}
