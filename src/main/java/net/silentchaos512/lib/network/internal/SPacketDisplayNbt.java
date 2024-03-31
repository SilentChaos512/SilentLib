package net.silentchaos512.lib.network.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

public record SPacketDisplayNbt(
        CompoundTag nbt,
        Component title
) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentLib.getId("display_nbt");

    public SPacketDisplayNbt(FriendlyByteBuf buf) {
        this(buf.readNbt(), buf.readComponent());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeNbt(this.nbt);
        pBuffer.writeComponent(this.title);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
