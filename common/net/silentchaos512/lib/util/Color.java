package net.silentchaos512.lib.util;

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
