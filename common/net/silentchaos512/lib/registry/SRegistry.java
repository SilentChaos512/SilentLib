package net.silentchaos512.lib.registry;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.silentchaos512.lib.item.ItemBlockSL;
import net.silentchaos512.lib.util.LogHelper;
import scala.NotImplementedError;

public class SRegistry {

  // I highly recommend NOT touching these...
  protected final Map<String, Block> blocks = Maps.newHashMap();
  protected final Map<String, Item> items = Maps.newHashMap();
  protected final List<IRegistryObject> registryObjects = Lists.newArrayList();

  /**
   * A reference to the mod's instance object.
   */
  protected Object mod;
  /**
   * The LogHelper for the mod, if any. May be used to log error messages.
   */
  protected LogHelper logHelper;
  /**
   * Automatically incrementing ID number for registering entities.
   */
  protected int lastEntityId = -1;

  /**
   * The mod ID for the mod this SRegistry instance belongs to.
   */
  public final String modId;
  /**
   * The resource prefix for the mod. This is set in the constructor based on the modId.
   */
  public final String resourcePrefix;

  protected boolean listModelsInPost = false;

  public RecipeMaker recipes;

  public SRegistry(String modId) {

    this.modId = modId;
    this.resourcePrefix = modId.toLowerCase() + ":";
    this.recipes = new RecipeMaker(modId);
  }

  public SRegistry(String modId, LogHelper logHelper) {

    this(modId);
    this.logHelper = logHelper;
  }

  /**
   * The mod object should be set automatically in preInit, but it can be done manually if that fails.
   */
  public void setMod(Object mod) {

    this.mod = mod;
  }

  /**
   * Register an IRegistryObject Block (BlockSL, etc.) Uses getName for its key.
   */
  public <T extends Block & IRegistryObject> T registerBlock(T block) {

    return registerBlock(block, block.getName());
  }

  /**
   * Register a Block. Its name (registry key/name) must be provided. Uses a new ItemBlockSL.
   */
  public <T extends Block> T registerBlock(T block, String key) {

    return registerBlock(block, key, new ItemBlockSL(block));
  }

  /**
   * Register an IRegistryObject Block (BlockSL, etc.) with a custom ItemBlock. Uses getName for its key.
   */
  public <T extends Block & IRegistryObject> T registerBlock(T block, ItemBlock itemBlock) {

    return registerBlock(block, block.getName(), itemBlock);
  }

  /**
   * Register a Block. Its name (registry key/name) and ItemBlock must be provided.
   */
  public <T extends Block> T registerBlock(T block, String key, ItemBlock itemBlock) {

    blocks.put(key, block);
    if (block instanceof IRegistryObject) {
      registryObjects.add((IRegistryObject) block);
    }

    ResourceLocation resource = new ResourceLocation(resourcePrefix + key);
    block.setRegistryName(resource);
    GameRegistry.register(block);
    GameRegistry.register(itemBlock, resource);
    return block;
  }

  /**
   * Register an IRegistryObject Item (ItemSL, etc.) Uses getName for its key.
   */
  public <T extends Item & IRegistryObject> T registerItem(T item) {

    return registerItem(item, item.getName());
  }

  /**
   * Register an Item. Its name (registry key/name) must be provided.
   */
  public <T extends Item> T registerItem(T item, String key) {

    items.put(key, item);
    if (item instanceof IRegistryObject) {
      registryObjects.add((IRegistryObject) item);
    }

    ResourceLocation resource = new ResourceLocation(resourcePrefix + key);
    GameRegistry.register(item, resource);
    return item;
  }

  /**
   * Register a TileEntity. "tile." + resourcePrefix is automatically prepended.
   */
  public void registerTileEntity(Class<? extends TileEntity> tileClass, String key) {

    String fullKey = "tile." + resourcePrefix + key;
    GameRegistry.registerTileEntity(tileClass, fullKey);
  }

  public void registerEntity(Class<? extends Entity> entityClass, String key) {

    registerEntity(entityClass, key, ++lastEntityId, mod, 64, 20, true);
  }

  public void registerEntity(Class<? extends Entity> entityClass, String key, int trackingRange,
      int updateFrequency, boolean sendsVelocityUpdates) {

    registerEntity(entityClass, key, ++lastEntityId, mod, trackingRange, updateFrequency,
        sendsVelocityUpdates);
  }

