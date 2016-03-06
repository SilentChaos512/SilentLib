package net.silentchaos512.lib.registry;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.silentchaos512.lib.block.BlockSL;
import net.silentchaos512.lib.item.ItemBlockSL;
import net.silentchaos512.lib.item.ItemSL;

public class SRegistry {

  private final Map<String, Block> blocks = Maps.newHashMap();
  private final Map<String, Item> items = Maps.newHashMap();
  private final List<IRegistryObject> registryObjects = Lists.newArrayList();

  public final String modId;
  public final String resourcePrefix;

  public SRegistry(String modId) {

    this.modId = modId;
    this.resourcePrefix = modId.toLowerCase() + ":";
  }

  public Block registerBlock(BlockSL block) {

    return registerBlock(block, block.getName());
  }

  public Block registerBlock(Block block, String key) {

    return registerBlock(block, key, ItemBlockSL.class);
  }

  public Block registerBlock(Block block, String key, Class<? extends ItemBlock> itemClass) {

    blocks.put(key, block);
    if (block instanceof IRegistryObject) {
      registryObjects.add((IRegistryObject) block);
    }
    GameRegistry.registerBlock(block, itemClass, key);
    return block;
  }

  public Item registerItem(ItemSL item) {

    return registerItem(item, item.getName());
  }

  public Item registerItem(Item item, String key) {

    items.put(key, item);
    if (item instanceof IRegistryObject) {
      registryObjects.add((IRegistryObject) item);
    }
    GameRegistry.registerItem(item, key);
    return item;
  }

  public void registerTileEntity(Class<? extends TileEntity> tileClass, String key) {

    GameRegistry.registerTileEntity(tileClass, "tile." + resourcePrefix + key);
  }

  public IRecipe addRecipeHandler(Class<? extends IRecipe> recipeClass, String name,
      Category category, String dependencies)
          throws InstantiationException, IllegalAccessException {

    IRecipe recipe = recipeClass.newInstance();
    GameRegistry.addRecipe(recipe);
    RecipeSorter.INSTANCE.register(resourcePrefix + name, recipeClass, category, dependencies);
    return recipe;
  }

  public Block getBlock(String key) {

    return blocks.get(key);
  }

  public Item getItem(String key) {

    return items.get(key);
  }

  public void preInit() {

    // TODO
  }

  public void init() {

    // TODO
    addRecipesAndOreDictEntries();
  }

  public void postInit() {

    // TODO
  }

  public void clientPreInit() {

    registerModelVariants();
  }

  public void clientInit() {

    registerModels();
  }

  public void clientPostInit() {

  }

  protected void addRecipesAndOreDictEntries() {

    for (IRegistryObject obj : registryObjects) {
      obj.addOreDict();
      obj.addRecipes();
    }
  }

  protected void registerModelVariants() {

    for (IRegistryObject obj : registryObjects) {
      Item item = obj instanceof Block ? Item.getItemFromBlock((Block) obj) : (Item) obj;
      List<ModelResourceLocation> models = obj.getVariants();
      // TODO: Remove null elements?

      ModelLoader.registerItemVariants(item,
          models.toArray(new ModelResourceLocation[models.size()]));

      // Custom mesh?
      // ItemMeshDefinition mesh = obj.getCustomMesh();
      // if (mesh != null) {
      // ModelLoader.setCustomMeshDefinition(item, mesh);
      // }
    }
  }

  protected void registerModels() {

    ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    for (IRegistryObject obj : registryObjects) {
      if (!obj.registerModels()) {
        Item item = obj instanceof Block ? Item.getItemFromBlock((Block) obj) : (Item) obj;
        List<ModelResourceLocation> models = obj.getVariants();
        for (int i = 0; i < models.size(); ++i) {
          if (models.get(i) != null) {
            mesher.register(item, i, models.get(i));
          }
        }
      }
    }
  }
}
