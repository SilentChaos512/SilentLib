package net.silentchaos512.lib.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.silentchaos512.lib.registry.IRegistryObject;

public class ModelHelper {

  public static List<ModelResourceLocation> getVariants(IRegistryObject obj, int subItemCount) {

    String fullName = obj.getFullName().toLowerCase();
    if (subItemCount > 1) {
      List<ModelResourceLocation> models = Lists.newArrayList();
      for (int i = 0; i < subItemCount; ++i) {
        models.add(new ModelResourceLocation(fullName + i, "inventory"));
      }
      return models;
    }
    return Lists.newArrayList(new ModelResourceLocation(fullName, "inventory"));
  }

  /**
   * Gets a ModelResourceLocation. Lowercases everything and gets the "inventory" variant.
   * 
   * @param modId
   *          The mod ID. This will be converted to lowercase (although it should already be lowercase!)
   * @param name
   *          The model name. This will be converted to lowercase.
   * @return The "inventory" variant ModelResourceLocation
   * @since 2.1.1
   */
  public static ModelResourceLocation getResource(String modId, String name) {

    return getResource(modId, name, "inventory");
  }

  /**
   * Gets a ModelResourceLocation. Lowercases everything except the variant.
   * 
   * @param modId
   *          The mod ID. This will be converted to lowercase (although it should already be lowercase!)
   * @param name
   *          The model name. This will be converted to lowercase.
   * @param variant
   *          The variant to get.
   * @return The ModelResourceLocation
   * @since 2.1.1
   */
  public static ModelResourceLocation getResource(String modId, String name, String variant) {

    return new ModelResourceLocation(modId.toLowerCase() + ":" + name.toLowerCase(), variant);
  }
}
