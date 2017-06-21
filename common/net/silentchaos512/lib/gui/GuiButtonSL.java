package net.silentchaos512.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonSL extends GuiButton {

  public GuiButtonSL(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {

    super(buttonId, x, y, widthIn, heightIn, buttonText);
  }

  public GuiButtonSL(int buttonId, int x, int y, String buttonText) {

    super(buttonId, x, y, buttonText);
  }

  // 1.10.2 and 1.11.2
  public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {

    drawButton(minecraft, mouseX, mouseY, 0f);
  }
}
