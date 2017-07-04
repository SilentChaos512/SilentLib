/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.gui.GuiButtonSL;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.util.AssetUtil;
import net.silentchaos512.lib.util.StackHelper;
import net.silentchaos512.lib.util.StringUtil;

@SideOnly(Side.CLIENT)
public class EntryButton extends GuiButtonSL {

  private final GuiGuideBase gui;
  private final ItemStack stackToRender;

  public EntryButton(GuiGuideBase gui, int id, int x, int y, int width, int height, String text,
      ItemStack stackToRender) {
    super(id, x, y, width, height, text);
    this.gui = gui;
    this.stackToRender = stackToRender;
  }

  @Override
  public void clDrawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {

    if (this.visible) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
          && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.blendFunc(770, 771);
      this.mouseDragged(minecraft, mouseX, mouseY);

      int textOffsetX = 0;
      if (StackHelper.isValid(this.stackToRender)) {
        GlStateManager.pushMatrix();
        AssetUtil.renderStackToGui(this.stackToRender, this.xPosition - 4, this.yPosition, 0.725F);
        GlStateManager.popMatrix();
        textOffsetX = 10;
      }

      float scale = this.gui.getMediumFontSize();

      if (this.hovered) {
        GlStateManager.pushMatrix();
        AssetUtil.drawHorizontalGradientRect(this.xPosition + textOffsetX - 1,
            this.yPosition + this.height - 1,
            this.xPosition
                + (int) (minecraft.fontRendererObj.getStringWidth(this.displayString) * scale)
                + textOffsetX + 1,
            this.yPosition + this.height, 0x80 << 24 | 22271, 22271, this.zLevel);
        GlStateManager.popMatrix();
      }

      StringUtil.renderScaledAsciiString(minecraft.fontRendererObj, this.displayString,
          this.xPosition + textOffsetX, this.yPosition + 2 + (this.height - 8) / 2, 0, false,
          scale);
    }
  }
}
