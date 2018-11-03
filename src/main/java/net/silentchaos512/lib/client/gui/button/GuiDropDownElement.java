/*
 * Silent Lib -- GuiDropDownElement
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
import net.silentchaos512.lib.SilentLib;

import java.util.function.Consumer;

/**
 * Drop down list element. Still needs some cleanup. Also see {@link GuiDropDownList}.
 *
 * @since 3.0.8
 */
public class GuiDropDownElement extends GuiButton {
    GuiDropDownList parent;
    private final Consumer<GuiDropDownElement> action;

    public GuiDropDownElement(int buttonId, String buttonText, Consumer<GuiDropDownElement> action) {
        super(buttonId, 0, 0, GuiDropDownList.ELEMENT_WIDTH, GuiDropDownList.ELEMENT_HEIGHT, buttonText);
        this.action = action;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        SilentLib.logHelper.debug("GuiDropDownElement#mousePressed (parent {}) visible={}", parent, visible);
        return this.visible && super.mousePressed(mc, mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        SilentLib.logHelper.debug("GuiDropDownElement#mouseReleased (parent {})", parent);
        if (parent != null) {
            parent.onElementSelected(this);
            action.accept(this);
        }
    }
}
