package net.silentchaos512.lib.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * General purpose config GUI factory, suitable for most purposes.
 * 
 * @author SilentChaos512
 * @since 2.3.2
 */
public abstract class AbstractGuiFactory implements IModGuiFactory {

  /**
   * Gets the config instance for the mod
   */
  public abstract ConfigBase getConfig();

  /**
   * Gets the mod ID
   */
  public abstract String getModId();

  /**
   * Gets a title for the GUI, such as "[Mod Name] Config"
   */
  public abstract String getGuiTitle();

  @Override
  public boolean hasConfigGui() {

    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {

    return new BasicGuiConfig(parentScreen, this);
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return null;
  }

  public static class BasicGuiConfig extends GuiConfig {

    public BasicGuiConfig(GuiScreen parentScreen, AbstractGuiFactory factory) {

      super(parentScreen, getAllElements(factory), factory.getModId(), false, false, factory.getGuiTitle());
    }

    public static List<IConfigElement> getAllElements(AbstractGuiFactory factory) {

      ConfigBase config = factory.getConfig();
      List<IConfigElement> list = new ArrayList<>();
      Set<String> categories = config.getConfiguration().getCategoryNames();

      for (String cat : categories) {
        if (!cat.contains(".")) {
          List<IConfigElement> elements = new ConfigElement(config.getCategory(cat)).getChildElements();
          String langKey = "config." + factory.getModId() + ":" + cat;
          list.add(new DummyConfigElement.DummyCategoryElement(cat, langKey, elements));
        }
      }

      return list;
    }
  }
}
