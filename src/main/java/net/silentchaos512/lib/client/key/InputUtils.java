package net.silentchaos512.lib.client.key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

public final class InputUtils {
    private InputUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static boolean isShiftDown() {
        long h = Minecraft.getInstance().func_228018_at_().getHandle(); // func_228018_at_ = getMainWindow
        return InputMappings.isKeyDown(h, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isControlDown() {
        long h = Minecraft.getInstance().func_228018_at_().getHandle();
        return InputMappings.isKeyDown(h, GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isAltDown() {
        long h = Minecraft.getInstance().func_228018_at_().getHandle();
        return InputMappings.isKeyDown(h, GLFW.GLFW_KEY_LEFT_ALT) || InputMappings.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_ALT);
    }
}
