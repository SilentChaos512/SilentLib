package net.silentchaos512.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class TeleportUtils {
    public static void teleport(Player player, DimPos pos, @Nullable Direction direction) {
        teleport(player, pos.dimension(), pos.posX() + 0.5, pos.posY(), pos.posZ() + 0.5, direction);
    }

    public static void teleport(Player player, ResourceKey<Level> dimension, double destX, double destY, double destZ, @Nullable Direction direction) {
        ResourceKey<Level> currentDimension = player.getCommandSenderWorld().dimension();

        float rotationYaw = player.getYRot();
        float rotationPitch = player.getXRot();

        if (!currentDimension.equals(dimension)) {
            teleportToDimension(player, dimension, destX, destY, destZ);
        }
        if (direction != null) {
            fixOrientation(player, destX, destY, destZ, direction);
        } else {
            player.setYRot(rotationYaw);
            player.setXRot(rotationPitch);
        }
        player.teleportTo(destX, destY, destZ);
    }

    public static void teleportToDimension(Player player, ResourceKey<Level> dimension, double x, double y, double z) {
        MinecraftServer server = player.getCommandSenderWorld().getServer();
        if (server == null) return;

        ServerLevel level = server.getLevel(dimension);
        if (level == null) return;

        player.teleport(new TeleportTransition(level, new Vec3(x, y, z), Vec3.ZERO, player.getYRot(), player.getXRot(), TeleportTransition.PLAY_PORTAL_SOUND));
    }

    private static void facePosition(Entity entity, double newX, double newY, double newZ, BlockPos dest) {
        double d0 = dest.getX() - newX;
        double d1 = dest.getY() - (newY + entity.getEyeHeight());
        double d2 = dest.getZ() - newZ;

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(Mth.atan2(d1, d3) * (180D / Math.PI)));
        entity.setXRot(updateRotation(entity.getXRot(), f1));
        entity.setYRot(updateRotation(entity.getYRot(), f));
    }

    private static float updateRotation(float angle, float targetAngle) {
        float f = Mth.wrapDegrees(targetAngle - angle);
        return angle + f;
    }

    @Nullable
    public static Entity teleportEntity(Entity entity, DimPos pos, @Nullable Direction facing) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;

        ServerLevel destinationLevel = server.getLevel(pos.dimension());
        if (destinationLevel == null) return null;

        return teleportEntity(entity, destinationLevel, pos.posX() + 0.5, pos.posY(), pos.posZ() + 0.5, facing);
    }

    @Nullable
    public static Entity teleportEntity(Entity entity, Level destinationLevel, double newX, double newY, double newZ, @Nullable Direction facing) {
        Level currentLevel = entity.getCommandSenderWorld();
        if (currentLevel.dimension().location().equals(destinationLevel.dimension().location())) {
            if (facing != null) {
                fixOrientation(entity, newX, newY, newZ, facing);
            }
            entity.snapTo(newX, newY, newZ, entity.getYRot(), entity.getXRot());
            ((ServerLevel) destinationLevel).tickNonPassenger(entity);
            return entity;
        } else {
            return entity.teleport(new TeleportTransition((ServerLevel) destinationLevel, new Vec3(newX, newY, newZ), Vec3.ZERO, entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING));
        }
    }

    private static void fixOrientation(Entity entity, double newX, double newY, double newZ, Direction facing) {
        if (facing != Direction.DOWN && facing != Direction.UP) {
            facePosition(entity, newX, newY, newZ, new BlockPos(new Vec3i((int) newX, (int) newY, (int) newZ)).relative(facing, 4));
        }
    }
}
