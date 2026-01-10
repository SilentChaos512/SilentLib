package net.silentchaos512.lib.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

/**
 * An ingredient paired with a required count. Useful for recipes that consume
 * multiple items of the same type.
 *
 * <h2>Migration Guide (Minecraft 1.21.5+ / NeoForge 26.1+)</h2>
 * <p>
 * Tag-based ingredient creation now requires a {@link HolderGetter} context.
 * If you were using {@code IngredientWithCount.of(count, tag)}, you now need to use
 * {@link #of(HolderGetter, int, TagKey)} instead.
 * </p>
 *
 * <h3>Before (no longer works):</h3>
 * <pre>{@code
 * IngredientWithCount ingredient = IngredientWithCount.of(3, ItemTags.COALS);
 * }</pre>
 *
 * <h3>After (in a RecipeProvider or similar context):</h3>
 * <pre>{@code
 * // In your RecipeProvider, you have access to HolderLookup.Provider
 * HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);
 * IngredientWithCount ingredient = IngredientWithCount.of(items, 3, ItemTags.COALS);
 * }</pre>
 *
 * <p>
 * Item-based creation (non-tag) still works without changes:
 * </p>
 * <pre>{@code
 * IngredientWithCount ingredient = IngredientWithCount.of(5, Items.DIAMOND);
 * }</pre>
 *
 * @author SilentChaos512
 * @since 3.0.0
 */
public record IngredientWithCount(Ingredient ingredient, int count) implements Predicate<ItemStack> {
    /**
     * Codec for serializing/deserializing IngredientWithCount.
     * Note: This codec requires registry context (RegistryOps) for proper ingredient serialization.
     */
    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(iwc -> iwc.ingredient),
                    Codec.INT.fieldOf("count").forGetter(iwc -> iwc.count)
            ).apply(instance, (ingredient, count) -> new IngredientWithCount(ingredient, count.intValue()))
    );

    /**
     * @deprecated Empty ingredients are no longer supported in Minecraft 1.21.5+.
     * The concept of an "empty" ingredient has been removed from the Ingredient API.
     * Consider using {@link java.util.Optional} to represent optional ingredients instead.
     */
    @Deprecated(forRemoval = true)
    public static final IngredientWithCount EMPTY = null;

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        return pStack != null && pStack.getCount() >= this.count && ingredient.test(pStack);
    }

    // ==================== Factory Methods ====================

    /**
     * @deprecated Empty ingredients are no longer supported in Minecraft 1.21.5+.
     * Consider using {@link java.util.Optional} to represent optional ingredients.
     * @throws UnsupportedOperationException always
     */
    @Deprecated(forRemoval = true)
    public static IngredientWithCount of() {
        throw new UnsupportedOperationException(
                "Empty ingredients are no longer supported in Minecraft 1.21.5+. " +
                "Consider using Optional<IngredientWithCount> for optional ingredients."
        );
    }

    /**
     * Creates an IngredientWithCount from one or more items.
     *
     * @param count The required count of items
     * @param items The items that satisfy this ingredient
     * @return A new IngredientWithCount
     */
    public static IngredientWithCount of(int count, ItemLike... items) {
        return of(count, Arrays.stream(items));
    }

    /**
     * Creates an IngredientWithCount from a stream of items.
     *
     * @param count The required count of items
     * @param items Stream of items that satisfy this ingredient
     * @return A new IngredientWithCount
     */
    public static IngredientWithCount of(int count, Stream<ItemLike> items) {
        return new IngredientWithCount(Ingredient.of(items), count);
    }

    /**
     * Creates an IngredientWithCount from an item tag.
     * <p>
     * This is the recommended way to create tag-based ingredients in Minecraft 1.21.5+.
     * You can obtain a HolderGetter from your RecipeProvider's registries parameter:
     * </p>
     * <pre>{@code
     * HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);
     * IngredientWithCount ingredient = IngredientWithCount.of(items, 3, ItemTags.COALS);
     * }</pre>
     *
     * @param items The item registry holder getter (from HolderLookup.Provider)
     * @param count The required count of items
     * @param tag   The item tag that satisfies this ingredient
     * @return A new IngredientWithCount
     * @since 12.0.0
     */
    public static IngredientWithCount of(HolderGetter<Item> items, int count, TagKey<Item> tag) {
        return new IngredientWithCount(Ingredient.of(items.getOrThrow(tag)), count);
    }

    /**
     * @deprecated Tag-based ingredient creation now requires a {@link HolderGetter} context.
     * Use {@link #of(HolderGetter, int, TagKey)} instead.
     * <p>
     * Migration example:
     * <pre>{@code
     * // Before:
     * IngredientWithCount.of(3, ItemTags.COALS)
     *
     * // After (in RecipeProvider):
     * HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);
     * IngredientWithCount.of(items, 3, ItemTags.COALS)
     * }</pre>
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated(forRemoval = true)
    public static IngredientWithCount of(int count, TagKey<Item> tag) {
        throw new UnsupportedOperationException(
                "Tag-based ingredient creation now requires a HolderGetter context. " +
                "Use IngredientWithCount.of(HolderGetter<Item>, int, TagKey<Item>) instead. " +
                "You can obtain a HolderGetter from registries.lookupOrThrow(Registries.ITEM) in your RecipeProvider."
        );
    }

    // ==================== Network Serialization ====================

    /**
     * Reads an IngredientWithCount from a network buffer.
     *
     * @param buf The buffer to read from
     * @return The deserialized IngredientWithCount
     */
    public static IngredientWithCount fromNetwork(RegistryFriendlyByteBuf buf) {
        return new IngredientWithCount(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readByte());
    }

    /**
     * Writes this IngredientWithCount to a network buffer.
     *
     * @param buf The buffer to write to
     */
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, this.ingredient);
        buf.writeByte(count);
    }
}