  public void registerEntity(Class<? extends Entity> entityClass, String key, int id, Object mod,
      int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {

    ResourceLocation resource = new ResourceLocation(modId, key);
    EntityRegistry.registerModEntity(resource, entityClass, key, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
  }

  @SideOnly(Side.CLIENT)
  public <T extends TileEntity> void registerTileEntitySpecialRenderer(Class<T> tileClass,
      TileEntitySpecialRenderer<T> renderer) {

    ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
  }

  /**
   * @deprecated Use the factory version registerEntityRenderer(Class, IRenderFactory)
   */
  @SideOnly(Side.CLIENT)
  @Deprecated
  public void registerEntityRenderer(Class<? extends Entity> entityClass, Render renderer) {

    Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(entityClass, renderer);
  }

  @SideOnly(Side.CLIENT)
  public <T extends Entity> void registerEntityRenderer(Class<T> entityClass,
      IRenderFactory<T> renderFactory) {

    RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
  }

  /**
   * Used to register a custom recipe handler. Be sure to catch potential exceptions.
   * 
   * @param recipeClass
   *          The recipe class (extending RecipeBase is recommended, but not required).
   * @param name
   *          A name for the recipe. resourcePrefix is automatically prepended.
   * @param category
   *          Recipe category.
   * @param dependencies
   *          Dependencies, such as "after:minecraft:shapeless"
   * @return The created IRecipe instance (hanging onto this is recommended)
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public IRecipe addRecipeHandler(Class<? extends IRecipe> recipeClass, String name,
      Category category, String dependencies)
      throws InstantiationException, IllegalAccessException {

//    IRecipe recipe = recipeClass.newInstance();
//    GameRegistry.addRecipe(recipe);
//    RecipeSorter.INSTANCE.register(resourcePrefix + name, recipeClass, category, dependencies);
//    return recipe;
    throw new NotImplementedError();
  }

  /**
   * Gets a Block from the blocks map. Not recommended, Block instances should be saved in variables for easy access.
   */
  @Deprecated
  public Block getBlock(String key) {

    return blocks.get(key);
  }

  /**
   * Gets an Item from the items map. Not recommended, Item instances should be saved in variables for easy access.
   */
  @Deprecated
  public Item getItem(String key) {

    return items.get(key);
  }

  /**
   * Call in the "preInit" phase in your common proxy.
   */
  public void preInit() {

    if (mod == null) {
      ModContainer container = Loader.instance().getIndexedModList().get(modId);
      if (container != null)
        mod = container.getMod();
      else if (logHelper != null)
        logHelper.warning("SRegistry for this mod failed to get the mod instance! This could be"
            + " because the provided mod ID is incorrect.");
    }

    addOreDictEntries();
  }

  /**
   * Call in the "init" phase in your common proxy.
   */
  public void init() {

    addRecipes();
  }

  /**
   * Call in the "postInit" phase in your common proxy.
   */
  public void postInit() {

  }

  /**
   * Call in the "preInit" phase in your client proxy.
   */
  public void clientPreInit() {

    // registerModelVariants();
  }

  /**
   * Call in the "init" phase in your client proxy.
   */
  public void clientInit() {

    registerModels();
  }

  /**
   * Call in the "postInit" phase in your client proxy.
   */
  public void clientPostInit() {

    if (listModelsInPost)
      for (IRegistryObject obj : registryObjects)
        for (ModelResourceLocation model : obj.getVariants())
          if (model != null)
            System.out.println(model);
  }

  /**
   * @deprecated Use addRecipes and addOreDictEntries instead.
   */
  @Deprecated
  protected void addRecipesAndOreDictEntries() {

    for (IRegistryObject obj : registryObjects) {
      obj.addOreDict();
      obj.addRecipes(recipes);
    }
  }

  protected void addRecipes() {

    for (IRegistryObject obj : registryObjects)
      obj.addRecipes(recipes);
  }

  protected void addOreDictEntries() {

    for (IRegistryObject obj : registryObjects)
      obj.addOreDict();
  }

  @SideOnly(Side.CLIENT)
  protected void registerModels() {

    ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    ModelResourceLocation model;

    for (IRegistryObject obj : registryObjects) {
      if (!obj.registerModels()) {
        Item item = obj instanceof Block ? Item.getItemFromBlock((Block) obj) : (Item) obj;
        List<ModelResourceLocation> models = obj.getVariants();

        for (int i = 0; i < models.size(); ++i) {
          model = models.get(i);
          if (model != null) {
            ModelLoader.registerItemVariants(item, model);
            mesher.register(item, i, model);
          }
        }
      }
    }
  }
}
