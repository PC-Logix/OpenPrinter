package pcl.openprinter.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
/**
 * @author Caitlyn
 *
 */
import org.lwjgl.opengl.GL11;

import pcl.openprinter.OpenPrinter;
import pcl.openprinter.container.PrinterContainer;
import pcl.openprinter.tileentity.PrinterTE;

public class PrinterGUI extends GuiContainer {

        public PrinterGUI (InventoryPlayer inventoryPlayer, PrinterTE tileEntity) {
                //the container is instanciated and passed to the superclass for handling
                super(new PrinterContainer(inventoryPlayer, tileEntity));
        		this.xSize = 175;
        		this.ySize = 195;
        }

        @Override
        protected void drawGuiContainerForegroundLayer(int x, int y) {
        	super.drawGuiContainerForegroundLayer(x, y);
        	GL11.glEnable(GL11.GL_ALPHA_TEST);
        	//the parameters for drawString are: string, x, y, color
        	mc.fontRendererObj.drawSplitString(I18n.translateToLocal("gui.string.blackInk"), 25, 25, 40, 0x404040);
        	mc.fontRendererObj.drawSplitString(I18n.translateToLocal("gui.string.colorInk"), 55, 25, 40, 0x404040);
        	mc.fontRendererObj.drawSplitString(I18n.translateToLocal("gui.string.paperInput"), 125, 25, 40, 0x404040);
        	mc.fontRendererObj.drawString(I18n.translateToLocal("gui.string.scannerInput"), 70, 4, 0x404040);
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