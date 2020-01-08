package net.silentchaos512.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.hooks.BasicEventHooks;

import javax.annotation.Nullable;

/**
 * ITeleporter which can move entities across dimensions to any given point.
 *
 * FIXME: Why is ITeleporter gone?
 *
 * @since 4.0.10
 */
public class TeleporterSL extends Teleporter /*implements ITeleporter*/ {
    private final DimPos pos;

    public static TeleporterSL of(ServerWorld world, DimPos pos) {
        return new TeleporterSL(world, pos);
    }

    public TeleporterSL(ServerWorld world, DimPos pos) {
        super(world);
        this.pos = DimPos.of(pos.getPos(), pos.getDimension());
    }

    @Override
    public boolean placeInPortal(Entity p_222268_1_, float p_222268_2_) {
        return true;
    }

    //    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        entity.setMotion(Vec3d.ZERO);
        entity.fallDistance = 0;
        Vec3d position = this.pos.getPosCentered(0.1);

        if (entity instanceof ServerPlayerEntity && ((ServerPlayerEntity) entity).connection != null) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.connection.setPlayerLocation(position.x, position.y, position.z, yaw, entity.rotationPitch);
        } else {
            entity.setLocationAndAngles(position.x, position.y, position.z, yaw, entity.rotationPitch);
        }
    }

    @Nullable
    public Entity teleport(Entity entity) {
        if (entity.world.isRemote) return entity;

        // TODO: Logging?

        if (this.pos.getDimension() != entity.dimension.getId()) {
            return entity instanceof ServerPlayerEntity
                    ? changeDimension((ServerPlayerEntity) entity, this.pos)
                    : changeDimensionEntity(entity, this.pos);
        } else {
            placeEntity(entity.world, entity, entity.rotationYaw);
        }

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

    @Nullable
    public static ServerPlayerEntity changeDimension(ServerPlayerEntity player, DimPos pos) {
        // Very similar to ServerPlayerEntity#changeDimension
        DimensionType newDimension = pos.getDimensionType();
        if (newDimension == null || !ForgeHooks.onTravelToDimension(player, newDimension)) return null;

        if (player.world.isRemote || !player.isAlive()) return null;

        player.invulnerableDimensionChange = true;
        DimensionType dimensiontype = player.dimension;

        ServerWorld serverworld = player.server.getWorld(dimensiontype);
        player.dimension = newDimension;
        ServerWorld serverworld1 = player.server.getWorld(newDimension);
        WorldInfo worldinfo = player.world.getWorldInfo();
        player.connection.sendPacket(new SRespawnPacket(newDimension, serverworld1.getSeed(), worldinfo.getGenerator(), player.interactionManager.getGameType()));
        player.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
        PlayerList playerlist = player.server.getPlayerList();
        playerlist.updatePermissionLevel(player);
        serverworld.removeEntity(player, true);
        player.revive();
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;

        Vec3d position = pos.getPosCentered(0.1);
        player.setLocationAndAngles(position.x, position.y, position.z, f1, f);
        serverworld.getProfiler().endSection();
        serverworld.getProfiler().startSection("placing");
        player.setLocationAndAngles(position.x, position.y, position.z, f1, f);

        serverworld.getProfiler().endSection();
        player.setWorld(serverworld1);
        serverworld1.func_217447_b(player);
        player.connection.setPlayerLocation(position.x, position.y, position.z, f1, f);
        player.interactionManager.setWorld(serverworld1);
        player.connection.sendPacket(new SPlayerAbilitiesPacket(player.abilities));
        playerlist.sendWorldInfo(player, serverworld1);
        playerlist.sendInventory(player);

        for(EffectInstance effectinstance : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPlayEntityEffectPacket(player.getEntityId(), effectinstance));
        }

        player.connection.sendPacket(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
        player.lastExperience = -1;
        player.lastHealth = -1F;
        player.lastFoodLevel = -1;
        BasicEventHooks.firePlayerChangedDimensionEvent(player, dimensiontype, newDimension);

        return player;
    }

    @Nullable
    public Entity changeDimensionEntity(Entity entityIn, DimPos pos) {
        DimensionType newDimension = pos.getDimensionType();
        if (newDimension == null || !ForgeHooks.onTravelToDimension(entityIn, newDimension)) return null;

        if (entityIn.world.isRemote || !entityIn.isAlive()) return null;

        this.world.getProfiler().startSection("changeDimension");
        MinecraftServer minecraftserver = entityIn.getServer();
        if (minecraftserver == null) return null;

        DimensionType oldDimension = entityIn.dimension;
        ServerWorld serverworld = minecraftserver.getWorld(oldDimension);
        ServerWorld serverworld1 = minecraftserver.getWorld(newDimension);
        entityIn.dimension = newDimension;
        entityIn.detach();
        this.world.getProfiler().startSection("reposition");
        Vec3d vec3d = entityIn.getMotion();
        float f = 0.0F;

        this.world.getProfiler().endStartSection("reloading");
        Entity entity = entityIn.getType().create(serverworld1);
        if (entity != null) {
            entity.copyDataFromOld(entityIn);
            entity.moveToBlockPosAndAngles(pos.getPos(), entity.rotationYaw + f, entity.rotationPitch);
            entity.setMotion(vec3d);
            serverworld1.func_217460_e(entity);
        }

        this.world.getProfiler().endSection();
        serverworld.resetUpdateEntityTick();
        serverworld1.resetUpdateEntityTick();
        this.world.getProfiler().endSection();
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
