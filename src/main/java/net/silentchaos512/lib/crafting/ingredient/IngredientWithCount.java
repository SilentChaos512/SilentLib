package net.silentchaos512.lib.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class IngredientWithCount extends Ingredient {
    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(iwa -> iwa),
                    Codec.INT.fieldOf("count").forGetter(iwa -> iwa.count)
            ).apply(instance, IngredientWithCount::new)
    );

    public static final IngredientWithCount EMPTY = new IngredientWithCount(Stream.empty(), 0);

    private final int count;

    private IngredientWithCount(Ingredient ingredient, int count) {
        this(Arrays.stream(ingredient.values), count);
    }

    private IngredientWithCount(Stream<? extends Value> values, int count) {
        super(values);
        this.count = count;
    }

    private IngredientWithCount(Stream<? extends Value> values, Supplier<? extends IngredientType<?>> type, int count) {
        super(values, type);
        this.count = count;
    }

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        return pStack != null && pStack.getCount() >= this.count && super.test(pStack);
    }

    public static IngredientWithCount of() {
        return EMPTY;
    }

    public static IngredientWithCount of(int count, ItemLike... pItems) {
        return of(count, Arrays.stream(pItems).map(ItemStack::new));
    }

    public static IngredientWithCount of(int count, ItemStack... pStacks) {
        return of(count, Arrays.stream(pStacks));
    }

    public static IngredientWithCount of(int count, Stream<ItemStack> pStacks) {
        return fromValues(pStacks.filter((stack) -> {
            return !stack.isEmpty();
        }).map(ItemValue::new), count);
    }

    public static IngredientWithCount of(int count, TagKey<Item> pTag) {
        return fromValues(Stream.of(new TagValue(pTag)), count);
    }

    public static IngredientWithCount fromValues(Stream<? extends Value> pStream, int count) {
        IngredientWithCount ingredient = new IngredientWithCount(pStream, count);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static IngredientWithCount fromNetworkIwc(FriendlyByteBuf buf) {
        return new IngredientWithCount(Ingredient.fromNetwork(buf), buf.readByte());
    }

    public void toNetworkIwc(FriendlyByteBuf buf) {
        super.toNetwork(buf);
        buf.writeByte(count);
    }
}
