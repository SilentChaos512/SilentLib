package net.silentchaos512.lib.registry;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.MapMaker;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.lib.item.ItemBlockSL;
import net.silentchaos512.lib.util.LogHelper;

public class SRegistry {

  // Internal use only!
  private final Set<IRegistryObject> registryObjects = new HashSet<>();

  protected IRegistrationHandler handlerBlocks;
  protected IRegistrationHandler handlerItems;
  protected IRegistrationHandler handlerEnchantments;
  protected IRegistrationHandler handlerRecipes;

  /** A reference to the mod's instance object. */
  protected Object mod;
  /** The LogHelper for the mod, if any. May be used to log error messages. */
  protected LogHelper logHelper;

  /** The mod ID for the mod this SRegistry instance belongs to. */
  public final String modId;
  /** The resource prefix for the mod. This is set in the constructor based on the modId. */
  public final String resourcePrefix;

  public RecipeMaker recipes;

  protected boolean listModelsInPost = false;

  public SRegistry(String modId) {

    this.modId = modId;
    this.resourcePrefix = modId.toLowerCase() + ":";
    this.recipes = new RecipeMaker(modId);
    MinecraftForge.EVENT_BUS.register(new EventHandler(this));
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

  public <T> IRegistrationHandler<T> addRegistrationHandler(IRegistrationHandler<T> handler, Class<T> clazz) {

    if (clazz == Block.class)
      handlerBlocks = handler;
    else if (clazz == Item.class)
      handlerItems = handler;
    else if (clazz == Enchantment.class)
      handlerEnchantments = handler;
    else if (clazz == IRecipe.class)
      handlerRecipes = handler;
    // TODO
    return handler;
  }

  /********************************************************************************************************************
   * Register methods. Should be called in the appropriate IRegistrationHandler (your ModBlocks, ModItems, etc.).
   * Recipes should be registers in the block/item's addRecipe method in most cases, but you can use a handler as well.
   ********************************************************************************************************************/

  // Block

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

    if (block instanceof IRegistryObject) {
      registryObjects.add((IRegistryObject) block);
    }

    ResourceLocation name = new ResourceLocation(resourcePrefix + key);
    safeSetRegistryName(block, name);
    ForgeRegistries.BLOCKS.register(block);
    if (itemBlock != null) {
      safeSetRegistryName(itemBlock, name);
      ForgeRegistries.ITEMS.register(itemBlock);
    }

    return block;
  }

  // Item

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

    if (item instanceof IRegistryObject) {
      registryObjects.add((IRegistryObject) item);
    }

    ResourceLocation name = new ResourceLocation(resourcePrefix + key);
    safeSetRegistryName(item, name);
    ForgeRegistries.ITEMS.register(item);

