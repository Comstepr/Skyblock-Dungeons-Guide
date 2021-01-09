package kr.syeyoung.dungeonsguide.config.guiconfig;

import kr.syeyoung.dungeonsguide.features.AbstractFeature;
import kr.syeyoung.dungeonsguide.features.FeatureParameter;
import kr.syeyoung.dungeonsguide.roomedit.MPanel;
import kr.syeyoung.dungeonsguide.roomedit.elements.MButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GuiParameterConfig extends GuiScreen {

    private MPanel mainPanel = new MPanel() {
        @Override
        public void onBoundsUpdate() {
            for (MPanel childComponent : getChildComponents()) {
                childComponent.setSize(new Dimension(getBounds().width - 10, childComponent.getSize().height));
            }
        }
    };
    private GuiScreen before;
    private AbstractFeature feature;

    public GuiParameterConfig(final GuiScreen before, AbstractFeature feature) {
        this.before = before;
        for (FeatureParameter parameter: feature.getParameters()) {
            mainPanel.add(new MParameter(feature, parameter, this));
        }
        MButton  save = new MButton();
        save.setText("Back");
        save.setBackgroundColor(Color.green);
        save.setBounds(new Rectangle(0,0,100,20));
        save.setOnActionPerformed(new Runnable() {
            @Override
            public void run() {
                Minecraft.getMinecraft().displayGuiScreen(before);
            }
        });
        mainPanel.add(save);
        mainPanel.setBackgroundColor(new Color(17, 17, 17, 179));

    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        mainPanel.setBounds(new Rectangle(Math.min((scaledResolution.getScaledWidth() - 500) / 2, scaledResolution.getScaledWidth()), Math.min((scaledResolution.getScaledHeight() - 300) / 2, scaledResolution.getScaledHeight()),500,300));
    }

    MPanel within;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glPushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.color(1,1,1,1);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        int heights = 0;
        within = null;
        for (MPanel panel:mainPanel.getChildComponents()) {
            panel.setPosition(new Point(5, -offsetY + heights + 5));
            heights += panel.getBounds().height;

            if (panel.getBounds().contains(mouseX - mainPanel.getBounds().x, mouseY - mainPanel.getBounds().y)) within = panel;
        }
        mainPanel.render0(scaledResolution, new Point(0,0), new Rectangle(0,0,scaledResolution.getScaledWidth(),scaledResolution.getScaledHeight()), mouseX, mouseY, mouseX, mouseY, partialTicks);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GlStateManager.color(1,1,1,1);
        if (within instanceof MParameter) {
            FeatureParameter feature = ((MParameter) within).getParameter();
            GlStateManager.pushAttrib();
            drawHoveringText(new ArrayList<String>(Arrays.asList(feature.getDescription().split("\n"))), mouseX, mouseY, Minecraft.getMinecraft().fontRendererObj);
            GlStateManager.popAttrib();
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        mainPanel.keyTyped0(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mainPanel.mouseClicked0(mouseX, mouseY,mouseX,mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        mainPanel.mouseReleased0(mouseX, mouseY,mouseX,mouseY, state);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        mainPanel.mouseClickMove0(mouseX,mouseY,mouseX,mouseY,clickedMouseButton,timeSinceLastClick);
    }

    public int offsetY = 0;
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            mainPanel.mouseScrolled0(i, j,i,j, wheel);
        }

        if (wheel > 0) offsetY -= 20;
        else if (wheel < 0) offsetY += 20;
        if (offsetY < 0) offsetY = 0;
    }
}