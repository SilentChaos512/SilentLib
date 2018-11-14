/*
 * Silent Lib -- GuiDropDownList
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

package net.silentchaos512.lib.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Drop down list. Needs cleanup, method signatures may change.
 *
 * @since 3.0.8
 */
// FIXME: direction UP stacks the elements upside-down.
public class GuiDropDownList extends GuiButton {
    static final int ELEMENT_WIDTH = 100;
    static final int ELEMENT_HEIGHT = 12;

    private final ExpandDirection expandDirection;
    private final List<GuiDropDownElement> children = new ArrayList<>();
    private final List<Consumer<GuiDropDownElement>> listeners = new ArrayList<>();
    private boolean expanded = false;

    public GuiDropDownList(int buttonId, int x, int y, String buttonText, ExpandDirection direction) {
        super(buttonId, x, y, ELEMENT_WIDTH, 20, buttonText); // add overload
        this.expandDirection = direction;
    }

    public void addElement(GuiDropDownElement element, @Nullable Collection<GuiButton> buttonList) {
        element.parent = this;
        element.visible = expanded;
        element.x = this.x + expandDirection.offsetX(this);
        element.y = this.y + expandDirection.offsetY(this);
        if (expandDirection == ExpandDirection.UP)
            children.add(0, element);
        else
            children.add(element);
        if (buttonList != null) buttonList.add(element);
    }

    public void addListener(Consumer<GuiDropDownElement> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        setExpanded(!expanded);
    }

    void onElementSelected(GuiDropDownElement child) {
        listeners.forEach(listener -> listener.accept(child));
        setExpanded(false);
    }

    private void setExpanded(boolean value) {
        expanded = value;

        for (GuiDropDownElement b : children) {
            b.visible = expanded;
            // Should positions be adjusted?
        }

        this.enabled = !expanded;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);

        if (expanded) {
            for (GuiDropDownElement child : children) {
                child.drawButton(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    public enum ExpandDirection {
        UP(0, -1), DOWN(0, 1);

        private final int scaleX;
        private final int scaleY;

        ExpandDirection(int x, int y) {
            this.scaleX = x;
            this.scaleY = y;
        }

        private int offsetX(GuiDropDownList list) {
            return 0;
        }

        private int offsetY(GuiDropDownList list) {
            int offset = this == DOWN ? 7 : 0;
            return scaleY * ELEMENT_HEIGHT * (list.children.size() + 1) + offset;
        }
    }
}
