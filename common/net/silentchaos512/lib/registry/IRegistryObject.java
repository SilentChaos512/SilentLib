package net.silentchaos512.lib.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IRegistryObject {

  // Formerly IAddRecipe

  public void addRecipes(RecipeMaker recipes);

  public void addOreDict();

  // Formerly IHasVariants

  public String getModId();

  public String getName();

  public default String getFullName() {

    return getModId() + ":" + getName();
  }

  public void getModels(Map<Integer, ModelResourceLocation> models);

  public default boolean registerModels() {

    return false;
  }
}
