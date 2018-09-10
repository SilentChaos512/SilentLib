/*
 * Silent Lib -- HudAnchor
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
