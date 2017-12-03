package net.silentchaos512.lib.util;

import net.minecraft.util.math.MathHelper;

public class Color {

  public static final Color BLACK = new Color(0x0);
  public static final Color WHITE = new Color(0xFFFFFF);

  float red;
  float green;
  float blue;
  float alpha;

  public Color(int color) {

    this((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
  }

  public Color(int red, int green, int blue) {

    this(red / 255f, green / 255f, blue / 255f, 1f);
  }

  public Color(int red, int green, int blue, int alpha) {

    this(red / 255f, green / 255f, blue / 255f, alpha / 255f);
  }

  public Color(float red, float green, float blue) {

    this(red, green, blue, 1f);
  }

  public Color(float red, float green, float blue, float alpha) {

    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
  }

  public Color blendWith(Color other) {

    return blend(this, other);
  }

  public static Color blend(Color color1, Color color2) {

    return blend(color1, color2, 0.5f);
  }

  public static Color blend(Color color1, Color color2, float ratio) {

    int i1 = color1.getColor();
    int i2 = color2.getColor();

    int color = blend(i1, i2, ratio);
    return new Color(color);
  }

  public static int blend(int color1, int color2) {

    return blend(color1, color2, 0.5f);
  }

  public static int blend(int color1, int color2, float ratio) {

    ratio = MathHelper.clamp(ratio, 0f, 1f);
    float iRatio = 1f - ratio;

    int a1 = (color1 >> 24 & 0xff);
    int r1 = ((color1 & 0xff0000) >> 16);
    int g1 = ((color1 & 0xff00) >> 8);
    int b1 = (color1 & 0xff);

    int a2 = (color2 >> 24 & 0xff);
    int r2 = ((color2 & 0xff0000) >> 16);
    int g2 = ((color2 & 0xff00) >> 8);
    int b2 = (color2 & 0xff);

    int a = (int)((a1 * iRatio) + (a2 * ratio));
    int r = (int)((r1 * iRatio) + (r2 * ratio));
    int g = (int)((g1 * iRatio) + (g2 * ratio));
    int b = (int)((b1 * iRatio) + (b2 * ratio));

    int color = a << 24 | r << 16 | g << 8 | b;
    return color;
  }

  public int getColor() {

    int r = (int) (red * 255f) << 16;
    int g = (int) (green * 255f) << 8;
    int b = (int) (blue * 255f);
    return r + g + b;
  }

  public float getRed() {

    return red;
  }

  public float getGreen() {

    return green;
  }

  public float getBlue() {

    return blue;
  }

  public float getAlpha() {

    return alpha;
  }

  public int getRedInt() {

    return (int) (red * 255f);
  }

  public int getGreenInt() {

    return (int) (green * 255f);
  }

  public int getBlueInt() {

    return (int) (blue * 255f);
  }

  public int getAlphaInt() {

    return (int) (alpha * 255f);
  }
}
