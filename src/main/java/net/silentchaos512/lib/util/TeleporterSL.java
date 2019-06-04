package net.silentchaos512.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;

/**
 * {@link ITeleporter} which can move entities across dimensions to any given point.
 *
 * @since 4.0.10
 */
public class TeleporterSL implements ITeleporter {
    private final double posX;
    private final double posY;
    private final double posZ;
    private final int dim;

    public static TeleporterSL of(DimPos pos) {
        return new TeleporterSL(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, pos.getDimension());
    }

    public TeleporterSL(double posX, double posY, double posZ, int dimension) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.dim = dimension;
    }

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        entity.motionX = entity.motionY = entity.motionZ = 0;
        entity.fallDistance = 0;

        if (entity instanceof EntityPlayerMP && ((EntityPlayerMP) entity).connection != null) {
            ((EntityPlayerMP) entity).connection.setPlayerLocation(posX, posY, posZ, yaw, entity.rotationPitch);
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
            return entity.changeDimension(dimensionType, this);
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
