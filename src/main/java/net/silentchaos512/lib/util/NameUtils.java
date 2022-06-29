package net.silentchaos512.lib.util;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public final class NameUtils {
    private static final Pattern PATTERN = Pattern.compile("([a-z0-9._-]+:)?[a-z0-9/._-]+");

    private NameUtils() { throw new IllegalAccessError("Utility class"); }

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
     * Gets the item's registry name, throwing an exception if it is null
     *
     * @param item The item
     * @return The registry name
     * @throws NullPointerException if registry name is null
     */
    public static ResourceLocation fromItem(ItemLike item) {
        Preconditions.checkNotNull(item.asItem(), "asItem() is null, has object not been fully constructed?");
        return checkNotNull(ForgeRegistries.ITEMS.getKey(item.asItem()));
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

    public static ResourceLocation fromRecipeSerializer(RecipeSerializer<? extends Recipe<?>> serializer) {
        return checkNotNull(ForgeRegistries.RECIPE_SERIALIZERS.getKey(serializer));
    }
}
