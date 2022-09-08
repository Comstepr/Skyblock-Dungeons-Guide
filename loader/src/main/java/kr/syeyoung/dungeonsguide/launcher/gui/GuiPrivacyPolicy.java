package kr.syeyoung.dungeonsguide.launcher.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiPrivacyPolicy extends GuiScreen {
    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.buttonList.add(new GuiButton(0, sr.getScaledWidth()/2+50,sr.getScaledHeight()-40, 300, 20,"Accept Privacy Policy"));
        this.buttonList.add(new GuiButton(1, sr.getScaledWidth()/2-50-300,sr.getScaledHeight()-40, 300, 20,"Deny and Play Without DG"));
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 0) {
            // accept
        } else if (button.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(0);

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        fontRenderer.drawString("", (sr.getScaledWidth()-fontRenderer.getStringWidth("Please accept or deny Dungeons Guide Privacy Policy to continue"))/2,40,0xFFFF0000);


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static void clip(ScaledResolution resolution, int x, int y, int width, int height) {
        if (width < 0 || height < 0) return;

        int scale = resolution.getScaleFactor();
        GL11.glScissor((x ) * scale, Minecraft.getMinecraft().displayHeight - (y + height) * scale, (width) * scale, height * scale);
    }
}
