package net.silentchaos512.lib.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.gui.GuiGuide;
import net.silentchaos512.lib.guidebook.gui.GuiMainPage;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;
import net.silentchaos512.lib.item.ItemGuideBookSL;

public final class GuiHandlerLibF implements IGuiHandler {

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

    // Using the ID variable to index into the book list... Sneaky!
    ItemGuideBookSL item = ItemGuideBookSL.getBookById(ID);

    if (item != null) {
      if (item.forcedPage != null) {
        GuiGuide gui = GuideBookUtils.createBookletGuiFromPage(item.book, null, item.forcedPage);
        item.forcedPage = null;
        return gui;
      } else {
        if (item.book.lastViewedPage != null)
          return item.book.lastViewedPage;
        return new GuiMainPage(item.book, null);
      }
    }
    return null;
  }
}
