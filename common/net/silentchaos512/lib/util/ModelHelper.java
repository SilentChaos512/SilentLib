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
}
