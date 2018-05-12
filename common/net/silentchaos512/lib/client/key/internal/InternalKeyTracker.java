package net.silentchaos512.lib.client.key.internal;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.GameType;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.network.internal.MessageChangeGamemode;
import net.silentchaos512.lib.util.ChatHelper;

/**
 * Registers some dev-only keybindings to change gamemodes.
 * 
 * @author SilentChaos512
 */
public final class InternalKeyTracker extends KeyTrackerSL {

  public static InternalKeyTracker INSTANCE = new InternalKeyTracker();

  private @Nullable KeyBinding keySurvivalMode = null;
  private @Nullable KeyBinding keyCreativeMode = null;
  private @Nullable KeyBinding keySpectatorMode = null;

  public InternalKeyTracker() {

    super(SilentLib.MOD_NAME + " (Dev Only)");

    if (0 == SilentLib.instance.getBuildNumber()) {
      // Dev-only keybindings.
      keySurvivalMode = createBinding("Switch gamemode to survival", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD7);
      keyCreativeMode = createBinding("Switch gamemode to creative", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD8);
      keySpectatorMode = createBinding("Switch gamemode to spectator", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD9);
    }
  }

  @Override
  public void onKeyInput(KeyInputEvent event) {

    Minecraft mc = Minecraft.getMinecraft();
    if (safeCheckPressed(keySurvivalMode)) {
      mc.playerController.setGameType(GameType.SURVIVAL);
      SilentLib.network.wrapper.sendToServer(new MessageChangeGamemode(0));
      ChatHelper.sendStatusMessage(mc.player, "Silent Lib: switched to survival mode", true);
    } else if (safeCheckPressed(keyCreativeMode)) {
      mc.playerController.setGameType(GameType.CREATIVE);
      SilentLib.network.wrapper.sendToServer(new MessageChangeGamemode(1));
      ChatHelper.sendStatusMessage(mc.player, "Silent Lib: switched to creative mode", true);
    } else if (safeCheckPressed(keySpectatorMode)) {
      mc.playerController.setGameType(GameType.SPECTATOR);
      SilentLib.network.wrapper.sendToServer(new MessageChangeGamemode(3));
      ChatHelper.sendStatusMessage(mc.player, "Silent Lib: switched to spectator mode", true);
    }
  }

  private boolean safeCheckPressed(@Nullable KeyBinding binding) {

    if (binding == null)
      return false;
    return binding.isPressed();
  }
}
