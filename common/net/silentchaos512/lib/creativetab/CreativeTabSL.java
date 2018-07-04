package net.silentchaos512.lib.creativetab;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

/**
 * CreativeTabs with cross compatibility support.
 *
 * @author SilentChaos512
 * @since 2.0.0
 */
public class CreativeTabSL extends CreativeTabs {

    private final Supplier<ItemStack> iconSupplier;

    @Deprecated
    public CreativeTabSL(String label) {
        this(label, () -> ItemStack.EMPTY);
    }

    /**
     * Create the creative tab with a block for the icon. Note that meta is ignored in 1.10.
     *
     * @since 2.0.6
     */
    @Deprecated
    public CreativeTabSL(String label, Block block, int meta) {
        this(label, () -> new ItemStack(block, 1, meta));
    }

    /**
     * Create the creative tab with an item for the icon. Note that meta is ignored in 1.10.
     *
     * @since 2.0.6
     */
    @Deprecated
    public CreativeTabSL(String label, Item item, int meta) {
        this(label, () -> new ItemStack(item, 1, meta));
    }

    /**
     * Supplier-based constructor. Can be called at any time, as opposed to the others which only work if the block/item
     * has already been initialized.
     * @param label The tab label (mod ID in many cases)
     * @param iconSupplier A supplier that produces the ItemStack used for the tab icon
     */
    public CreativeTabSL(String label, Supplier<ItemStack> iconSupplier) {
        super(label);
        this.iconSupplier = iconSupplier;
    }

    @Deprecated
    protected ItemStack getStack() {
        return iconSupplier.get();
    }

    @Override
    public ItemStack getTabIconItem() {
        return getStack();
    }
}
