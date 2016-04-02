package net.silentchaos512.lib.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.block.BlockSL;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.util.LocalizationHelper;

public class ItemBlockSL extends ItemBlock {

  protected Block block;
  protected String blockName = "null";
  protected String unlocalizedName = "null";
  protected String modId = "null";

  public ItemBlockSL(Block block) {

    super(block);
    this.block = block;
    setMaxDamage(0);

    if (block instanceof IHasSubtypes) {
      setHasSubtypes(((IHasSubtypes) block).hasSubtypes());
    }
    if (block instanceof IRegistryObject) {
      IRegistryObject obj = (IRegistryObject) block;
      blockName = obj.getName();
      unlocalizedName = "tile." + obj.getFullName();
      modId = obj.getModId();
    }
  }

  @Override
  public int getMetadata(int meta) {

    return meta;
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {

    LocalizationHelper loc = SilentLib.instance.getLocalizationHelperForMod(modId);
    if (loc != null) {
      String name = getNameForStack(stack);
      list.addAll(loc.getBlockDescriptionLines(name));
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return unlocalizedName + (hasSubtypes ? stack.getItemDamage() : "");
  }

  public String getNameForStack(ItemStack stack) {

    return blockName + (hasSubtypes ? stack.getItemDamage() : "");
  }
}
