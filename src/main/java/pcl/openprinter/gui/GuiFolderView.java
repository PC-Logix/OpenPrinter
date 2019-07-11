package pcl.openprinter.gui;

import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import pcl.openprinter.inventory.FolderInventory;

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

	public final static ResourceLocation folderView = new ResourceLocation("openprinter", "textures/gui/folder_view.png");
	public final static ResourceLocation folderViewEmpty = new ResourceLocation("openprinter", "textures/gui/folder_view_empty.png");

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public GuiFolderView(World world, EntityPlayer player) {
		ItemStack stack = player.getHeldItemMainhand();
		inventory = new FolderInventory(stack);

		pageCount = 0;
		for(int slot = 0; slot < inventory.getSlots(); slot++)
			if(!inventory.getStackInSlot(slot).isEmpty())
				pageCount++;

		if(stack.hasDisplayName()) {
			name = stack.getDisplayName();
		}

	}

	FolderInventory inventory;

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

		this.mc.renderEngine.bindTexture(pageCount > 0 ? folderView : folderViewEmpty);

		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);

		GL11.glPushMatrix();
		GL11.glTranslated(this.width / 2 + 92, this.height / 2 - 106, 0);
		GL11.glRotated(90, 0, 0, 1);
		mc.fontRenderer.drawString(PaperGUI.limit(name,14), 0, 0, 0x000000);
		GL11.glPopMatrix();

		if(pageCount == 0)
			return;

		GL11.glPushMatrix();
		GL11.glTranslated(this.width / 2 + 12, this.height / 2 - 114, 0);
		GL11.glScaled(0.9, 0.9, 0);
		mc.fontRenderer.drawString("Page " + (this.currPage + 1) + " of " + this.pageCount, 0, 0, 0x0);
		GL11.glPopMatrix();

		updateButtons();

		NBTTagCompound pageNBT = inventory.getStackInSlot(currPage).getTagCompound();
		PaperGUI.drawPrintedPage(pageNBT, width, height, posX + 16, posY + 16);

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