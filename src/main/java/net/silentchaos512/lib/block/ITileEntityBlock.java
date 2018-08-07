package net.silentchaos512.lib.block;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    @SideOnly(Side.CLIENT)
    @Nullable
    default TileEntitySpecialRenderer<?> getTileRenderer() {
        return null;
    }
}
