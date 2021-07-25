package net.silentchaos512.lib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Deprecated
public class ContainerSL extends AbstractContainerMenu {
    protected final Container tileInventory;

    public ContainerSL(MenuType<?> type, Inventory playerInventory, Container tileInventory) {
        super(type, 0);
        this.tileInventory = tileInventory;
        addTileInventorySlots(tileInventory);
        addPlayerInventorySlots(playerInventory);
    }

    protected void addTileInventorySlots(Container inv) {
    }

    protected void addPlayerInventorySlots(Inventory inv) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return tileInventory.stillValid(player);
    }

    @Deprecated
    public static void onTakeFromSlot(Slot slot, Player player, ItemStack stack) {
        slot.onTake(player, stack);
    }
}
