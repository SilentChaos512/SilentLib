package net.silentchaos512.lib.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.block.BlockSL;
import net.silentchaos512.lib.registry.IHasSubtypes;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.util.LocalizationHelper;

public class ItemBlockSL extends ItemBlock {

  protected String blockName = "null";
  protected String unlocalizedName = "null";
  protected String modId = "null";

  public ItemBlockSL(Block block) {

    super(block);
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

    return meta & 0xF;
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {

    if (block instanceof BlockSL) {
      return ((BlockSL) block).getRarity(stack.getItemDamage());
    }
    return super.getRarity(stack);
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List<String> list,
      boolean advanced) {

    // Get tooltip from block? (New method)
    int length = list.size();
    block.addInformation(stack, player, list, advanced);

    // If block doesn't add anything, use the old method.
    if (length == list.size()) {
      LocalizationHelper loc = SilentLib.instance.getLocalizationHelperForMod(modId);
      if (loc != null) {
        String name = getNameForStack(stack);
        list.addAll(loc.getBlockDescriptionLines(name));
      }
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {

    return unlocalizedName + (hasSubtypes ? stack.getItemDamage() & 0xF : "");
  }

  public String getNameForStack(ItemStack stack) {

    return blockName + (hasSubtypes ? stack.getItemDamage() & 0xF : "");
  }
}
