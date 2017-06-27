package net.silentchaos512.lib.gui.config;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public abstract class GuiFactorySL implements IModGuiFactory {

  public abstract Class<? extends GuiScreen> mainConfigGuiClass();

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  @Override
  public boolean hasConfigGui() {

    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {

    try {
      return mainConfigGuiClass().getConstructor(GuiScreen.class).newInstance(parentScreen);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return new HashSet<>();
  }

}
