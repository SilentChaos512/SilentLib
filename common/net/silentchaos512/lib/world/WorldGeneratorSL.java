package net.silentchaos512.lib.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class WorldGeneratorSL implements IWorldGenerator {

  public final boolean allowRetrogen;
  public final String retrogenKey;

  public WorldGeneratorSL(boolean allowRetrogren, String retrogenKey) {

    this.allowRetrogen = allowRetrogren;
    this.retrogenKey = retrogenKey;
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world,
      IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

    final int dim = world.provider.getDimension();
    final int posX = chunkX * 16;
    final int posZ = chunkZ * 16;

    if (!generateForDimension(dim, world, random, posX, posZ)) {
      switch (dim) {
        case 0:
          generateSurface(world, random, posX, posZ);
          break;
        case -1:
          generateNether(world, random, posX, posZ);
          break;
        case 1:
          generateEnd(world, random, posX, posZ);
          break;
        default:
          generateSurface(world, random, posX, posZ);
      }
    }
  }

  protected void generateSurface(World world, Random random, int posX, int posZ) {

  }

  protected void generateNether(World world, Random random, int posX, int posZ) {

  }

  protected void generateEnd(World world, Random random, int posX, int posZ) {

  }

  protected boolean generateForDimension(final int dim, World world, Random random, int posX,
      int posZ) {

    return false;
  }

  @SubscribeEvent
  public void onChunkLoad(ChunkDataEvent.Load event) {

    if (!allowRetrogen)
      return;

    // TODO
  }
}
