package net.silentchaos512.lib.network.internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.lib.client.gui.nbt.DisplayNBTScreen;

import java.util.function.Supplier;

public class DisplayNBTPacket {
    private CompoundTag nbt;
    private Component title;

    public DisplayNBTPacket() {
    }

    public DisplayNBTPacket(CompoundTag nbt, Component title) {
        this.nbt = nbt;
        this.title = title;
    }

    public static DisplayNBTPacket fromBytes(FriendlyByteBuf buffer) {
        DisplayNBTPacket packet = new DisplayNBTPacket();
        packet.nbt = buffer.readNbt();
        packet.title = buffer.readComponent();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.nbt);
        buffer.writeComponent(this.title);
    }

    public static void handle(DisplayNBTPacket packet, Supplier<NetworkEvent.Context> context) {
        ClientWrapper.handle(packet);
        context.get().setPacketHandled(true);
    }

    private static class ClientWrapper {
        private static void handle(DisplayNBTPacket packet) {
            LocalPlayer player = Minecraft.getInstance().player;

            if (player != null) {
                DisplayNBTScreen screen = new DisplayNBTScreen(packet.nbt, packet.title);
                Minecraft.getInstance().setScreen(screen);
            }
        }
    }
}
