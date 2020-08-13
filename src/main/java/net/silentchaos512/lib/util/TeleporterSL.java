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
 */
public class TeleporterSL implements ITeleporter {
    private final ServerWorld world;
    private final DimPos pos;

    public static TeleporterSL of(ServerWorld world, BlockPos pos) {
        return new TeleporterSL(world, DimPos.of(pos, world.func_234923_W_()));
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
        entity.setMotion(Vector3d.ZERO);
        entity.fallDistance = 0;
        entity.setWorld(destWorld);

        Vector3d position = this.pos.getPosCentered(0.1);

        if (entity instanceof ServerPlayerEntity && ((ServerPlayerEntity) entity).connection != null) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.connection.setPlayerLocation(position.x, position.y, position.z, yaw, entity.rotationPitch);

            player.interactionManager.setWorld(destWorld);
            player.connection.sendPacket(new SPlayerAbilitiesPacket(player.abilities));
            player.server.getPlayerList().sendWorldInfo(player, destWorld);
            player.server.getPlayerList().sendInventory(player);

            for (EffectInstance effect : player.getActivePotionEffects()) {
                player.connection.sendPacket(new SPlayEntityEffectPacket(player.getEntityId(), effect));
            }

            BasicEventHooks.firePlayerChangedDimensionEvent(player, currentWorld.func_234923_W_(), destWorld.func_234923_W_());
        } else {
            entity.setLocationAndAngles(position.x, position.y, position.z, yaw, entity.rotationPitch);
        }

        return entity;
    }

    @Nullable
    public Entity teleport(Entity entity) {
        if (entity.world.isRemote) return entity;
        ServerWorld destWorld = this.world.getServer().getWorld(this.pos.getDimension());
        return placeEntity(entity, (ServerWorld) entity.world, destWorld, entity.rotationYaw, unused -> entity);
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

    public static boolean isSafePosition(IBlockReader worldIn, Entity entityIn, BlockPos pos) {
        // TODO: This doesn't consider wide entities
        for (int i = 1; i < Math.ceil(entityIn.getHeight()); ++i) {
            BlockPos up = pos.up(i);
            if (!worldIn.getBlockState(up).isAir(worldIn, up)) {
                return false;
            }
        }
        return true;
    }
}
