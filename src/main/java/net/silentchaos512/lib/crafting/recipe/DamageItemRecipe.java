package net.silentchaos512.lib.crafting.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.silentchaos512.lib.SilentLib;

/**
 * NOTE: This recipe type has some issues. Sometimes items are duplicated in certain crafting
 * interfaces from other mods. Consider using a custom crafting block instead.
 */
public class DamageItemRecipe extends ExtendedShapelessRecipe {
    public static final ExtendedShapelessRecipe.Serializer<DamageItemRecipe> SERIALIZER = new ExtendedShapelessRecipe.Serializer<>(
            DamageItemRecipe::new,
            (json, recipe) -> recipe.damageToItems = GsonHelper.getAsInt(json, "damage", 1),
            (buffer, recipe) -> recipe.damageToItems = buffer.readVarInt(),
            (buffer, recipe) -> buffer.writeVarInt(recipe.damageToItems)
    );

    private int damageToItems = 1;

    public DamageItemRecipe(ShapelessRecipe recipe) {
        super(recipe);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return getBaseRecipe().matches(inv, worldIn);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        return getBaseRecipe().assemble(inv);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.hasCraftingRemainingItem()) {
                list.set(i, stack.getCraftingRemainingItem());
            } else if (stack.getMaxDamage() > 0) {
                ItemStack tool = stack.copy();
                if (tool.hurt(this.damageToItems, SilentLib.RANDOM_SOURCE, null)) {
                    tool.shrink(1);
                }
                list.set(i, tool);
            }
        }

        return list;
    }
}
