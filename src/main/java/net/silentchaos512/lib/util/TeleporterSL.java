package net.silentchaos512.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fmllegacy.hooks.BasicEventHooks;

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
    private final ServerLevel world;
    private final DimPos pos;

    public static TeleporterSL of(ServerLevel world, BlockPos pos) {
        return new TeleporterSL(world, DimPos.of(pos, world.dimension()));
    }

    public static TeleporterSL of(ServerLevel world, DimPos pos) {
        return new TeleporterSL(world, pos);
    }

    public TeleporterSL(ServerLevel world, DimPos pos) {
        this.world = world;
        this.pos = DimPos.of(pos.getPos(), pos.getDimension());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        entity.setDeltaMovement(Vec3.ZERO);
        entity.fallDistance = 0;
        entity.level = destWorld;

        Vec3 position = this.pos.getPosCentered(0.1);

        if (entity instanceof ServerPlayer && ((ServerPlayer) entity).connection != null) {
            ServerPlayer player = (ServerPlayer) entity;
            player.connection.teleport(position.x, position.y, position.z, yaw, entity.getXRot());

            player.gameMode.setLevel(destWorld);
            player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
            player.server.getPlayerList().sendLevelInfo(player, destWorld);
            player.server.getPlayerList().sendAllPlayerInfo(player);

            for (MobEffectInstance effect : player.getActiveEffects()) {
                player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect));
            }

            BasicEventHooks.firePlayerChangedDimensionEvent(player, currentWorld.dimension(), destWorld.dimension());
        } else {
            entity.moveTo(position.x, position.y, position.z, yaw, entity.getXRot());
        }

        return entity;
    }

    @Nullable
    public Entity teleport(Entity entity) {
        if (entity.level.isClientSide) return entity;
        ServerLevel destWorld = this.world.getServer().getLevel(this.pos.getDimension());
        return placeEntity(entity, (ServerLevel) entity.level, destWorld, entity.getYRot(), unused -> entity);
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

    public static boolean isSafePosition(BlockGetter worldIn, Entity entityIn, BlockPos pos) {
        // TODO: This doesn't consider wide entities
        for (int i = 1; i < Math.ceil(entityIn.getBbHeight()); ++i) {
            BlockPos up = pos.above(i);
            if (!worldIn.getBlockState(up).isAir()) {
                return false;
            }
        }
        return true;
    }
}
