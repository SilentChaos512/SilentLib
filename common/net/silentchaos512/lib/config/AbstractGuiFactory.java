/*
 * SilentLib - AbstractGuiFactory
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
