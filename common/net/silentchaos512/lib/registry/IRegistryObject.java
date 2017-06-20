package net.silentchaos512.lib.registry;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IRegistryObject {

  // Formerly IAddRecipe
  public void addRecipes(RecipeMaker recipes);
  public void addOreDict();

  // Formerly IHasVariants
  public String getName();
  public String getFullName();
  public String getModId();

  public List<ModelResourceLocation> getVariants();
  public boolean registerModels();
}
