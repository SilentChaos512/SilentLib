package net.silentchaos512.lib.util;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeHelper {

  public static Set<BiomeDictionary.Type> getTypes(Biome biome) {

    Set<BiomeDictionary.Type> ret = Sets.newHashSet();
    for (BiomeDictionary.Type type : BiomeDictionary.getTypesForBiome(biome))
      ret.add(type);
    return ret;
  }

  public static String getTypeName(BiomeDictionary.Type type) {

    return type.name();
  }
}
