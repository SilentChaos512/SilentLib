package net.silentchaos512.lib.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
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

    switch (world.provider.getDimension()) {
      case 0:
        generateSurface(world, random, chunkX * 16, chunkZ * 16);
        break;
      case -1:
        generateNether(world, random, chunkX * 16, chunkZ * 16);
        break;
      case 1:
        generateEnd(world, random, chunkX * 16, chunkZ * 16);
        break;
      default:
        generateSurface(world, random, chunkX * 16, chunkZ * 16);
    }
  }

  protected abstract void generateSurface(World world, Random random, int posX, int posZ);

  protected abstract void generateNether(World world, Random random, int posX, int posZ);

  protected abstract void generateEnd(World world, Random random, int posX, int posZ);
  
  @SubscribeEvent
  public void onChunkLoad(ChunkDataEvent.Load event) {

    if (!allowRetrogen)
      return;

    // TODO
  }
}
