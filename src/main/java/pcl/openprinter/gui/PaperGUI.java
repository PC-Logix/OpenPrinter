/**
 * 
 */
package pcl.openprinter.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.awt.*;

import pcl.openprinter.util.CharacterWidth;

/**
 * @author Caitlyn
 * 
 */
public class PaperGUI extends GuiScreen {
	int pageCount = 0;
	int currPage = 0;

	boolean isBook = false;
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	public static String limit(String value, int length)
	{
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length)
		{
			buf.setLength(length);
			buf.append("...");
		}

		return buf.toString();
	}

	public static ItemStack stack = null;
	public PaperGUI(World world, EntityPlayer player) {
	}
	
	public void initGui(){
		
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks){

		drawDefaultBackground();

		if (stack.hasTagCompound()) {
			isBook = stack.getTagCompound().hasKey("book");
			pageCount = stack.getTagCompound().getTagList("pages", Constants.NBT.TAG_STRING).tagCount();
		}

		currPage = 0;
		//OpenPrinter.logger.info(stack.getTagCompound().getTagList("pages", Constants.NBT.TAG_COMPOUND).getStringTagAt(0));


		if (isBook) {
			final int xSizeOfTexture = 271;
			final int ySizeOfTexture = 180;
			final ResourceLocation theTexture = new ResourceLocation("openprinter", "textures/gui/book.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(theTexture);

			int posX = (width - xSizeOfTexture) / 2;
			int posY = (height - ySizeOfTexture) / 2;
			drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);
			int offset = 100;

			for (int x = 0; x <= stack.getTagCompound().getKeySet().size(); x++) {
				String output = stack.getTagCompound().getString("line"+x);
				String[] parts = output.split("∞");
				if(parts.length > 1) {
					Integer outleng = parts[0].replaceAll("(?:§[0-9a-fk-or])+", "").length();
					if (outleng > 30) {
						parts[0] = CharacterWidth.limitWidth(parts[0],164);
					}
					Integer color = Integer.parseInt(parts[1]);
					String alignment = parts[2];
					if (alignment.equalsIgnoreCase("center")) {

						mc.fontRenderer.drawString(parts[0], xSizeOfTexture/2 - mc.fontRenderer.getStringWidth(parts[0])/2, ySizeOfTexture / 2 - offset, color);
					} else {
						mc.fontRenderer.drawString(parts[0], xSizeOfTexture/2 , ySizeOfTexture / 2 - offset, color);
					}
					offset = offset - 10;
				}
			}
		} else {
			final int xSizeOfTexture = 171;
			final int ySizeOfTexture = 208;
			final ResourceLocation theTexture = new ResourceLocation("openprinter", "textures/gui/printer_paper.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(theTexture);

			int posX = (width - xSizeOfTexture) / 2;
			int posY = (height - ySizeOfTexture) / 2;
			drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);
			int offset = 100;
			if (stack.hasTagCompound()) {
				drawPrintedPage(stack.getTagCompound(), xSizeOfTexture, ySizeOfTexture, posX + 6, posY + 6);
			} else {
				mc.fontRenderer.drawString("This page intentionally left blank.", xSizeOfTexture/2 - mc.fontRenderer.getStringWidth("This page intentionally left blank.")/2, ySizeOfTexture / 2 - offset, 0x000000);
			}
		}


		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static void drawPrintedPage(NBTTagCompound nbt, int width, int height, int posX, int posY){
		Minecraft mc = Minecraft.getMinecraft();
		if (nbt.getDouble("version") == 2.0) {
			//drawCenteredString(mc.fontRenderer, stack.stackTagCompound.getString("pageTitle"), width / 2, height / 2 - 110, 0x000000);
			for (int x = 0; x <= nbt.getKeySet().size(); x++) {
				String output = nbt.getString("line"+x);
				String[] parts = output.split("∞");
				if(parts.length > 1) {
					Integer outleng = parts[0].replaceAll("(?:§[0-9a-fk-or])+", "").length();
					if (outleng > 30) {
						parts[0] = CharacterWidth.limitWidth(parts[0],164);
					}

					Integer color = 0;

					try { color = Integer.parseInt(parts[1]); } catch(Exception ex){} // just catch invalid user inputs

					String alignment = parts[2];
					if (alignment.equalsIgnoreCase("center")) {
						mc.fontRenderer.drawString(parts[0], posX + width/2 - mc.fontRenderer.getStringWidth(parts[0])/2, posY, color);
					} else {
						mc.fontRenderer.drawString(parts[0] , posX, posY, color);
					}
					posY+=10;
				}
			}
		} else {
			//drawCenteredString(mc.fontRenderer, stack.stackTagCompound.getString("pageTitle"), width / 2, height / 2 - 110, 0x000000);
			for (int x = 0; x <= nbt.getKeySet().size(); x++) {
				String output = nbt.getString("line"+x);
				Integer outleng = output.replaceAll("(?:§[0-9a-fk-or])+", "").length();
				if (outleng > 30) {
					output = limit(output,30);
				}
				Integer color = nbt.getInteger("color"+x);
				String alignment = nbt.getString("alignment"+x);
				if (alignment.equalsIgnoreCase("center")) {
					mc.fontRenderer.drawString(output, posX - mc.fontRenderer.getStringWidth(output)/2, posY, color);
				} else {
					mc.fontRenderer.drawString(output , posX, posY, color);
				}
				posY+=10;
			}
		}
	}
}
