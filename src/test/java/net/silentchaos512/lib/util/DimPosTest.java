package net.silentchaos512.lib.util;

import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DimPosTest {
    private static final DimPos VALUE = DimPos.of(-12, 64, 37, ResourceKey.create(Registries.DIMENSION, net.minecraft.resources.Identifier.parse("silentlib:test_dimension")));

    @Test
    void codecRoundTripPreservesCoordinatesAndDimension() {
        var encoded = DimPos.CODEC.encodeStart(JsonOps.INSTANCE, VALUE).getOrThrow();

        assertEquals(VALUE, DimPos.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow());
    }

    @Test
    void streamCodecRoundTripPreservesCoordinatesAndDimension() {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY, ConnectionType.OTHER);
        DimPos.STREAM_CODEC.encode(buffer, VALUE);

        assertEquals(VALUE, DimPos.STREAM_CODEC.decode(buffer));
    }

    @Test
    void nbtRoundTripAndZeroOffsetBehaveAsExpected() {
        CompoundTag nbt = VALUE.serializeNbt();

        assertEquals(VALUE, DimPos.deserializeNbt(nbt));
        assertSame(VALUE, VALUE.offset(net.minecraft.core.Direction.NORTH, 0));
    }
}
