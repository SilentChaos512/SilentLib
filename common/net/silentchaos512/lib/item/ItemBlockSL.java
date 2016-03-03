package net.silentchaos512.lib.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.block.BlockSL;
import net.silentchaos512.lib.registry.IRegistryObject;

public class ItemBlockSL extends ItemBlock {

  protected Block block;
  protected String unlocalizedName;

  public ItemBlockSL(Block block) {

    super(block);
    this.block = block;
    setMaxDamage(0);

    if (block instanceof BlockSL) {
      BlockSL blockSL = (BlockSL) block;
      setHasSubtypes(blockSL.getHasSubtypes());
    }
    if (block instanceof IRegistryObject) {
      unlocalizedName = "tile." + ((IRegistryObject) block).getFullName();
    }
  }

  @Override
  public int getMetadata(int meta) {

    return meta;
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {

    // TODO
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return unlocalizedName + (hasSubtypes ? stack.getItemDamage() : "");
  }
}
