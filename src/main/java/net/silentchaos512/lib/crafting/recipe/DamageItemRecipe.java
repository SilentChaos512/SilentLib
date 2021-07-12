package net.silentchaos512.lib.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.silentchaos512.lib.SilentLib;

public class DamageItemRecipe extends ExtendedShapelessRecipe {
    public static final ExtendedShapelessRecipe.Serializer<DamageItemRecipe> SERIALIZER = new ExtendedShapelessRecipe.Serializer<>(
            DamageItemRecipe::new,
            (json, recipe) -> recipe.damageToItems = JSONUtils.getAsInt(json, "damage", 1),
            (buffer, recipe) -> recipe.damageToItems = buffer.readVarInt(),
            (buffer, recipe) -> buffer.writeVarInt(recipe.damageToItems)
    );

    private int damageToItems = 1;

    public DamageItemRecipe(ShapelessRecipe recipe) {
        super(recipe);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return getBaseRecipe().matches(inv, worldIn);
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        return getBaseRecipe().assemble(inv);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.hasContainerItem()) {
                list.set(i, stack.getContainerItem());
            } else if (stack.getMaxDamage() > 0) {
                ItemStack tool = stack.copy();
                if (tool.hurt(this.damageToItems, SilentLib.RANDOM, null)) {
                    tool.shrink(1);
                }
                list.set(i, tool);
            }
        }

        return list;
    }
}
