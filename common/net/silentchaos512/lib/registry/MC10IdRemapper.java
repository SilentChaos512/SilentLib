package net.silentchaos512.lib.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.registry.GameRegistry.Type;

public class MC10IdRemapper {

  public static void remap(MissingMapping mismap) {

    if (mismap.type == Type.BLOCK) {
      Block block = Block.getBlockFromName(mismap.name.toLowerCase());
      mismap.remap(block);
    } else if (mismap.type == Type.ITEM) {
      Item item = Item.getByNameOrId(mismap.name.toLowerCase());
      mismap.remap(item);
    }
  }
}
