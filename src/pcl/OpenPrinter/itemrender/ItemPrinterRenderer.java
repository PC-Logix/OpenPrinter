/**
 * 
 */
package pcl.OpenPrinter.itemrender;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.WavefrontObject;
import pcl.OpenPrinter.OpenPrinter;
import pcl.OpenPrinter.TileEntity.PrinterTE;

/**
 * @author Caitlyn
 *
 */
public class ItemPrinterRenderer implements IItemRenderer {
	private float scale = 1;
	WavefrontObject model = null;
	private final ResourceLocation theTexture = new ResourceLocation("openprinter", "textures/obj/OpenPrinter.png");
	TileEntitySpecialRenderer render;
	private TileEntity dummytile;

    public ItemPrinterRenderer(TileEntitySpecialRenderer render, TileEntity dummy) {
	    this.render = render;
	    this.dummytile = dummy;
	    model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/" + OpenPrinter.MODID + "/models/printer.obj");
	}

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (OpenPrinter.render3D) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glScalef(scale, scale, scale);
			//GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				//GL11.glRotatef(-180F, 0F, 1F, 0F);
			//GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(theTexture);
			model.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

}