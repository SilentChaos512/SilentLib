package net.silentchaos512.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.silentchaos512.lib.SilentLib;

import javax.annotation.Nullable;

/**
 * {@link ITeleporter} which can move entities across dimensions to any given point.
 *
 * FIXME: Why is ITeleporter gone?
 *
 * @since 4.0.10
 */
public class TeleporterSL extends Teleporter /*implements ITeleporter*/ {
    private final double posX;
    private final double posY;
    private final double posZ;
    private final int dim;

    public static TeleporterSL of(DimPos pos) {
        // FIXME
        return new TeleporterSL(null, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, pos.getDimension());
    }

    public static TeleporterSL of(ServerWorld world, DimPos pos) {
        // FIXME
        return new TeleporterSL(world, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, pos.getDimension());
    }

    public TeleporterSL(ServerWorld world, double posX, double posY, double posZ, int dimension) {
        super(world);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.dim = dimension;
    }

//    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        entity.setMotion(Vec3d.ZERO);
        entity.fallDistance = 0;

        if (entity instanceof ServerPlayerEntity && ((ServerPlayerEntity) entity).connection != null) {
            ((ServerPlayerEntity) entity).connection.setPlayerLocation(posX, posY, posZ, yaw, entity.rotationPitch);
        } else {
            entity.setLocationAndAngles(posX, posY, posZ, yaw, entity.rotationPitch);
        }
    }

    @Nullable
    public Entity teleport(Entity entity) {
        if (entity.world.isRemote) {
            return entity;
        }

        // TODO: Logging?

        if (this.dim != entity.dimension.getId()) {
            DimensionType dimensionType = DimensionType.getById(this.dim);
            // FIXME: Missing Forge patch or something?
//            return entity.changeDimension(dimensionType, this);
            SilentLib.LOGGER.warn("TeleporterSL is currently broken, sorry...");
        }

        placeEntity(entity.world, entity, entity.rotationYaw);
        return entity;
    }

    @Nullable
    public Entity teleportWithMount(Entity entity) {
        Entity mount = entity.getRidingEntity();
        if (mount != null) {
            entity.stopRiding();
            this.teleport(mount);
        }

        this.teleport(entity);
        return entity;
    }
}
