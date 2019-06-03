package net.silentchaos512.lib.network.internal;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.lib.SilentLib;

import java.util.Objects;

public class SilentLibNetwork {
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
                .consumer(LeftClickItemPacket::handle)
                .add();
    }

    public static void init() {
    }
}
