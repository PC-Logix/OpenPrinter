package pcl.openprinter.mud.gui;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import pcl.openprinter.OpenPrinter;

import java.util.List;

public class GuiModUpdateButton extends GuiButton{


    RenderItem renderItem = new RenderItem();
    ItemStack icon = new ItemStack(OpenPrinter.printerBlock);
    List<String> text = null;
    private GuiScreen parent;
    public GuiModUpdateButton(int id, int x, int y, GuiScreen parent) {
        super(id, x, y, 100, 20, "");
        this.parent = parent;
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        super.drawButton(par1Minecraft, par2, par3);

        if(text == null){
            text = par1Minecraft.fontRenderer.listFormattedStringToWidth(StatCollector.translateToLocal("mud.name"), 80);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        renderItem.renderItemAndEffectIntoGUI(par1Minecraft.fontRenderer, par1Minecraft.getTextureManager(), icon, xPosition+2, yPosition+2);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        float scale = 1.25F;
        GL11.glScalef(1F/scale, 1F/scale, 1F/scale);

        for(int i = 0; i < text.size() && i < 2; i++){
            drawCenteredString(par1Minecraft.fontRenderer, text.get(i), (int)(scale*(xPosition+20+40)), (int)(scale*((yPosition+3)+par1Minecraft.fontRenderer.FONT_HEIGHT*i)), 0xFFFFFF);
        }
        GL11.glScalef(scale, scale, scale);
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
        boolean press = super.mousePressed(par1Minecraft, par2, par3);
        if(press){
            Minecraft.getMinecraft().displayGuiScreen(new GuiChangelogDownload(parent));
        }
        return press;
    }
}
