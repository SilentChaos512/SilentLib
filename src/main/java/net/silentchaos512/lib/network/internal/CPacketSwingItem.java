package net.silentchaos512.lib.network.internal;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.item.ILeftClickItem;

public record CPacketSwingItem(
        ILeftClickItem.ClickType clickType,
        InteractionHand hand
) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentLib.getId("swing_item");

    public CPacketSwingItem(FriendlyByteBuf buf) {
        this(
                buf.readBoolean() ? ILeftClickItem.ClickType.BLOCK : ILeftClickItem.ClickType.EMPTY,
                buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND
        );
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(this.clickType == ILeftClickItem.ClickType.BLOCK);
        pBuffer.writeBoolean(this.hand == InteractionHand.MAIN_HAND);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
