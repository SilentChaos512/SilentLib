package net.silentchaos512.lib.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;


public class TileEntitySpecialRendererSL<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

  @Override
  public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {

    clRender(te, x, y, z, partialTicks, destroyStage, 1f);
  }

  public void clRender(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

    super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
  }
}
