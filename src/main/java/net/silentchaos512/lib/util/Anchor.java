package net.silentchaos512.lib.util;

public enum Anchor {
    TOP_LEFT(Horizontal.LEFT, Vertical.TOP),
    TOP_CENTER(Horizontal.CENTER, Vertical.TOP),
    TOP_RIGHT(Horizontal.RIGHT, Vertical.TOP),
    CENTER_LEFT(Horizontal.LEFT, Vertical.CENTER),
    CENTER(Horizontal.CENTER, Vertical.CENTER),
    CENTER_RIGHT(Horizontal.RIGHT, Vertical.CENTER),
    BOTTOM_LEFT(Horizontal.LEFT, Vertical.BOTTOM),
    BOTTOM_CENTER(Horizontal.CENTER, Vertical.BOTTOM),
    BOTTOM_RIGHT(Horizontal.RIGHT, Vertical.BOTTOM);

    public enum Horizontal {
        LEFT {
            @Override
            public int getX(int scaledScreenWidth, int elementWidth, int margin) {
                return margin;
            }
        },
        CENTER {
            @Override
            public int getX(int scaledScreenWidth, int elementWidth, int margin) {
                return (scaledScreenWidth - elementWidth) / 2;
            }
        },
        RIGHT {
            @Override
            public int getX(int scaledScreenWidth, int elementWidth, int margin) {
                return scaledScreenWidth - elementWidth - margin;
            }
        };

        public abstract int getX(int scaledScreenWidth, int elementWidth, int margin);
    }

    public enum Vertical {
        TOP {
            @Override
            public int getY(int scaledScreenHeight, int elementHeight, int margin) {
                return margin;
            }
        },
        CENTER {
            @Override
            public int getY(int scaledScreenHeight, int elementHeight, int margin) {
                return (scaledScreenHeight - elementHeight) / 2;
            }
        },
        BOTTOM {
            @Override
            public int getY(int scaledScreenHeight, int elementHeight, int margin) {
                return scaledScreenHeight - elementHeight - margin;
            }
        };

        public abstract int getY(int scaledScreenHeight, int elementHeight, int margin);
    }

    private final Horizontal horizontal;
    private final Vertical vertical;

    Anchor(Horizontal horizontal, Vertical vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int getX(int scaledScreenWidth, int elementWidth, int margin) {
        return this.horizontal.getX(scaledScreenWidth, elementWidth, margin);
    }

    public int getY(int scaledScreenHeight, int elementHeight, int margin) {
        return this.vertical.getY(scaledScreenHeight, elementHeight, margin);
    }

    public Horizontal getHorizontal() {
        return horizontal;
    }

    public Vertical getVertical() {
        return vertical;
    }
}
