package net.silentchaos512.lib.world;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.lib.SilentLib;

public abstract class WorldGeneratorSL implements IWorldGenerator {

  public final boolean allowRetrogen;
  public final String retrogenKey;
  public final int retrogenVersion;

  protected boolean printDebugInfo = false;

  public WorldGeneratorSL(boolean allowRetrogren, String retrogenKey) {

    this(allowRetrogren, retrogenKey, 1);
  }

  public WorldGeneratorSL(boolean allowRetrogren, String retrogenKey, int retrogenVersion) {

    this.allowRetrogen = allowRetrogren;
    this.retrogenKey = retrogenKey;
    this.retrogenVersion = retrogenVersion > 0 ? retrogenVersion : 1;

    if (allowRetrogren)
      MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

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

  protected boolean generateForDimension(final int dim, World world, Random random, int posX, int posZ) {

    return false;
  }

  protected void debug(Object obj) {

    if (printDebugInfo)
      SilentLib.logHelper.debug(obj);
  }

  // ============
  // = Retrogen =
  // ============

  private int retroCurrentX = Integer.MAX_VALUE, retroCurrentZ = Integer.MAX_VALUE;

  @SubscribeEvent
  public void onChunkLoad(ChunkDataEvent.Load event) {

    // Retrogen allowed?
    if (!allowRetrogen)
      return;

//    // Retrogen already done?
//    NBTTagCompound data = event.getData();
//    if (data.hasKey(retrogenKey) && data.getInteger(retrogenKey) >= retrogenVersion)
//      return;
//
//    // Get chunk info.
//    Chunk chunk = event.getChunk();
//    // Is this chunk currently generating?
//    if (chunk.x == retroCurrentX && chunk.z == retroCurrentZ)
//      return;
//    World world = chunk.getWorld();
//    ChunkProviderServer chunkProvider = (ChunkProviderServer) world.getChunkProvider();
//    IChunkGenerator chunkGenerator = chunkProvider.chunkGenerator;
//
//    retroCurrentX = chunk.x;
//    retroCurrentZ = chunk.z;
//
//    // Setting up a Random object in the same way Forge does (GameRegistry#generateWorld)
//    long worldSeed = world.getSeed();
//    Random rand = new Random(worldSeed);
//    long xSeed = rand.nextLong() >> 2 + 1L;
//    long zSeed = rand.nextLong() >> 2 + 1L;
//    long chunkSeed = (xSeed * chunk.x + zSeed * chunk.z) ^ worldSeed;
//    rand.setSeed(chunkSeed);
//
//    debug(retrogenKey + " version " + retrogenVersion + " (" + chunk.x + ", " + chunk.z + ")");
//
//    // Mark the chunk
//    data.setInteger(retrogenKey, retrogenVersion);
//
//    // Generate!
//    generate(rand, chunk.x, chunk.z, world, chunkGenerator, chunkProvider);
  }
}
