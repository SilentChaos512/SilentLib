package net.silentchaos512.lib.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.silentchaos512.lib.client.render.BufferBuilderSL;

public class ParticleSL extends Particle {

  protected ParticleSL(World worldIn, double posXIn, double posYIn, double posZIn) {

    super(worldIn, posXIn, posYIn, posZIn);
  }

  public ParticleSL(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {

    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
  }

  @Override
  public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY,
      float rotationXZ) {

    BufferBuilderSL bufferBuilderSl = BufferBuilderSL.INSTANCE.internalAcquireMC12(buffer);
    clRenderParticle(bufferBuilderSl, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
  }

  public void clRenderParticle(BufferBuilderSL buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY,
      float rotationXZ) {

    super.renderParticle(buffer.internalGetBufferMC12(), entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
  }
}
