package net.silentchaos512.lib.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record IngredientWithCount(Ingredient ingredient, int count) implements Predicate<ItemStack> {
    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(iwc -> iwc.ingredient),
                    Codec.INT.fieldOf("count").forGetter(iwc -> iwc.count)
            ).apply(instance, IngredientWithCount::new)
    );

    public static final IngredientWithCount EMPTY = new IngredientWithCount(Ingredient.of(), 0);

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        return pStack != null && pStack.getCount() >= this.count && ingredient.test(pStack);
    }

    public static IngredientWithCount of() {
        return EMPTY;
    }

    public static IngredientWithCount of(int count, ItemLike... items) {
        return of(count, Arrays.stream(items));
    }

    public static IngredientWithCount of(int count, Stream<ItemLike> items) {
        return new IngredientWithCount(Ingredient.of(items), count);
    }

    public static IngredientWithCount of(int count, TagKey<Item> tag) {
        return new IngredientWithCount(Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(tag)), count);
    }

    public static IngredientWithCount fromNetwork(RegistryFriendlyByteBuf buf) {
        return new IngredientWithCount(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readByte());
    }

    public void toNetwork(RegistryFriendlyByteBuf buf) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, this.ingredient);
        buf.writeByte(count);
    }
}
