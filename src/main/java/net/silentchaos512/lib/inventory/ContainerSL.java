package net.silentchaos512.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

@Deprecated
public class ContainerSL extends Container {
    protected final IInventory tileInventory;

    public ContainerSL(ContainerType<?> type, PlayerInventory playerInventory, IInventory tileInventory) {
        super(type, 0);
        this.tileInventory = tileInventory;
        addTileInventorySlots(tileInventory);
        addPlayerInventorySlots(playerInventory);
    }

    protected void addTileInventorySlots(IInventory inv) {
    }

    protected void addPlayerInventorySlots(PlayerInventory inv) {
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
    public boolean canInteractWith(PlayerEntity player) {
        return tileInventory.isUsableByPlayer(player);
    }

    @Deprecated
    public static void onTakeFromSlot(Slot slot, PlayerEntity player, ItemStack stack) {
        slot.onTake(player, stack);
    }
}
