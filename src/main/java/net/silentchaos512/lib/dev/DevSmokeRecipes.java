package net.silentchaos512.lib.dev;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.component.LootContainer;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.lib.item.LootContainerItem;

import java.util.List;

/**
 * Recipe types used only by the development client/server smoke test. This package and its recipe JSON files are
 * excluded from release artifacts in {@code build.gradle}.
 */
@EventBusSubscriber(modid = SilentLib.MOD_ID)
public final class DevSmokeRecipes {
    private static final RecipeSerializer<Shapeless> SHAPELESS = ExtendedShapelessRecipe.basicSerializer(Shapeless::new);
    private static final RecipeSerializer<Shaped> SHAPED = ExtendedShapedRecipe.basicSerializer(Shaped::new);
    private static final ResourceKey<LootTable> LOOT_BAG_TABLE = ResourceKey.create(Registries.LOOT_TABLE, SilentLib.getId("dev_smoke_loot_bag"));
    private static final ResourceKey<PlacedFeature> OVERWORLD_ONLY_FEATURE = ResourceKey.create(Registries.PLACED_FEATURE, SilentLib.getId("dev_smoke_overworld_only"));

    private DevSmokeRecipes() {
    }

    @SubscribeEvent
    private static void registerSerializers(RegisterEvent event) {
        event.register(Registries.RECIPE_SERIALIZER, SilentLib.getId("dev_smoke_shapeless"), () -> SHAPELESS);
        event.register(Registries.RECIPE_SERIALIZER, SilentLib.getId("dev_smoke_shaped"), () -> SHAPED);
        var lootBagId = SilentLib.getId("dev_smoke_loot_bag");
        event.register(
                Registries.ITEM,
                lootBagId,
                () -> new LootContainerItem(
                        true,
                        new Item.Properties()
                                .setId(ResourceKey.create(Registries.ITEM, lootBagId))
                                .component(SilentLib.LOOT_CONTAINER.get(), new LootContainer(LOOT_BAG_TABLE))
                )
        );
    }

    @SubscribeEvent
    private static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("sl_smoke_place_feature")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> placeOverworldOnlyFeature(context, BlockPos.containing(context.getSource().getPosition())))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> placeOverworldOnlyFeature(context, BlockPosArgument.getLoadedBlockPos(context, "pos"))))
        );
    }

    private static int placeOverworldOnlyFeature(CommandContext<CommandSourceStack> context, BlockPos pos) {
        CommandSourceStack source = context.getSource();
        var level = source.getLevel();
        PlacedFeature feature = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(OVERWORLD_ONLY_FEATURE).value();
        boolean placed = feature.place(level, level.getChunkSource().getGenerator(), level.getRandom(), pos);
        if (placed) {
            source.sendSuccess(() -> Component.literal("SilentLib smoke feature placed at " + pos.toShortString()), true);
            return 1;
        }
        source.sendFailure(Component.literal("SilentLib smoke feature was blocked by its dimension filter"));
        return 0;
    }

    private static final class Shapeless extends ExtendedShapelessRecipe {
        private Shapeless(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
            super(commonInfo, bookInfo, result, ingredients);
        }

        @Override
        public RecipeSerializer<Shapeless> getSerializer() {
            return SHAPELESS;
        }
    }

    private static final class Shaped extends ExtendedShapedRecipe {
        private Shaped(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
            super(commonInfo, bookInfo, pattern, result);
        }

        @Override
        public RecipeSerializer<Shaped> getSerializer() {
            return SHAPED;
        }
    }
}
