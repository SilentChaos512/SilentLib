package net.silentchaos512.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Borrowed from CC-Tweaked with minor modifications
 * <p>
 * Original: https://github.com/SquidDev-CC/CC-Tweaked/blob/mc-1.13.x/src/main/java/dan200/computercraft/shared/network/container/TileEntityContainerType.java
 *
 * @param <T> Container type
 */
@Deprecated
public class TileEntityContainerType<T extends Container> implements ContainerType<T> {
    public BlockPos pos;
    private final ResourceLocation id;

    public TileEntityContainerType(ResourceLocation id, BlockPos pos) {
        this.id = id;
        this.pos = pos;
    }

    public TileEntityContainerType(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }

    @Nullable
    public TileEntity getTileEntity(PlayerEntity entity) {
        return entity.world.getTileEntity(pos);
    }
}
