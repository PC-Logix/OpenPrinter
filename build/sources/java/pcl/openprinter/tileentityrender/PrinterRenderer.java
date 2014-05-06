package pcl.openprinter.tileentityrender;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.WavefrontObject;

import org.lwjgl.opengl.GL11;

import pcl.openprinter.Config;
import pcl.openprinter.OpenPrinter;
import cpw.mods.fml.client.FMLClientHandler;

public class PrinterRenderer extends TileEntitySpecialRenderer {
	private float scale = 1;
	WavefrontObject model = null;
	private final ResourceLocation theTexture = new ResourceLocation("openprinter", "textures/obj/OpenPrinter.png");
	public PrinterRenderer() {
		model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/" + OpenPrinter.MODID + "/models/printer.obj");
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		if (OpenPrinter.render3D) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslated(x, y, z);
			int dir = tileEntity.getBlockMetadata();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			if (dir == 1 || dir == 3)
				GL11.glRotatef(dir * 90F, 0F, 1F, 0F);
			else if (dir == 0)
				GL11.glRotatef(-180F, 0F, 1F, 0F);
			else
				GL11.glRotatef(dir * 180F, 0F, 1F, 0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(theTexture);
			model.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}
}
