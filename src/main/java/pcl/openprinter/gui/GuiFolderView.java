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
 * Folder GUI with tintable base + overlay rendering.
 * Author: Caitlyn
 */
public class GuiFolderView extends GuiScreen {

	private int pageCount = 0;
	private int currPage = 0;

	final int xSizeOfTexture = 200;
	final int ySizeOfTexture = 230;

	private String name = "Folder";

	private GuiFolderView.NextPageButton buttonNextPage;
	private GuiFolderView.NextPageButton buttonPreviousPage;


	public static final ResourceLocation FOLDER_BASE    = new ResourceLocation("openprinter", "textures/gui/folder_base.png");
	public static final ResourceLocation FOLDER_OVERLAY = new ResourceLocation("openprinter", "textures/gui/folder_overlay.png");

	public static final ResourceLocation FOLDER_EMPTY_OVERLAY = new ResourceLocation("openprinter", "textures/gui/folder_empty_overlay.png");
	FolderInventory inventory;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public GuiFolderView(World world, EntityPlayer player) {
		ItemStack stack = player.getHeldItemMainhand();
		inventory = new FolderInventory(stack);

		// count pages
		pageCount = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++)
			if (!inventory.getStackInSlot(slot).isEmpty())
				pageCount++;

		if (stack.hasDisplayName()) {
			name = stack.getDisplayName();
		}
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);

		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		this.buttonList.add(this.buttonNextPage = new NextPageButton(1, posX + 120, posY + 217, true));
		this.buttonList.add(this.buttonPreviousPage = new NextPageButton(2, posX + 38, posY + 217, false));
		this.updateButtons();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		// ===== Draw tinted folder base =====
		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;

		int color = getFolderColor(Minecraft.getMinecraft().player.getHeldItemMainhand());
		float r = ((color >> 16) & 0xFF) / 255.0f;
		float g = ((color >> 8) & 0xFF) / 255.0f;
		float b = (color & 0xFF) / 255.0f;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// tint base
		GL11.glColor4f(r, g, b, 1.0F);
		this.mc.renderEngine.bindTexture(FOLDER_BASE);
		drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);

		// overlay (details, borders, arrows)
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(FOLDER_OVERLAY);
		drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);

		// ===== Title (rotated on tab) =====
		GL11.glPushMatrix();
		GL11.glTranslated(this.width / 2 + 92, this.height / 2 - 106, 0);
		GL11.glRotated(90, 0, 0, 1);
		mc.fontRenderer.drawString(PaperGUI.limit(name, 14), 0, 0, 0x000000);
		GL11.glPopMatrix();

		if (pageCount > 0) {
			GL11.glPushMatrix();
			GL11.glTranslated(this.width / 2 + 12, this.height / 2 - 114, 0);
			GL11.glScaled(0.9, 0.9, 0);
			mc.fontRenderer.drawString("Page " + (this.currPage + 1) + " of " + this.pageCount, 0, 0, 0x0);
			GL11.glPopMatrix();
		}

		updateButtons();

		if (pageCount > 0 && inventory.getStackInSlot(currPage).hasTagCompound()) {
			NBTTagCompound pageNBT = inventory.getStackInSlot(currPage).getTagCompound();
			PaperGUI.drawPrintedPage(pageNBT, width, height, posX + 16, posY + 16);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1 && this.currPage < this.pageCount)
			this.currPage = this.currPage + 1;

		if (button.id == 2 && this.currPage - 1 >= 0)
			this.currPage = this.currPage - 1;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	private void updateButtons() {
		this.buttonNextPage.visible = (this.currPage < this.pageCount - 1);
		this.buttonPreviousPage.visible = this.currPage > 0;
	}

	/**
	 * Pull folder color from item NBT under OpenPrinter/FolderColor.
	 * Returns default manila if not present.
	 */
	private static int getFolderColor(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return 0xD2B48C; // default manila
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) return 0xD2B48C;
		NBTTagCompound root = tag.getCompoundTag("OpenPrinter");
		return root.hasKey("FolderColor") ? root.getInteger("FolderColor") : 0xD2B48C;
	}

	@SideOnly(Side.CLIENT)
	static class NextPageButton extends GuiButton {
		private final boolean show;
		private int x;
		private int y;

		public NextPageButton(int id, int x, int y, boolean enabled) {
			super(id, x, y, 23, 13, "");
			this.show = enabled;
			this.x = x;
			this.y = y;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				// Buttons are part of the overlay atlas now, draw untinted.
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(FOLDER_OVERLAY);

				int u = 10;   // same UVs you used before
				int v = 233;

				if (show) u += 20;
				if (!this.show) v += 13;

				this.drawTexturedModalRect(this.x, this.y, u, v, 23, 13);
			}
		}
	}
}
