package pcl.OpenPrinter.TileEntityRender;

import org.lwjgl.opengl.GL11;

import pcl.OpenPrinter.OpenPrinter;
import pcl.OpenPrinter.Blocks.Printer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class PrinterRenderer implements ISimpleBlockRenderingHandler
{
	private WavefrontObject model;
	private final ResourceLocation texture = new ResourceLocation("openprinter", "textures/obj/OpenPrinter.png");
	
	public PrinterRenderer()
	{
		model = (WavefrontObject)AdvancedModelLoader.loadModel("/assets/" + OpenPrinter.MODID + "/models/printer.obj");
		assert null != model : "WTF, model didn't load!";
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		GL11.glPushMatrix();
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		model.renderOnly("0");
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		// Stop the tesselator from messing with us
		int previousGLRenderType = Tessellator.instance.drawMode;
		Tessellator.instance.setColorOpaque_F(1, 1, 1);
		Tessellator.instance.draw();
		
		// Draw our stuff
		GL11.glPushMatrix();
		GL11.glTranslated((double)x, (double)y, (double)z);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		int dir = world.getBlockMetadata(x, y, z);
		if (dir == 1)
			model.renderOnly("90");
		else if (dir == 0)
			model.renderOnly("180");
		else if (dir == 2)
			model.renderOnly("0");
		else if (dir == 3)
			model.renderOnly("270");
		GL11.glPopMatrix();

		// Restart the Tesselator
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		Tessellator.instance.startDrawing(previousGLRenderType);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return Printer.block.getRenderType();
	}

}
