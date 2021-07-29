/*
 * Dungeons Guide - The most intelligent Hypixel Skyblock Dungeons Mod
 * Copyright (C) 2021  cyoung06
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package kr.syeyoung.dungeonsguide.config.guiconfig.nyu;

import kr.syeyoung.dungeonsguide.config.guiconfig.old.MFeature;
import kr.syeyoung.dungeonsguide.gui.MPanel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

public class MCategory extends MPanel {

    private NestedCategory nestedCategory;
    private RootConfigPanel rootConfigPanel;
    @Getter
    @Setter
    private Color hover = new Color(94, 94, 94, 255);
    public MCategory(NestedCategory nestedCategory, RootConfigPanel rootConfigPanel) {
        this.nestedCategory = nestedCategory;
        this.rootConfigPanel = rootConfigPanel;
    }

    @Override
    public void render(int absMousex, int absMousey, int relMousex0, int relMousey0, float partialTicks, Rectangle scissor) {
        Gui.drawRect(0,0,getBounds().width, getBounds().height,0xFF444444);


        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        GlStateManager.pushMatrix();
        GlStateManager.translate(5,5,0);
        GlStateManager.scale(1.0,1.0,0);
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        fr.drawString((lastAbsClip.contains(absMousex, absMousey) ? "§n" : "") + nestedCategory.categoryName(), 0,0, 0xFFFFFFFF);
        GlStateManager.popMatrix();

        fr.drawSplitString("NO DESC", 5, 23, getBounds().width -10, 0xFFBFBFBF);
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 17);
    }
    @Override
    public void mouseClicked(int absMouseX, int absMouseY, int relMouseX, int relMouseY, int mouseButton) {
        if (lastAbsClip.contains(absMouseX, absMouseY))
            rootConfigPanel.setCurrentPage(nestedCategory.categoryFull());
    }
}