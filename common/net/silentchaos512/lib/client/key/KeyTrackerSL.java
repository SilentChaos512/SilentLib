package net.silentchaos512.lib.client.key;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

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
