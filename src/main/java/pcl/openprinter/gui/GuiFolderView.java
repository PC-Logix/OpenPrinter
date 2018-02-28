/**
 * 
 */
package pcl.openprinter.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * @author Caitlyn
 * 
 */
public class GuiFolderView extends GuiScreen {

	private int pageCount = 0;
	private int currPage = 0;

	final int xSizeOfTexture = 200;
	final int ySizeOfTexture = 230;

	private String name = "Folder";

	private GuiFolderView.NextPageButton buttonNextPage;
	private GuiFolderView.NextPageButton buttonPreviousPage;

	public final static ResourceLocation folderView = new ResourceLocation("openprinter", "textures/gui/folderView.png");
	public final static ResourceLocation folderViewEmpty = new ResourceLocation("openprinter", "textures/gui/folderViewEmpty.png");
	
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

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public static ItemStack stack = null;
	public GuiFolderView(World world, EntityPlayer player) { }
	@SuppressWarnings("unchecked")
	public void initGui(){
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);

		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		this.buttonList.add(this.buttonNextPage = new NextPageButton(1, posX + 120, posY + 217, true));
		this.buttonList.add(this.buttonPreviousPage = new NextPageButton(2, posX + 38, posY + 217, false));
		this.updateButtons();
	}
	public void drawScreen(int i, int j, float f){
		drawDefaultBackground();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if(stack.hasTagCompound()) {
			if (stack.getTagCompound().hasKey("ItemInventory", Constants.NBT.TAG_LIST) && 
					stack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND).tagCount() > 0) {
				this.mc.renderEngine.bindTexture(folderView);
			} else {
				this.mc.renderEngine.bindTexture(folderViewEmpty);
			}
		} else {
			this.mc.renderEngine.bindTexture(folderViewEmpty);
		}
		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);

		int offset = 100;

		if(stack.hasDisplayName()) {
			name = stack.getDisplayName();
		}
		GL11.glPushMatrix();
		GL11.glTranslated(this.width / 2 + 92, this.height / 2 - 106, 0);
		GL11.glRotated(90, 0, 0, 1);
		mc.fontRenderer.drawString(limit(name,14), 0, 0, 0x000000);
		GL11.glPopMatrix();
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("ItemInventory", Constants.NBT.TAG_LIST)) {
			NBTTagList ItemInventory = stack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
			this.pageCount = ItemInventory.tagCount();

			GL11.glPushMatrix();
			GL11.glTranslated(this.width / 2 + 12, this.height / 2 - 114, 0);
			GL11.glScaled(0.9, 0.9, 0);
			if (this.pageCount > 0)
				mc.fontRenderer.drawString("Page " + (this.currPage + 1) + " Of " + this.pageCount, 0, 0, 0x000000);
			GL11.glPopMatrix();
			
			if (this.currPage <= 0)
				this.buttonPreviousPage.visible = false;
			else
				this.buttonPreviousPage.visible = true;

			if (this.currPage >= this.pageCount - 1)
				this.buttonNextPage.visible = false;
			else
				this.buttonNextPage.visible = true;
			for (int l = 0; l <= ItemInventory.getCompoundTagAt(this.currPage).getCompoundTag("tag").getKeySet().size(); l++) {
				if (ItemInventory.getCompoundTagAt(this.currPage).getCompoundTag("tag").getDouble("version") == 2.0) {
					String output = ItemInventory.getCompoundTagAt(this.currPage).getCompoundTag("tag").getString("line"+l);
					String[] parts = output.split("∞");
					if(parts.length > 1) {
						Integer outleng = parts[0].replaceAll("(?:§[0-9a-fk-or])+", "").length();
						if (outleng > 30) {
							parts[0] = limit(parts[0],30);
						}
						Integer color = Integer.parseInt(parts[1]);
						String alignment = parts[2];
						if (alignment.equalsIgnoreCase("center")) {
							mc.fontRenderer.drawString(parts[0], width/2 - mc.fontRenderer.getStringWidth(parts[0])/2, height / 2 - offset, color);
						} else {
							mc.fontRenderer.drawString(parts[0] , width/2 - xSizeOfTexture/2 + 6, height / 2 - offset, color);
						}
						offset = offset - 10;	
					}
				} else {
					drawCenteredString(mc.fontRenderer, stack.getTagCompound().getString("pageTitle"), width / 2, height / 2 - 110, 0x000000);
					String output = ItemInventory.getCompoundTagAt(this.currPage).getCompoundTag("tag").getString("line"+l);
					Integer outleng = output.replaceAll("(?:§[0-9a-fk-or])+", "").length();
					if (outleng > 30) {
						output = limit(output,30);
					}
					Integer color = ItemInventory.getCompoundTagAt(this.currPage).getCompoundTag("tag").getInteger("color"+l);
					String alignment = ItemInventory.getCompoundTagAt(this.currPage).getCompoundTag("tag").getString("alignment"+l);
					if (alignment.equalsIgnoreCase("center")) {
						mc.fontRenderer.drawString(output, width/2 - mc.fontRenderer.getStringWidth(output)/2, height / 2 - offset, color);
					} else {
						mc.fontRenderer.drawString(output , width/2 - xSizeOfTexture/2 + 6, height / 2 - offset, color);
					}
					offset = offset - 10;
				}
			}
		}

		super.drawScreen(i, j, f);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == 1 && this.currPage < this.pageCount)
			this.currPage = this.currPage + 1;

		if (button.id == 2 && this.currPage -1 >= 0)
			this.currPage = this.currPage - 1;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	private void updateButtons()
	{
		this.buttonNextPage.visible = (this.currPage < this.pageCount - 1);
		this.buttonPreviousPage.visible = this.currPage > 0;

	}


	@SideOnly(Side.CLIENT)
	static class NextPageButton extends GuiButton
	{
		private final boolean show;

		private int x;
		private int y;

		public NextPageButton(int id, int x, int y, boolean enabled)
		{
			super(id, x, y, 23, 13, "");
			this.show = enabled;
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(folderView);
				int k = 10;
				int l = 233;

				if (show) {
					k += 20;
				}

				if (!this.show) {
					l += 13;
				}

				this.drawTexturedModalRect(this.x, this.y, k, l, 23, 13);
			}
		}
	}
}