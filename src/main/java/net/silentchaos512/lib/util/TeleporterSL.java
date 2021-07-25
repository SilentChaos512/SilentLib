package net.silentchaos512.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.hooks.BasicEventHooks;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * ITeleporter which can move entities across dimensions to any given point.
 *
 * @since 4.0.10
 * @deprecated Use {@link TeleportUtils} instead
 */
@Deprecated
public class TeleporterSL implements ITeleporter {
    private final ServerWorld world;
    private final DimPos pos;

    public static TeleporterSL of(ServerWorld world, BlockPos pos) {
        return new TeleporterSL(world, DimPos.of(pos, world.dimension()));
    }

    public static TeleporterSL of(ServerWorld world, DimPos pos) {
        return new TeleporterSL(world, pos);
    }

    public TeleporterSL(ServerWorld world, DimPos pos) {
        this.world = world;
        this.pos = DimPos.of(pos.getPos(), pos.getDimension());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        entity.setDeltaMovement(Vector3d.ZERO);
        entity.fallDistance = 0;
        entity.setLevel(destWorld);

        Vector3d position = this.pos.getPosCentered(0.1);

        if (entity instanceof ServerPlayerEntity && ((ServerPlayerEntity) entity).connection != null) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.connection.teleport(position.x, position.y, position.z, yaw, entity.xRot);

            player.gameMode.setLevel(destWorld);
            player.connection.send(new SPlayerAbilitiesPacket(player.abilities));
            player.server.getPlayerList().sendLevelInfo(player, destWorld);
            player.server.getPlayerList().sendAllPlayerInfo(player);

            for (EffectInstance effect : player.getActiveEffects()) {
                player.connection.send(new SPlayEntityEffectPacket(player.getId(), effect));
            }

            BasicEventHooks.firePlayerChangedDimensionEvent(player, currentWorld.dimension(), destWorld.dimension());
        } else {
            entity.moveTo(position.x, position.y, position.z, yaw, entity.xRot);
        }

        return entity;
    }

    @Nullable
    public Entity teleport(Entity entity) {
        if (entity.level.isClientSide) return entity;
        ServerWorld destWorld = this.world.getServer().getLevel(this.pos.getDimension());
        return placeEntity(entity, (ServerWorld) entity.level, destWorld, entity.yRot, unused -> entity);
    }

    @Nullable
    public Entity teleportWithMount(Entity entity) {
        Entity mount = entity.getVehicle();
        if (mount != null) {
            entity.stopRiding();
            this.teleport(mount);
        }

        this.teleport(entity);
        return entity;
    }

    public static boolean isSafePosition(IBlockReader worldIn, Entity entityIn, BlockPos pos) {
        // TODO: This doesn't consider wide entities
        for (int i = 1; i < Math.ceil(entityIn.getBbHeight()); ++i) {
            BlockPos up = pos.above(i);
            if (!worldIn.getBlockState(up).isAir(worldIn, up)) {
                return false;
            }
        }
        return true;
    }
}
