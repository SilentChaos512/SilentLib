/*
 * SRegistry
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.registry;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.block.BlockMetaSubtypes;
import net.silentchaos512.lib.block.IColoredBlock;
import net.silentchaos512.lib.block.ITileEntityBlock;
import net.silentchaos512.lib.client.render.TEISRCustomEnchantedEffect;
import net.silentchaos512.lib.item.IColoredItem;
import net.silentchaos512.lib.item.ICustomEnchantColor;
import net.silentchaos512.lib.item.ItemBlockMetaSubtypes;
import net.silentchaos512.lib.util.GameUtil;
import net.silentchaos512.lib.util.LogHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class SRegistry {
    private static final Pattern PATTERN_REGISTRY_NAME = Pattern.compile("[^a-z0-9_]+");

    @Getter
    private final List<Block> blocks = NonNullList.create();
    @Getter
    private final List<Item> items = NonNullList.create();

    private final List<IAddRecipes> recipeAdders = NonNullList.create();
    private final List<Block> coloredBlocks = NonNullList.create();
    private final List<Item> coloredItems = NonNullList.create();

    private final List<IPhasedInitializer> phasedInitializers = new ArrayList<>();
    private final Map<Class<? extends IForgeRegistryEntry<?>>, Consumer<SRegistry>> registrationHandlers = new HashMap<>();

    private Object mod;
    @Deprecated
    private final LogHelper logHelper;
    private final Logger logger;
    private final String modId;
    private final String resourcePrefix;

    @Nonnull
    private final RecipeMaker recipes;

    @Getter
    @Setter
    @Nullable
    private CreativeTabs defaultCreativeTab = null;

    /**
     * Constructor which automatically acquires the mod container to populate required fields.
     *
     * @since 2.3.16
     */
    public SRegistry() {
        ModContainer mod = Objects.requireNonNull(Loader.instance().activeModContainer());
        this.modId = mod.getModId();
        this.resourcePrefix = this.modId + ":";
        this.logHelper = new LogHelper(mod.getName() + "/SRegistry", 1);
        this.logger = LogManager.getLogger(mod.getName() + "/SRegistry");
        this.recipes = new RecipeMaker(modId);
        MinecraftForge.EVENT_BUS.register(new EventHandler(this));
    }

    public RecipeMaker getRecipeMaker() {
        return recipes;
    }

    /**
     * Set the mod instance object
     */
    public void setMod(Object mod) {
        this.mod = mod;
    }

    /**
     * Gets a {@link LogHelper} object to use. If {@link #logHelper} is null, uses Silent Lib's.
     */
    @Deprecated
    private LogHelper logger() {
        return this.logHelper != null ? this.logHelper : SilentLib.logHelper;
    }

    /**
     * Add a phased initializer, which has preInit, init, and postInit methods which SRegistry will
     * call automatically.
     * <p>This method should be called during <em>pre-init</em> in the proper proxy,
     * <em>before</em> calling the SRegistry's preInit method.</p>
     *
     * @param instance Your initializer (singleton design is recommended)
     * @since 2.3.2
     */
    public void addPhasedInitializer(IPhasedInitializer instance) {
        this.phasedInitializers.add(instance);
    }

    /**
     * Adds a function that will be called when it is time to register objects for a certain class.
     * For example, adding a handler for class {@link Item} will call the function during {@link
     * RegistryEvent.Register} for type {@link Item}.
     * <p>This method should be called during <em>pre-init</em> in the proper proxy.</p>
     *
     * @param registerFunction The function to call
     * @param registryClass    The registry object class
     * @throws RuntimeException if a handler for the class is already registered
     * @since 2.3.16
     */
    // Interestingly, the deprecated method is still called with plain consumers... OK for now, I guess?
    public void addRegistrationHandler(Consumer<SRegistry> registerFunction, Class<? extends IForgeRegistryEntry<?>> registryClass) throws RuntimeException {
        if (this.registrationHandlers.containsKey(registryClass))
            throw new RuntimeException("Registration handler for class " + registryClass + " already registered!");
        this.registrationHandlers.put(registryClass, registerFunction);
    }

    public CreativeTabs makeCreativeTab(String label, Supplier<ItemStack> icon) {
        CreativeTabs tab = new CreativeTabs(label) {
            @Override
            public ItemStack createIcon() {
                return icon.get();
            }
        };
        if (defaultCreativeTab == null) defaultCreativeTab = tab;
        return tab;
    }

    //region Standard register methods (usually called within a registration handler)

    // Block

    /**
     * Register a Block. Its name (registry key/name) must be provided. Uses a new ItemBlockSL.
     */
    public <T extends Block> T registerBlock(T block, String key) {
        return registerBlock(block, key, defaultItemBlock(block));
    }

    @Nonnull
    private <T extends Block> ItemBlock defaultItemBlock(T block) {
        if (block instanceof BlockMetaSubtypes)
            return new ItemBlockMetaSubtypes((BlockMetaSubtypes) block);
        else
            return new ItemBlock(block);
    }

    /**
     * Register a Block. Its name registry name and ItemBlock must be provided.
     */
    public <T extends Block> T registerBlock(T block, String key, ItemBlock itemBlock) {
        blocks.add(block);
        block.setTranslationKey(modId + "." + key);

        validateRegistryName(key);
        ResourceLocation name = new ResourceLocation(modId, key);
        safeSetRegistryName(block, name);
        ForgeRegistries.BLOCKS.register(block);

        // Register ItemBlock; TODO: Should this be done in Item register event?
        safeSetRegistryName(itemBlock, name);
        ForgeRegistries.ITEMS.register(itemBlock);

        // Register TileEntity
        if (block instanceof ITileEntityBlock) {
            Class<? extends TileEntity> clazz = ((ITileEntityBlock) block).getTileEntityClass();
            registerTileEntity(clazz, key);
        }

        if (block instanceof IAddRecipes) {
            this.recipeAdders.add((IAddRecipes) block);
        }

        if (GameUtil.isClient() && block instanceof IColoredBlock) {
            this.coloredBlocks.add(block);
        }

        if (defaultCreativeTab != null) {
            block.setCreativeTab(defaultCreativeTab);
        }

        return block;
    }

    // Item

    /**
     * Register an Item. Its name (registry key/name) must be provided.
     */
    public <T extends Item> T registerItem(T item, String key) {
        items.add(item);
        item.setTranslationKey(modId + "." + key);

        validateRegistryName(key);
        ResourceLocation name = new ResourceLocation(modId, key);
        safeSetRegistryName(item, name);
        ForgeRegistries.ITEMS.register(item);

        if (item instanceof IAddRecipes) {
            this.recipeAdders.add((IAddRecipes) item);
        }

        if (GameUtil.isClient() && item instanceof IColoredItem) {
            this.coloredItems.add(item);
        }

        if (defaultCreativeTab != null) {
            item.setCreativeTab(defaultCreativeTab);
        }

        return item;
    }

    // Enchantment

    public void registerEnchantment(Enchantment enchantment, String key) {
        validateRegistryName(key);
        ResourceLocation name = new ResourceLocation(modId, key);
        safeSetRegistryName(enchantment, name);
        enchantment.setName(name.getNamespace() + "." + name.getPath());
        ForgeRegistries.ENCHANTMENTS.register(enchantment);
    }

    // Entity

    /**
     * Automatically incrementing ID number for registering entities.
     */
    private int lastEntityId = -1;

    public void registerEntity(Class<? extends Entity> entityClass, String key) {
        registerEntity(entityClass, key, ++lastEntityId, mod, 64, 20, true);
    }

    public void registerEntity(Class<? extends Entity> entityClass, String key, int trackingRange, int updateFrequency,
                               boolean sendsVelocityUpdates) {
        registerEntity(entityClass, key, ++lastEntityId, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    public void registerEntity(Class<? extends Entity> entityClass, String key, int id, Object mod, int trackingRange,
                               int updateFrequency, boolean sendsVelocityUpdates) {
        ResourceLocation resource = new ResourceLocation(modId, key);
        EntityRegistry.registerModEntity(resource, entityClass, key, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    public void registerEntity(Class<? extends Entity> entityClass, String key, int trackingRange, int updateFrequency,
                               boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary) {
        registerEntity(entityClass, key, ++lastEntityId, mod, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary);
    }

    public void registerEntity(Class<? extends Entity> entityClass, String key, int id, Object mod, int trackingRange,
                               int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary) {
        ResourceLocation resource = new ResourceLocation(modId, key);
        EntityRegistry.registerModEntity(resource, entityClass, key, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary);
    }

    @SideOnly(Side.CLIENT)
    public <T extends Entity> void registerEntityRenderer(Class<T> entityClass, IRenderFactory<T> renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
    }

    // Potion

    public void registerPotion(Potion potion, String key) {
        if (potion.getName().isEmpty())
            potion.setPotionName("effect." + modId + "." + key);

        validateRegistryName(key);
        ResourceLocation name = new ResourceLocation(this.modId, key);
        safeSetRegistryName(potion, name);
        ForgeRegistries.POTIONS.register(potion);
    }

    // Sound Events

    public void registerSoundEvent(SoundEvent sound, String key) {
        validateRegistryName(key);
        ResourceLocation name = new ResourceLocation(modId, key);
        safeSetRegistryName(sound, name);
        ForgeRegistries.SOUND_EVENTS.register(sound);
    }

    // Loot

    public void registerLootCondition(LootCondition.Serializer<? extends LootCondition> serializer) {
        LootConditionManager.registerCondition(serializer);
    }

    public void registerLootEntityProperty(EntityProperty.Serializer<? extends EntityProperty> serializer) {
        EntityPropertyManager.registerProperty(serializer);
    }

    public void registerLootFunction(LootFunction.Serializer<? extends LootFunction> serializer) {
        LootFunctionManager.registerFunction(serializer);
    }

    public void registerLootTable(String name) {
        LootTableList.register(new ResourceLocation(this.modId, name));
    }

    /**
     * Set the object's registry name, if it has not already been set. Logs a warning if it has.
     */
    private void safeSetRegistryName(IForgeRegistryEntry<?> entry, ResourceLocation name) {
        if (entry.getRegistryName() == null)
            entry.setRegistryName(name);
        else
            logger.warn("Registry name for {} has already been set. Was trying to set it to {}.", entry.getRegistryName(), name);
    }

    /**
     * Ensure the given name does not contain upper case letters. This is not a problem until 1.13,
     * so just log it as a warning.
     */
    private void validateRegistryName(String name) {
        if (PATTERN_REGISTRY_NAME.matcher(name).matches())
            logger.warn("Invalid name for object: {}", name);
    }

    // Advancements
    public <T extends ICriterionInstance> ICriterionTrigger<T> registerAdvancementTrigger(ICriterionTrigger<T> trigger) {
        CriteriaTriggers.register(trigger);
        return trigger;
    }

    /**
     * Register a TileEntity. "tile." + resourcePrefix is automatically prepended to the key.
     */
    public void registerTileEntity(Class<? extends TileEntity> tileClass, String key) {
        GameRegistry.registerTileEntity(tileClass, new ResourceLocation(modId, key));
    }

    /**
     * Registers a renderer for a TileEntity.
     */
    @SideOnly(Side.CLIENT)
    public <T extends TileEntity> void registerTileEntitySpecialRenderer(Class<T> tileClass, TileEntitySpecialRenderer<T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
    }

    // Model registration wrappers

    @SideOnly(Side.CLIENT)
    public void setModel(Block block, int meta, String modelPath) {
        setModel(Item.getItemFromBlock(block), meta, modelPath, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public void setModel(Block block, int meta, String modelPath, String variant) {
        setModel(Item.getItemFromBlock(block), meta, modelPath, variant);
    }

    @SideOnly(Side.CLIENT)
    public void setModel(Item item, int meta, String modelPath) {
        setModel(item, meta, modelPath, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public void setModel(Item item, int meta, String modelPath, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(this.resourcePrefix + modelPath, variant));
    }

    //endregion

    //region Initialization phases

    /*
     * Initialization phases. Calling in either your common or client proxy is recommended. "client" methods in your
     * client proxy, the rest in your common AND client proxy.
     */

    private boolean preInitDone = false;
    private boolean initDone = false;
    private boolean postInitDone = false;

    /**
     * Call in the "preInit" phase in your common proxy.
     */
    public void preInit(FMLPreInitializationEvent event) {
        if (this.preInitDone) {
            logger.warn("preInit called more than once!");
            return;
        }

        verifyOrFindModObject();
        this.phasedInitializers.forEach(i -> i.preInit(this, event));
        this.preInitDone = true;
    }

    private void verifyOrFindModObject() {
        if (mod == null) {
            logger.warn("Mod {} did not manually set its mod object! This is bad and may cause crashes.", modId);
            ModContainer container = Loader.instance().getIndexedModList().get(modId);
            if (container != null) {
                this.mod = container.getMod();
                logger.warn("Automatically acquired mod object for {}", modId);
            } else {
                logger.warn("Could not find mod object. The mod ID is likely incorrect.");
            }
        }
    }

    /**
     * Call in the "init" phase in your common proxy.
     */
    public void init(FMLInitializationEvent event) {
        if (this.initDone) {
            logger.warn("init called more than once!");
            return;
        }
        this.phasedInitializers.forEach(i -> i.init(this, event));
        this.initDone = true;
    }

    /**
     * Call in the "postInit" phase in your common proxy.
     */
    public void postInit(FMLPostInitializationEvent event) {
        if (this.postInitDone) {
            logger.warn("postInit called more than once!");
            return;
        }

        int oldRecipeRegisterCount = recipes.getOldRecipeRegisterCount();
        if (oldRecipeRegisterCount > 0) {
            long totalRecipes = ForgeRegistries.RECIPES.getKeys().stream()
                    .map(ResourceLocation::getNamespace)
                    .filter(s -> s.equals(modId))
                    .count();
            logger.warn("Mod '{}' is still registering recipes with RecipeMaker ({} recipes, out of {} total)",
                    modId, oldRecipeRegisterCount, totalRecipes);
        }

        this.phasedInitializers.forEach(i -> i.postInit(this, event));
        this.postInitDone = true;
    }

    /**
     * Call in the "preInit" phase in your client proxy.
     */
    @SideOnly(Side.CLIENT)
    public void clientPreInit(FMLPreInitializationEvent event) {
    }

    /**
     * Call in the "init" phase in your client proxy.
     */
    @SideOnly(Side.CLIENT)
    public void clientInit(FMLInitializationEvent event) {
        for (Block block : this.blocks) {
            if (block instanceof ITileEntityBlock) {
                ITileEntityBlock tileBlock = (ITileEntityBlock) block;
                final TileEntitySpecialRenderer tesr = tileBlock.getTileRenderer();
                if (tesr != null) {
                    ClientRegistry.bindTileEntitySpecialRenderer(tileBlock.getTileEntityClass(), tesr);
                }
            }
        }

        for (Item item : this.items) {
            if (item instanceof ICustomEnchantColor) {
                item.setTileEntityItemStackRenderer(TEISRCustomEnchantedEffect.INSTANCE);
            }
        }
    }

    /**
     * Call in the "postInit" phase in your client proxy.
     *
     * @param event
     */
    @SideOnly(Side.CLIENT)
    public void clientPostInit(FMLPostInitializationEvent event) {
    }

    //endregion

    @SuppressWarnings("deprecation")
    private void addRecipes() {
        this.recipeAdders.forEach(obj -> obj.addRecipes(this.recipes));
    }

    @SuppressWarnings("deprecation")
    private void addOreDictEntries() {
        this.recipeAdders.forEach(IAddRecipes::addOreDict);
    }

    @SideOnly(Side.CLIENT)
    private void registerModels() {
        for (Block block : blocks) {
            if (block instanceof ICustomModel) {
                ((ICustomModel) block).registerModels();
            } else {
                ResourceLocation registryName = Objects.requireNonNull(block.getRegistryName());
                ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, model);
            }
        }
        for (Item item : items) {
            if (item instanceof ICustomMesh) {
                ICustomMesh customMesh = (ICustomMesh) item;
                ModelBakery.registerItemVariants(item, customMesh.getVariants());
                ModelLoader.setCustomMeshDefinition(item, customMesh.getCustomMesh());
            } else if (item instanceof ICustomModel) {
                ((ICustomModel) item).registerModels();
            } else {
                ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());
                ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
                ModelLoader.setCustomModelResourceLocation(item, 0, model);
            }
        }
    }

    /**
     * Handles the new Forge RegistryEvents. An instance will automatically be registered when an
     * SRegistry is constructed.
     *
     * @author SilentChaos512
     * @since 2.2.2
     */
    @SuppressWarnings("unused")
    public static class EventHandler {
        private final SRegistry sregistry;

        public EventHandler(SRegistry sregistry) {
            this.sregistry = sregistry;
        }

        private void runRegistrationHandlerIfPresent(Class<? extends IForgeRegistryEntry<?>> registryClass) {
            if (sregistry.registrationHandlers.containsKey(registryClass))
                sregistry.registrationHandlers.get(registryClass).accept(sregistry);
        }

        @SubscribeEvent
        public void registerBlocks(RegistryEvent.Register<Block> event) {
            runRegistrationHandlerIfPresent(Block.class);
        }

        @SubscribeEvent
        public void registerItems(RegistryEvent.Register<Item> event) {
            runRegistrationHandlerIfPresent(Item.class);
            sregistry.addOreDictEntries();
        }

        @SubscribeEvent
        public void registerPotions(RegistryEvent.Register<Potion> event) {
            runRegistrationHandlerIfPresent(Potion.class);
        }

        @SubscribeEvent
        public void registerBiomes(RegistryEvent.Register<Biome> event) {
            runRegistrationHandlerIfPresent(Biome.class);
        }

        @SubscribeEvent
        public void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
            runRegistrationHandlerIfPresent(SoundEvent.class);
        }

        @SubscribeEvent
        public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
            runRegistrationHandlerIfPresent(PotionType.class);
        }

        @SubscribeEvent
        public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
            runRegistrationHandlerIfPresent(Enchantment.class);
        }

        @SubscribeEvent
        public void registerVillagerProfessions(RegistryEvent.Register<VillagerProfession> event) {
            runRegistrationHandlerIfPresent(VillagerProfession.class);
        }

        @SubscribeEvent
        public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
            runRegistrationHandlerIfPresent(EntityEntry.class);
        }

        @SubscribeEvent
        public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
            runRegistrationHandlerIfPresent(IRecipe.class);
            sregistry.addRecipes();
        }

        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event) {
            sregistry.registerModels();
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void registerBlockColors(ColorHandlerEvent.Block event) {
            BlockColors blockColors = event.getBlockColors();
            for (Block block : sregistry.coloredBlocks)
                blockColors.registerBlockColorHandler(((IColoredBlock) block).getColorHandler(), block);
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void registerItemColors(ColorHandlerEvent.Item event) {
            ItemColors itemColors = event.getItemColors();
            for (Block block : sregistry.coloredBlocks)
                itemColors.registerItemColorHandler(((IColoredBlock) block).getItemColorHandler(), Item.getItemFromBlock(block));
            for (Item item : sregistry.coloredItems)
                itemColors.registerItemColorHandler(((IColoredItem) item).getColorHandler(), item);
        }
    }
}