    return item;
  }

  // Enchantment

  public void registerEnchantment(Enchantment ench, String key) {

    ResourceLocation name = new ResourceLocation(resourcePrefix + key);
    safeSetRegistryName(ench, name);
    ForgeRegistries.ENCHANTMENTS.register(ench);
  }

  // Entity

  /** Automatically incrementing ID number for registering entities. */
  protected int lastEntityId = -1;

  public void registerEntity(Class<? extends Entity> entityClass, String key) {

    registerEntity(entityClass, key, ++lastEntityId, mod, 64, 20, true);
  }

  public void registerEntity(Class<? extends Entity> entityClass, String key, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {

    registerEntity(entityClass, key, ++lastEntityId, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
  }

  public void registerEntity(Class<? extends Entity> entityClass, String key, int id, Object mod, int trackingRange, int updateFrequency,
      boolean sendsVelocityUpdates) {

    ResourceLocation resource = new ResourceLocation(modId, key);
    EntityRegistry.registerModEntity(resource, entityClass, key, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
  }

  @SideOnly(Side.CLIENT)
  public <T extends Entity> void registerEntityRenderer(Class<T> entityClass, IRenderFactory<T> renderFactory) {

    RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
  }

  public void safeSetRegistryName(IForgeRegistryEntry entry, ResourceLocation name) {

    if (entry.getRegistryName() == null) {
      entry.setRegistryName(name);
    }
  }

  /********************************************************************************************************************
   * TileEntity registration. These aren't Forge registries, but these need to be wrapped for xcompt (I think?)
   ********************************************************************************************************************/

  /**
   * Register a TileEntity. "tile." + resourcePrefix is automatically prepended to the key.
   */
  public void registerTileEntity(Class<? extends TileEntity> tileClass, String key) {

    String fullKey = "tile." + resourcePrefix + key;
    GameRegistry.registerTileEntity(tileClass, fullKey);
  }

  /**
   * Registers a renderer for a TileEntity.
   * 
   * @param tileClass
   * @param renderer
   */
  @SideOnly(Side.CLIENT)
  public <T extends TileEntity> void registerTileEntitySpecialRenderer(Class<T> tileClass, TileEntitySpecialRenderer<T> renderer) {

    ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
  }

  /*******************************************************************************************************************
   * Initialization phases. Calling in either your common or client proxy is recommended. "client" methods in your
   * client proxy, the rest in your common AND client proxy.
   *******************************************************************************************************************/

  /**
   * Call in the "preInit" phase in your common proxy.
   */
  public void preInit() {

    if (mod == null) {
      ModContainer container = Loader.instance().getIndexedModList().get(modId);
      if (container != null)
        mod = container.getMod();
      else if (logHelper != null)
        logHelper.warning("SRegistry for this mod failed to get the mod instance! This could be because the provided mod ID is incorrect.");
    }
  }

  /**
   * Call in the "init" phase in your common proxy.
   */
  public void init() {

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

  }

  /**
   * Call in the "init" phase in your client proxy.
   */
  public void clientInit() {

  }

  /**
   * Call in the "postInit" phase in your client proxy.
   */
  public void clientPostInit() {

    if (listModelsInPost) {
      Map<Integer, ModelResourceLocation> models = new MapMaker().makeMap();
      for (IRegistryObject obj : registryObjects) {
        models.clear();
        obj.getModels(models);
        for (ModelResourceLocation model : models.values()) {
          if (model != null) {
            System.out.println(model);
          }
        }
      }
    }
  }

  @Deprecated
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

    ModelResourceLocation model;
    Map<Integer, ModelResourceLocation> models = new MapMaker().initialCapacity(16).makeMap();

    for (IRegistryObject obj : registryObjects) {
      if (!obj.registerModels()) {
        Item item = obj instanceof Block ? Item.getItemFromBlock((Block) obj) : (Item) obj;
        models.clear();
        obj.getModels(models);
        models.entrySet().forEach(entry -> ModelLoader.setCustomModelResourceLocation(item, entry.getKey(), entry.getValue()));
      }
    }
  }

  /**
   * Handles the new Forge RegistryEvents. An instance will automatically be registered when an SRegistry is
   * constructed.
   * 
   * @author SilentChaos512
   * @since 2.2.2
   */
  public static class EventHandler {

    protected SRegistry sregistry;

    public EventHandler(SRegistry sregistry) {

      this.sregistry = sregistry;
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {

      if (sregistry.handlerBlocks != null) {
        sregistry.handlerBlocks.registerAll(sregistry);
      }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {

      if (sregistry.handlerItems != null) {
        sregistry.handlerItems.registerAll(sregistry);
      }

      sregistry.addOreDictEntries();
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {

      // TODO
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {

      // TODO
    }

    @SubscribeEvent
    public void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {

      // TODO
    }

    @SubscribeEvent
    public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {

      // TODO
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {

      if (sregistry.handlerEnchantments != null) {
        sregistry.handlerEnchantments.registerAll(sregistry);
      }
    }

    @SubscribeEvent
    public void registerVillagerProfessions(RegistryEvent.Register<VillagerProfession> event) {

      // TODO
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {

      // TODO
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

      if (sregistry.handlerRecipes != null) {
        sregistry.handlerRecipes.registerAll(sregistry);
      }
      sregistry.addRecipes();
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {

      sregistry.registerModels();
    }
  }
}
