package pcl.openprinter.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openprinter.items.FolderContainer;
import pcl.openprinter.items.FolderInventory;
import pcl.openprinter.network.MessageGUIFolder;
import pcl.openprinter.network.PacketHandler;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class GuiFolderInventory extends ContainerGUI {
	private GuiTextField text;
	private String name;

	private static final ResourceLocation iconLocation = new ResourceLocation("openprinter", "textures/gui/inventoryitem.png");

	/** The inventory to render on screen */
	private final FolderInventory inventory;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.text = new GuiTextField(1, this.fontRenderer, this.width / 2 - 68, guiTop + 5, 137, 10);
		text.setMaxStringLength(203);
		text.setText("Name");
		String s = this.inventory.hasCustomName() ? this.inventory.getName() : I18n.translateToLocal(this.inventory.getName());
		//this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.text.setText(s);
		this.text.setFocused(true);
	}

	public GuiFolderInventory(FolderContainer containerItem)
	{
		super(containerItem, 176, 136);
		this.inventory = containerItem.inventory;
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.text.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char key, int par2)
	{       
		//super.keyTyped(key, par2);

		if (text.isFocused()) {
			text.textboxKeyTyped(key, par2);
			//nameString = name.getText();
		}

		if (par2 == 1) {
			this.mc.player.closeScreen();
		}

		if (key == '\r') {
			this.name = this.text.getText();
			actionPerformed();
		}
	}
	@Override
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		try {
			super.mouseClicked(x, y, btn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.text.mouseClicked(x, y, btn);
	}

	public void onGuiClosed(){
		Keyboard.enableRepeatEvents(false);
		this.inventory.setInventoryName(this.text.getText());
		super.onGuiClosed();
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(iconLocation);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		int i1;
		this.text.drawTextBox();
	}

	@SideOnly(Side.CLIENT)
	protected void actionPerformed() {
		this.name = this.text.getText();
		PacketHandler.INSTANCE.sendToServer(new MessageGUIFolder(this.name, 1));
	}

	public void setName(String name2) {
		this.name = name2;
	}
}
