package pcl.openprinter.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import pcl.openprinter.inventory.BriefcaseContainer;

public class BriefcaseInventory extends ContainerGUI {

    private static final ResourceLocation iconLocation = new ResourceLocation("openprinter", "textures/gui/briefcaseinventory.png");

    public BriefcaseInventory(BriefcaseContainer containerItem) {
        super(containerItem, 176, 152);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(iconLocation);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        fr.drawString("Briefcase", guiLeft + 7, guiTop + 6, 0x505050, false);
        fr.drawString("Inventory", guiLeft + 7, guiTop + 60, 0x505050, false);
    }

}



