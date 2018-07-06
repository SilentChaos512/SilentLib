/*
 * SilentLib - KeyTrackerSL
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

package net.silentchaos512.lib.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Base class for a key tracker. Contains some helper methods for creating keybindings and checking for modifier key
 * presses.
 *
 * Recommended use is to create a static instance and register that on the MinecraftForge.EVENT_BUS during pre-init.
 *
 * @author SilentChaos512
 *
 */
public abstract class KeyTrackerSL {

  final String modName;

  public KeyTrackerSL(String modName) {

    this.modName = modName;
  }

  @SubscribeEvent
  public abstract void onKeyInput(KeyInputEvent event);

  /**
   * Creates a new keybinding and registers it in the ClientRegistry
   *
   * @return The newly-created keybinding.
   */
  protected KeyBinding createBinding(String name, IKeyConflictContext keyConflictContext,
      KeyModifier keyModifier, int keyCode) {

    KeyBinding binding = new KeyBinding(name, keyConflictContext, keyModifier, keyCode, modName);
    ClientRegistry.registerKeyBinding(binding);
    return binding;
  }

  /**
   * @return True if either the left or right Shift key is held down.
   */
  public static boolean isShiftDown() {

    return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
  }

  /**
   * @return True if either the left or right Control (Ctrl) key is held down.
   */
  public static boolean isControlDown() {

    return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
  }

  /**
   * @return True if either the left or right Alt key is held down.
   */
  public static boolean isAltDown() {

    return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
  }
}
