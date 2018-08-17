/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.page;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;

import java.util.Arrays;
import java.util.List;

public class PageCrafting extends GuidePage {
    private final List<IRecipe> recipes;
    private int recipeAt;
    private String recipeTypeLocKey;
    private boolean isWildcard;

    public PageCrafting(GuideBook book, int localizationKey, int priority, List<IRecipe> recipes) {
        super(book, localizationKey, priority);
        this.recipes = recipes;
    }

    public PageCrafting(GuideBook book, int localizationKey, List<IRecipe> recipes) {
        this(book, localizationKey, 0, recipes);
    }

    public PageCrafting(GuideBook book, int localizationKey, IRecipe... recipes) {
        this(book, localizationKey, 0, recipes);
    }

    public PageCrafting(GuideBook book, int localizationKey, int priority, IRecipe... recipes) {
        this(book, localizationKey, priority, Arrays.asList(recipes));
    }

    public GuidePage setWildcard() {
        this.isWildcard = true;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreenPre(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks) {
        super.drawScreenPre(gui, startX, startY, mouseX, mouseY, partialTicks);

        gui.mc.getTextureManager().bindTexture(book.getResourceGadgets());
        GuiUtils.drawTexturedModalRect(startX + 5, startY + 6, 20, 0, 116, 54, 0);

        gui.renderScaledAsciiString("(" + book.i18n.translate(this.recipeTypeLocKey) + ")",
                startX + 6, startY + 65, 0, false, gui.getMediumFontSize());

        PageTextOnly.renderTextToPage(gui, this, startX + 6, startY + 80);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen(GuiGuideBase gui, int startX, int startY, int pageTimer) {
        super.updateScreen(gui, startX, startY, pageTimer);

        if (pageTimer % 20 == 0) {
            this.findRecipe(gui, startX, startY);
        }
    }

    private void findRecipe(GuiGuideBase gui, int startX, int startY) {
        if (!this.recipes.isEmpty()) {
            IRecipe recipe = this.recipes.get(this.recipeAt);
            if (recipe != null) {
                this.setupRecipe(gui, recipe, startX, startY);
            }

            this.recipeAt++;
            if (this.recipeAt >= this.recipes.size()) {
                this.recipeAt = 0;
            }
        }
    }

    @Override
    public void initGui(GuiGuideBase gui, int startX, int startY) {
        super.initGui(gui, startX, startY);
        this.findRecipe(gui, startX, startY);
    }

    @Override
    public void getItemStacksForPage(List<ItemStack> list) {
        super.getItemStacksForPage(list);

        if (!this.recipes.isEmpty()) {
            for (IRecipe recipe : this.recipes) {
                if (recipe != null) {
                    ItemStack output = recipe.getRecipeOutput();
                    if (!output.isEmpty()) {
                        ItemStack copy = output.copy();
                        if (this.isWildcard) {
                            copy.setItemDamage(OreDictionary.WILDCARD_VALUE);
                        }
                        list.add(copy);
                    }
                }
            }
        }
    }

    private void setupRecipe(GuiGuideBase gui, IRecipe recipe, int startX, int startY) {
        ItemStack[] stacks = new ItemStack[9];
        int width = 3;
        int height = 3;

        if (recipe instanceof ShapedRecipes) {
            ShapedRecipes shaped = (ShapedRecipes) recipe;
            width = shaped.recipeWidth;
            height = shaped.recipeHeight;
            for (int i = 0; i < shaped.recipeItems.size(); ++i) {
                Ingredient ing = shaped.recipeItems.get(i);
                stacks[i] = ing.getMatchingStacks().length > 0 ? ing.getMatchingStacks()[0] : ItemStack.EMPTY;
            }
            this.recipeTypeLocKey = "guide.silentlib:shapedRecipe";
        } else if (recipe instanceof ShapelessRecipes) {
            ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
            for (int i = 0; i < shapeless.recipeItems.size(); i++) {
                Ingredient ing = shapeless.recipeItems.get(i);
                stacks[i] = ing.getMatchingStacks().length > 0 ? ing.getMatchingStacks()[0] : ItemStack.EMPTY;
            }
            this.recipeTypeLocKey = "guide.silentlib:shapelessRecipe";
        } else if (recipe instanceof ShapedOreRecipe) {
            ShapedOreRecipe shaped = (ShapedOreRecipe) recipe;
            try {
                width = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 4);
                height = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 5);
            } catch (Exception e) {
                SilentLib.logHelper.warn("Something went wrong trying to get the Crafting Recipe in the booklet to display!");
                e.printStackTrace();
            }
            for (int i = 0; i < shaped.getIngredients().size(); i++) {
                Ingredient input = shaped.getIngredients().get(i);
                stacks[i] = input.getMatchingStacks().length > 0 ? input.getMatchingStacks()[0] : ItemStack.EMPTY;
            }
            this.recipeTypeLocKey = "guide.silentlib:shapedOreRecipe";
        } else if (recipe instanceof ShapelessOreRecipe) {
            ShapelessOreRecipe shapeless = (ShapelessOreRecipe) recipe;
            for (int i = 0; i < shapeless.getIngredients().size(); i++) {
                Ingredient input = shapeless.getIngredients().get(i);
                stacks[i] = input.getMatchingStacks().length > 0 ? input.getMatchingStacks()[0] : ItemStack.EMPTY;
            }
            this.recipeTypeLocKey = "guide.silentlib:shapelessOreRecipe";
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                ItemStack stack = stacks[y * width + x];
                if (!stack.isEmpty()) {
                    ItemStack copy = stack.copy();
                    copy.setCount(1);
                    if (copy.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                        copy.setItemDamage(0);
                    }

                    gui.addOrModifyItemRenderer(copy, startX + 6 + x * 18, startY + 7 + y * 18, 1F, true);
                }
            }
        }

        gui.addOrModifyItemRenderer(recipe.getRecipeOutput(), startX + 100, startY + 25, 1F, false);
    }
}
