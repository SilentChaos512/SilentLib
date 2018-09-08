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

import net.minecraft.client.gui.ScaledResolution;

public enum HudAnchor {
    TOP_LEFT {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return padding;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return padding;
        }
    },
    TOP_CENTER {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return resolution.getScaledWidth() / 2;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return padding;
        }
    },
    TOP_RIGHT {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return resolution.getScaledWidth() - padding;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return padding;
        }
    },
    CENTER_LEFT {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return padding;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return resolution.getScaledHeight() / 2;
        }
    },
    CENTER {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return resolution.getScaledWidth() / 2;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return resolution.getScaledHeight() / 2;
        }
    },
    CENTER_RIGHT {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return resolution.getScaledWidth() - padding;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return resolution.getScaledHeight() / 2;
        }
    },
    BOTTOM_LEFT {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return padding;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return resolution.getScaledHeight() - padding;
        }
    },
    BOTTOM_CENTER {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return resolution.getScaledWidth() / 2;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return resolution.getScaledHeight() - padding;
        }
    },
    BOTTOM_RIGHT {
        @Override
        public int getX(ScaledResolution resolution, int padding) {
            return resolution.getScaledWidth() - padding;
        }

        @Override
        public int getY(ScaledResolution resolution, int padding) {
            return resolution.getScaledHeight() - padding;
        }
    };

    public abstract int getX(ScaledResolution resolution, int padding);

    public abstract int getY(ScaledResolution resolution, int padding);

    public int getX(ScaledResolution resolution) {
        return this.getX(resolution, 0);
    }

    public int getY(ScaledResolution resolution) {
        return this.getY(resolution, 0);
    }

    public int offsetX(ScaledResolution resolution, int amount) {
        return this.getX(resolution, 0) + amount;
    }

    public int offsetY(ScaledResolution resolution, int amount) {
        return this.getY(resolution, 0) + amount;
    }
}
