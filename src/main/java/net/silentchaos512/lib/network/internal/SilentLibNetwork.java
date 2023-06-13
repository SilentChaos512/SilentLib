package net.silentchaos512.lib.network.internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.silentchaos512.lib.SilentLib;

import java.util.Objects;

public final class SilentLibNetwork {
    private static final ResourceLocation NAME = new ResourceLocation(SilentLib.MOD_ID, "network");
    private static final int VERSION = 1;

    public static SimpleChannel channel;

    static {
        channel = NetworkRegistry.ChannelBuilder.named(NAME)
                .clientAcceptedVersions(s -> Objects.equals(s, String.valueOf(VERSION)))
                .serverAcceptedVersions(s -> Objects.equals(s, String.valueOf(VERSION)))
                .networkProtocolVersion(() -> String.valueOf(VERSION))
                .simpleChannel();

        channel.messageBuilder(LeftClickItemPacket.class, 1)
                .decoder(LeftClickItemPacket::fromBytes)
                .encoder(LeftClickItemPacket::toBytes)
                .consumerMainThread(LeftClickItemPacket::handle)
                .add();
        channel.messageBuilder(DisplayNBTPacket.class, 2)
                .decoder(DisplayNBTPacket::fromBytes)
                .encoder(DisplayNBTPacket::toBytes)
                .consumerMainThread(DisplayNBTPacket::handle)
                .add();
        channel.messageBuilder(SpawnEntityPacket.class, 3)
                .decoder(SpawnEntityPacket::decode)
                .encoder(SpawnEntityPacket::encode)
                .consumerMainThread(SpawnEntityPacket::handle)
                .add();
    }

    private SilentLibNetwork() {}

    public static void init() {}
}
