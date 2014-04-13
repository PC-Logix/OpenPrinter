package pcl.openprinter.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * @author Caitlyn
 *
 */
import org.lwjgl.opengl.GL11;

import pcl.openprinter.OpenPrinter;
import pcl.openprinter.tileentity.PrinterContainer;
import pcl.openprinter.tileentity.PrinterTE;

public class PrinterGUI extends GuiContainer {

        public PrinterGUI (InventoryPlayer inventoryPlayer, PrinterTE tileEntity) {
                //the container is instanciated and passed to the superclass for handling
                super(new PrinterContainer(inventoryPlayer, tileEntity));
        }

        @Override
        protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        	//the parameters for drawString are: string, x, y, color
        	fontRenderer.drawSplitString(StatCollector.translateToLocal("gui.string.blackInk"), 25, ySize - 128, 40, 4210752);
        	fontRenderer.drawSplitString(StatCollector.translateToLocal("gui.string.colorInk"), 55, ySize - 128, 40, 4210752);
        	fontRenderer.drawSplitString(StatCollector.translateToLocal("gui.string.paperInput"), 125, ySize - 128, 40, 4210752);
        	fontRenderer.drawString(StatCollector.translateToLocal("gui.string.printer"), 55, 2, 4210752);
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
                //draw your Gui here, only thing you need to change is the path
                ResourceLocation texture = new ResourceLocation(OpenPrinter.MODID, "textures/gui/printer.png");
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.renderEngine.bindTexture(texture);
                int x = (width - xSize) / 2;
                int y = (height - ySize) / 2;
                this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        }

}