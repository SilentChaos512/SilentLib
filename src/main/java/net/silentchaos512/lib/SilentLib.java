package net.silentchaos512.lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.Identifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.lib.command.internal.TeleportCommand;
import net.silentchaos512.lib.component.LootContainer;
import net.silentchaos512.lib.crafting.ingredient.IngredientWithCount;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.lib.world.placement.DimensionFilterPlacement;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mod(SilentLib.MOD_ID)
public final class SilentLib {
    public static final String MOD_ID = "silentlib";
    public static final Logger LOGGER = LogUtils.getLogger();

    static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES_REGISTRAR =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE_REGISTRAR =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MOD_ID);
    static final DeferredRegister<Consumer<GameTestHelper>> GAME_TEST_FUNCTIONS =
            DeferredRegister.create(Registries.TEST_FUNCTION, MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LootContainer>> LOOT_CONTAINER = DATA_COMPONENT_TYPES_REGISTRAR.register(
            "loot_container",
            () -> DataComponentType.<LootContainer>builder()
                    .persistent(LootContainer.CODEC)
                    .networkSynchronized(LootContainer.STREAM_CODEC)
                    .build()
    );

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<DimensionFilterPlacement>> DIMENSION_FILTER_PLACEMENT = PLACEMENT_MODIFIER_TYPE_REGISTRAR.register(
            "dimension_filter",
            () -> () -> DimensionFilterPlacement.CODEC
    );

    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> INGREDIENT_WITH_COUNT_CODEC_TEST = GAME_TEST_FUNCTIONS.register(
            "ingredient_with_count_codec",
            () -> SilentLib::ingredientWithCountCodecTest
    );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> RECIPE_CODEC_TEST = GAME_TEST_FUNCTIONS.register(
            "recipe_codec",
            () -> SilentLib::recipeCodecTest
    );

    private static final RecipeSerializer<TestShapelessRecipe> TEST_SHAPELESS_SERIALIZER = ExtendedShapelessRecipe.basicSerializer(TestShapelessRecipe::new);
    private static final RecipeSerializer<TestShapedRecipe> TEST_SHAPED_SERIALIZER = ExtendedShapedRecipe.basicSerializer(TestShapedRecipe::new);

    public SilentLib(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES_REGISTRAR.register(modEventBus);
        PLACEMENT_MODIFIER_TYPE_REGISTRAR.register(modEventBus);
        GAME_TEST_FUNCTIONS.register(modEventBus);
        modEventBus.addListener(this::registerGameTests);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
//        ILeftClickItem.EventHandler.init();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        TeleportCommand.register(dispatcher);
    }

    private void registerGameTests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(getId("default"));
        var testData = new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 20, 1, true, Rotation.NONE);
        event.registerTest(
                getId("ingredient_with_count_codec"),
                data -> new FunctionGameTestInstance(INGREDIENT_WITH_COUNT_CODEC_TEST.getKey(), data),
                testData
        );
        event.registerTest(
                getId("recipe_codec"),
                data -> new FunctionGameTestInstance(RECIPE_CODEC_TEST.getKey(), data),
                testData
        );
    }

    private static void ingredientWithCountCodecTest(GameTestHelper helper) {
        IngredientWithCount expected = IngredientWithCount.of(3, Items.DIAMOND);
        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());
        var encoded = IngredientWithCount.CODEC.encodeStart(ops, expected).getOrThrow();
        IngredientWithCount decoded = IngredientWithCount.CODEC.parse(ops, encoded).getOrThrow();

        helper.assertTrue(decoded.count() == 3, "Codec did not preserve the ingredient count");
        helper.assertTrue(decoded.ingredient().test(new ItemStack(Items.DIAMOND)), "Codec did not preserve the ingredient item");
        helper.assertTrue(!IngredientWithCount.EMPTY.test(new ItemStack(Items.DIAMOND)), "The empty ingredient must not match items");

        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.OTHER);
        expected.toNetwork(buffer);
        IngredientWithCount networkDecoded = IngredientWithCount.fromNetwork(buffer);

        helper.assertTrue(networkDecoded.count() == 3, "Network codec did not preserve the ingredient count");
        helper.assertTrue(networkDecoded.ingredient().test(new ItemStack(Items.DIAMOND)), "Network codec did not preserve the ingredient item");
        helper.succeed();
    }

    private static void recipeCodecTest(GameTestHelper helper) {
        var commonInfo = new Recipe.CommonInfo(false);
        var bookInfo = new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "codec_test");
        var result = new ItemStackTemplate(Items.EMERALD, 2);
        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());

        TestShapelessRecipe shapeless = new TestShapelessRecipe(commonInfo, bookInfo, result, List.of(Ingredient.of(Items.DIAMOND)));
        var shapelessCodec = ExtendedShapelessRecipe.basicCodec(TestShapelessRecipe::new).codec();
        TestShapelessRecipe decodedShapeless = shapelessCodec.parse(ops, shapelessCodec.encodeStart(ops, shapeless).getOrThrow()).getOrThrow();
        helper.assertTrue(decodedShapeless.ingredientCount() == 1, "Shapeless recipe codec did not preserve ingredients");
        helper.assertTrue(decodedShapeless.result().create().is(Items.EMERALD), "Shapeless recipe codec did not preserve the result");

        RegistryFriendlyByteBuf shapelessBuffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.OTHER);
        ExtendedShapelessRecipe.basicStreamCodec(TestShapelessRecipe::new).encode(shapelessBuffer, shapeless);
        TestShapelessRecipe networkShapeless = ExtendedShapelessRecipe.basicStreamCodec(TestShapelessRecipe::new).decode(shapelessBuffer);
        helper.assertTrue(networkShapeless.firstIngredient().test(new ItemStack(Items.DIAMOND)), "Shapeless recipe network codec did not preserve ingredients");

        TestShapedRecipe shaped = new TestShapedRecipe(
                commonInfo,
                bookInfo,
                ShapedRecipePattern.of(Map.of('A', Ingredient.of(Items.DIAMOND)), "AA"),
                result
        );
        var shapedCodec = ExtendedShapedRecipe.basicCodec(TestShapedRecipe::new).codec();
        TestShapedRecipe decodedShaped = shapedCodec.parse(ops, shapedCodec.encodeStart(ops, shaped).getOrThrow()).getOrThrow();
        helper.assertTrue(decodedShaped.getWidth() == 2 && decodedShaped.getHeight() == 1, "Shaped recipe codec did not preserve the pattern dimensions");
        helper.assertTrue(decodedShaped.result().create().getCount() == 2, "Shaped recipe codec did not preserve the result count");

        RegistryFriendlyByteBuf shapedBuffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.OTHER);
        ExtendedShapedRecipe.basicStreamCodec(TestShapedRecipe::new).encode(shapedBuffer, shaped);
        TestShapedRecipe networkShaped = ExtendedShapedRecipe.basicStreamCodec(TestShapedRecipe::new).decode(shapedBuffer);
        helper.assertTrue(networkShaped.getIngredients().getFirst().orElseThrow().test(new ItemStack(Items.DIAMOND)), "Shaped recipe network codec did not preserve ingredients");
        helper.succeed();
    }

    private static final class TestShapelessRecipe extends ExtendedShapelessRecipe {
        private TestShapelessRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
            super(commonInfo, bookInfo, result, ingredients);
        }

        @Override
        public RecipeSerializer<TestShapelessRecipe> getSerializer() {
            return TEST_SHAPELESS_SERIALIZER;
        }

        private int ingredientCount() {
            return this.ingredients.size();
        }

        private Ingredient firstIngredient() {
            return this.ingredients.getFirst();
        }

        private ItemStackTemplate result() {
            return this.result;
        }
    }

    private static final class TestShapedRecipe extends ExtendedShapedRecipe {
        private TestShapedRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
            super(commonInfo, bookInfo, pattern, result);
        }

        @Override
        public RecipeSerializer<TestShapedRecipe> getSerializer() {
            return TEST_SHAPED_SERIALIZER;
        }

        private ItemStackTemplate result() {
            return this.result;
        }
    }

    public static String getVersion() {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            return o.get().getModInfo().getVersion().toString();
        }
        return "0.0.0";
    }

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion());
    }

    public static Identifier getId(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
