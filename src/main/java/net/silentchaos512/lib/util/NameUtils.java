package net.silentchaos512.lib.util;

import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public final class NameUtils {
    private static final Pattern PATTERN = Pattern.compile("([a-z0-9._-]+:)?[a-z0-9/._-]+");

    private NameUtils() {throw new IllegalAccessError("Utility class");}

    public static boolean isValid(CharSequence name) {
        return PATTERN.matcher(name).matches();
    }

    /**
     * Verify name is not null, throwing an exception if it is.
     *
     * @param name Possibly null ResourceLocation
     * @return name
     * @throws NullPointerException if name is null
     */
    public static Identifier checkNotNull(@Nullable Identifier name) {
        Preconditions.checkNotNull(name, "Name is null, make sure the object has been registered correctly");
        return name;
    }

    /**
     * Get an Identifier with namespace "c". Does not handle exceptions.
     *
     * @param path The path (must be /[a-z0-9/._-]+/)
     * @return A new ResourceLocation
     * @throws net.minecraft.IdentifierException if path is invalid
     */
    public static Identifier common(String path) {
        return Identifier.fromNamespaceAndPath("c", path);
    }

    public static <T> Identifier from(Registry<T> registry, T obj) {
        return checkNotNull(registry.getKey(obj));
    }

    /**
     * Gets the block's registry name, throwing an exception if it is null
     *
     * @param block The block
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromBlock(Block block) {
        return checkNotNull(BuiltInRegistries.BLOCK.getKey(block));
    }

    /**
     * Gets the block's registry name, throwing an exception if it is null
     *
     * @param state The block
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromBlock(BlockState state) {
        return fromBlock(state.getBlock());
    }

    /**
     * Gets the entity type's registry name, throwing an exception if it is null
     *
     * @param entity The entity
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromEntity(Entity entity) {
        return fromEntityType(entity.getType());
    }

    /**
     * Gets the entity type's registry name, throwing an exception if it is null
     *
     * @param type The entity type
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromEntityType(EntityType<?> type) {
        return checkNotNull(BuiltInRegistries.ENTITY_TYPE.getKey(type));
    }

    /**
     * Gets the fluid's registry name, throwing an exception if it is null
     *
     * @param fluid The fluid
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromFluid(Fluid fluid) {
        return checkNotNull(BuiltInRegistries.FLUID.getKey(fluid));
    }

    /**
     * Gets the fluid's registry name, throwing an exception if it is null
     *
     * @param fluid The fluid
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromFluid(FluidStack fluid) {
        return fromFluid(fluid.getFluid());
    }

    /**
     * Gets the item's registry name, throwing an exception if it is null
     *
     * @param item The item
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromItem(ItemLike item) {
        Preconditions.checkNotNull(item.asItem(), "asItem() is null, has object not been fully constructed?");
        return checkNotNull(BuiltInRegistries.ITEM.getKey(item.asItem()));
    }

    /**
     * Gets the registry name of the stack's item, throwing an exception if it is null
     *
     * @param itemInstance The ItemStack or ItemStackTemplate
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromItem(ItemInstance itemInstance) {
        return fromItem(itemInstance.typeHolder().value());
    }

    /**
     * Gets the recipe serializer's registry name, throwing an exception if it is null
     *
     * @param serializer The recipe serializer
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static Identifier fromRecipeSerializer(RecipeSerializer<? extends Recipe<?>> serializer) {
        return checkNotNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer));
    }
}
