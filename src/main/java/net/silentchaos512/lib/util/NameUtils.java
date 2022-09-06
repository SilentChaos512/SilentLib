package net.silentchaos512.lib.util;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.silentchaos512.lib.block.IBlockProvider;

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
    public static ResourceLocation checkNotNull(@Nullable ResourceLocation name) {
        Preconditions.checkNotNull(name, "Name is null, make sure the object has been registered correctly");
        return name;
    }


    /**
     * Get registry name for a specific registered object
     * @param registry - See {@link ForgeRegistries} for full list of all forge built-in registries
     */
    public static <T> ResourceLocation getNameFromRegistry(IForgeRegistry<T> registry, T value) {
        ResourceLocation key = registry.getKey(value);
        return checkNotNull(key);
    }

    /**
     * Get a ResourceLocation with namespace "forge". Does not handle exceptions.
     *
     * @param path The path (must be /[a-z0-9/._-]+/)
     * @return A new ResourceLocation
     * @throws net.minecraft.ResourceLocationException if path is invalid
     */
    public static ResourceLocation forgeId(String path) {
        return new ResourceLocation("forge", path);
    }

    /**
     * Gets the block's registry name, throwing an exception if it is null
     *
     * @param block The block
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromBlock(Block block) {
        return getNameFromRegistry(ForgeRegistries.BLOCKS, block);
    }

    /**
     * Gets the block's registry name, throwing an exception if it is null
     *
     * @param block The block
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromBlock(IBlockProvider block) {
        Preconditions.checkNotNull(block.asBlock(), "asBlock() is null, has object not been fully constructed?");
        return fromBlock(block.asBlock());
    }

    /**
     * Gets the block's registry name, throwing an exception if it is null
     *
     * @param state The block
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromBlock(BlockState state) {
        return fromBlock(state.getBlock());
    }

    /**
     * Gets the enchantment's registry name, throwing an exception if it is null
     *
     * @param enchantment The enchantment
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromEnchantment(Enchantment enchantment) {
        return getNameFromRegistry(ForgeRegistries.ENCHANTMENTS, enchantment);
    }

    /**
     * Gets the entity type's registry name, throwing an exception if it is null
     *
     * @param entity The entity
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromEntity(Entity entity) {
        return fromEntityType(entity.getType());
    }

    /**
     * Gets the entity type's registry name, throwing an exception if it is null
     *
     * @param type The entity type
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromEntityType(EntityType<?> type) {
        return getNameFromRegistry(ForgeRegistries.ENTITY_TYPES, type);
    }

    /**
     * Gets the fluid's registry name, throwing an exception if it is null
     *
     * @param fluid The fluid
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromFluid(Fluid fluid) {
        return getNameFromRegistry(ForgeRegistries.FLUIDS, fluid);
    }

    /**
     * Gets the fluid's registry name, throwing an exception if it is null
     *
     * @param fluid The fluid
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromFluid(FluidStack fluid) {
        return fromFluid(fluid.getFluid());
    }

    /**
     * Gets the item's registry name, throwing an exception if it is null
     *
     * @param item The item
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromItem(ItemLike item) {
        Preconditions.checkNotNull(item.asItem(), "asItem() is null, has object not been fully constructed?");
        return getNameFromRegistry(ForgeRegistries.ITEMS, item.asItem());
    }

    /**
     * Gets the registry name of the stack's item, throwing an exception if it is null
     *
     * @param stack The ItemStack
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromItem(ItemStack stack) {
        return fromItem(stack.getItem());
    }

    /**
     * Gets the recipe serializer's registry name, throwing an exception if it is null
     *
     * @param serializer The recipe serializer
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromRecipeSerializer(RecipeSerializer<? extends Recipe<?>> serializer) {
        return getNameFromRegistry(ForgeRegistries.RECIPE_SERIALIZERS, serializer);
    }
}
