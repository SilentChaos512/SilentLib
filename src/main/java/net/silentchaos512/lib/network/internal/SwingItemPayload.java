package net.silentchaos512.lib.network.internal;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.item.ILeftClickItem;

public record SwingItemPayload(
        ILeftClickItem.Target target
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SwingItemPayload> TYPE = new CustomPacketPayload.Type<>(SilentLib.getId("swing_item"));

    public static final StreamCodec<ByteBuf, SwingItemPayload> STREAM_CODEC = StreamCodec.composite(
            ILeftClickItem.Target.ID_STREAM_CODEC, data -> data.target,
            SwingItemPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
