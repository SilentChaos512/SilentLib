package net.silentchaos512.lib.client.gui;

public enum HudAnchor {
    TOP_LEFT {
        @Override
        public int getX(int scaledWidth, int padding) {
            return padding;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return padding;
        }
    },
    TOP_CENTER {
        @Override
        public int getX(int scaledWidth, int padding) {
            return scaledWidth / 2;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return padding;
        }
    },
    TOP_RIGHT {
        @Override
        public int getX(int scaledWidth, int padding) {
            return scaledWidth - padding;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return padding;
        }
    },
    CENTER_LEFT {
        @Override
        public int getX(int scaledWidth, int padding) {
            return padding;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return scaledHeight / 2;
        }
    },
    CENTER {
        @Override
        public int getX(int scaledWidth, int padding) {
            return scaledWidth / 2;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return scaledHeight / 2;
        }
    },
    CENTER_RIGHT {
        @Override
        public int getX(int scaledWidth, int padding) {
            return scaledWidth - padding;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return scaledHeight / 2;
        }
    },
    BOTTOM_LEFT {
        @Override
        public int getX(int scaledWidth, int padding) {
            return padding;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return scaledHeight - padding;
        }
    },
    BOTTOM_CENTER {
        @Override
        public int getX(int scaledWidth, int padding) {
            return scaledWidth / 2;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return scaledHeight - padding;
        }
    },
    BOTTOM_RIGHT {
        @Override
        public int getX(int scaledWidth, int padding) {
            return scaledWidth - padding;
        }

        @Override
        public int getY(int scaledHeight, int padding) {
            return scaledHeight - padding;
        }
    };

    public abstract int getX(int scaledWidth, int padding);

    public abstract int getY(int scaledHeight, int padding);

    public int getX(int scaledWidth) {
        return this.getX(scaledWidth, 0);
    }

    public int getY(int scaledHeight) {
        return this.getY(scaledHeight, 0);
    }

    public int offsetX(int scaledWidth, int amount) {
        return this.getX(scaledWidth, 0) + amount;
    }

    public int offsetY(int scaledHeight, int amount) {
        return this.getY(scaledHeight, 0) + amount;
    }
}
