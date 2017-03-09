package net.silentchaos512.lib.util;

import java.util.Set;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeHelper {

  public static Set<BiomeDictionary.Type> getTypes(Biome biome) {

    return BiomeDictionary.getTypes(biome);
  }

  public static String getTypeName(BiomeDictionary.Type type) {

    return type.getName();
  }
}
