package net.silentchaos512.lib.block;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Allows a block to automatically register its TileEntity, and TESR if it has one.
 */
public interface ITileEntityBlock {
    /**
     * Gets the class of the tile entity associated with this block
     *
     * @return The TileEntity class
     */
    Class<? extends TileEntity> getTileEntityClass();

    /**
     * Gets a TESR for the tile entity, or null if it does not have one
     *
     * @return The TESR to bind, or null if nothing should be done.
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    default TileEntityRenderer<?> getTileRenderer() {
        return null;
    }
}
