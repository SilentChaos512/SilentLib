package net.silentchaos512.lib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Basically a BlockPos with a dimension coordinate. Used by {@link TeleportUtils}
 */
public record DimPos(int posX, int posY, int posZ, ResourceKey<Level> dimension) {
    public static final Codec<DimPos> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(DimPos::getX),
                    Codec.INT.fieldOf("y").forGetter(DimPos::getY),
                    Codec.INT.fieldOf("z").forGetter(DimPos::getZ),
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DimPos::getDimension)
            ).apply(instance, DimPos::of)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DimPos> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DimPos::getX,
            ByteBufCodecs.VAR_INT, DimPos::getY,
            ByteBufCodecs.VAR_INT, DimPos::getZ,
            ResourceKey.streamCodec(Registries.DIMENSION), DimPos::getDimension,
            DimPos::of
    );

    /**
     * Origin (0, 0, 0) in the overworld
     */
    public static final DimPos ZERO = new DimPos(0, 0, 0, Level.OVERWORLD);

    //region Static factory methods

    public static DimPos of(BlockPos pos, ResourceKey<Level> dimension) {
        return new DimPos(pos, dimension);
    }

    public static DimPos of(int x, int y, int z, ResourceKey<Level> dimension) {
        return new DimPos(x, y, z, dimension);
    }

    public static DimPos of(Entity entity) {
        return new DimPos(entity.blockPosition(), entity.level().dimension());
    }

    //endregion

    private DimPos(BlockPos pos, ResourceKey<Level> dimension) {
        this(pos.getX(), pos.getY(), pos.getZ(), dimension);
    }

    @Deprecated(forRemoval = true)
    public int getX() {
        return posX;
    }

    @Deprecated(forRemoval = true)
    public int getY() {
        return posY;
    }

    @Deprecated(forRemoval = true)
    public int getZ() {
        return posZ;
    }

    @Deprecated(forRemoval = true)
    public DimensionId getDimensionId() {
        return DimensionId.fromId(dimension);
    }

    @Deprecated(forRemoval = true)
    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public static DimPos deserializeNbt(CompoundTag tag) {
        return DimPos.of(
                tag.getInt("posX"),
                tag.getInt("posY"),
                tag.getInt("posZ"),
                ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("dim"))));
    }

    public CompoundTag serializeNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("posX", this.posX);
        tag.putInt("posY", this.posY);
        tag.putInt("posZ", this.posZ);
        tag.putString("dim", dimension.location().toString());
        return tag;
    }

    /**
     * Get the Level this DimPos is in. This can only be done on the server.
     *
     * @param anyLevel Any Level object, to provide access to the server
     * @return An Optional containing the level this DimPos is in, if it can be obtained.
     */
    public Optional<Level> getPosLevel(Level anyLevel) {
        var server = anyLevel.getServer();
        if (server == null) return Optional.empty();

        var posLevel = server.getLevel(this.dimension);
        return Optional.ofNullable(posLevel);
    }

    /**
     * Converts to a BlockPos
     *
     * @return A BlockPos with the same coordinates
     */
    public BlockPos getPos() {
        return new BlockPos(posX, posY, posZ);
    }

    public Vec3 getPosCentered(double yOffset) {
        return new Vec3(posX + 0.5, posY + yOffset, posZ + 0.5);
    }

    /**
     * Offset the DimPos in the given direction by the given distance.
     *
     * @param facing The direction to offset
     * @param n      The distance
     * @return A new DimPos with offset coordinates.
     * @since 4.0.10
     */
    public DimPos offset(Direction facing, int n) {
        if (n == 0) {
            return this;
        }
        return new DimPos(
                this.posX + facing.getStepX() * n,
                this.posY + facing.getStepY() * n,
                this.posZ + facing.getStepZ() * n,
                this.dimension);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %s) in %s", this.posX, this.posY, this.posZ, dimension.location());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DimPos pos) {
            return pos.dimension.location().equals(dimension.location()) && pos.posX == posX && pos.posY == posY && pos.posZ == posZ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 * posX + posY) + posZ) + dimension.location().hashCode();
    }
}
