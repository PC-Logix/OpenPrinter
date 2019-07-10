/**
 * 
 */
package pcl.openprinter.tileentity;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import pcl.openprinter.ContentRegistry;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.books.BiblioCraftBigBook;
import pcl.openprinter.books.VanillaBook;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaperRoll;

/**
 * @author Caitlyn
 *
 */
public class PrinterTE extends TileEntity implements ITickable, Environment, IInventory, ISidedInventory {

	public Double PrinterFormatVersion = 2.0;
	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	protected boolean addedToNetwork = false;

	public PrinterTE() {
		if (this.node() != null) {
			initOCFilesystem();
		}
		Arrays.fill(printerItemStacks, ItemStack.EMPTY);
	}

	private String getComponentName() {
		return "openprinter";
	}

	private Object oc_fs;
	protected ManagedEnvironment oc_fs(){
		return (ManagedEnvironment) this.oc_fs;
	}

	private void initOCFilesystem() {
		oc_fs = li.cil.oc.api.FileSystem.asManagedEnvironment(li.cil.oc.api.FileSystem.fromClass(OpenPrinter.class, OpenPrinter.MODID, "/lua/printer/"), "printer");
		((Component) oc_fs().node()).setVisibility(Visibility.Network);
	}

	@Override
	public Node node() {
		return node;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (node != null)
			node.remove();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (node != null)
			node.remove();
	}

	@Override
	public void onConnect(final Node node) {
		if (node == node()) {
			node.connect(oc_fs().node());
		}
	}

	@Override
	public void onDisconnect(final Node node) {
		if (node.host() instanceof Context) {
			node.disconnect(oc_fs().node());
		} else if (node == this.node) {
			oc_fs().node().remove();
		}
	}

	@Override
	public void update() {
		if(!addedToNetwork) {
			addToNetwork();
		}
	}

	protected void addToNetwork() {
		if(!addedToNetwork) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
		}
	}

	private ItemStack[] printerItemStacks = new ItemStack[20];
	private List<String> lines = new ArrayList<String>();
	private List<String> align = new ArrayList<String>();
	private List<Integer> colors = new ArrayList<Integer>();
	private String pageTitle = "";

	Integer colorUses = 0;
	Integer blackUses = 0;


	private static final int[] slots_top = new int[] {2};
	private static final int[] slots_bottom = new int[] {3,4,5,6,7,8,9,10,11,12};
	private static final int[] slots_sides = new int[] {0,1};

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
		if (oc_fs != null && oc_fs().node() != null) {
			oc_fs().node().load(nbt.getCompoundTag("oc:fs"));
		}
		NBTTagList var2 = nbt.getTagList("Items",nbt.getId());
		this.printerItemStacks = new ItemStack[this.getSizeInventory()];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.printerItemStacks.length)
			{
				this.printerItemStacks[var5] = new ItemStack(var4);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		if (oc_fs != null && oc_fs().node() != null) {
			final NBTTagCompound fsNbt = new NBTTagCompound();
			oc_fs().node().save(fsNbt);
			nbt.setTag("oc:fs", fsNbt);
		}

		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.printerItemStacks.length; ++var3)
		{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				this.printerItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
		}
		nbt.setTag("Items", var2);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return tag;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}


	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}

	//Real Printer methods follow:
	@Callback
	public Object[] scanLine(Context context, Arguments args) {
		ItemStack scannedPage = getStackInSlot(13);

		if (scannedPage.getItem() instanceof PrintedPage && scannedPage.hasTagCompound()) {
			return new Object[] { scannedPage.getTagCompound().getString("line" + args.checkInteger(0)) };
		} else {
			return new Object[] { false };
		}
	}

	@Callback
	public Object[] scan(Context context, Arguments args) {
		ItemStack scannerInput = getStackInSlot(13);

		if (scannerInput.getItem() instanceof PrintedPage) {
			return readPrintedPage(scannerInput);
		}

		return new Object[] { false };
	}

	@Callback
	public Object[] scanBook(Context context, Arguments args) {
		ItemStack scannerInput = getStackInSlot(13);

		if (scannerInput.getItem() instanceof ItemWritableBook || scannerInput.getItem() instanceof ItemWrittenBook) {
			return new VanillaBook().readFromStack(scannerInput);
		}
		else if (scannerInput.getItem().getRegistryName().toString().equals("bibliocraft:bigbook"))
			return new BiblioCraftBigBook().readFromStack(scannerInput);

		return new Object[] { false };
	}

	private static Object[] readPrintedPage(ItemStack printedPage){
		if(!printedPage.hasTagCompound())
			return new Object[] { false, "page doesnt have nbt data"};

		String outPageTitle = null;
		Map<Integer, String> output = new HashMap<Integer, String>();

		if (printedPage.getTagCompound().hasKey("pageTitle")) {
			outPageTitle = printedPage.getTagCompound().getString("pageTitle");
		}
		for (int x = 0; x <= 20; x++) {
			if(printedPage.getTagCompound().hasKey("line"+x)) {
				output.put(x, printedPage.getTagCompound().getString("line"+x));
			}
		}
		return new Object[] { outPageTitle, output };
	}

	@Callback
	public Object[] printTag(Context context, Arguments args) throws Exception {
		if (OpenPrinter.cfg.enableNameTag) {
			if (!getStackInSlot(0).isEmpty()) {
				if (!getStackInSlot(2).isEmpty() && getStackInSlot(2).getItem().equals(Items.NAME_TAG)) { //No paper
					for (int x = 3; x <= 12; x++) { //Loop the 9 output slots checking for a empty one
						if (!getStackInSlot(x).isEmpty()) { //The slot is empty lets make us a NameTag

							printerItemStacks[x] = new ItemStack(Items.NAME_TAG);
							printerItemStacks[x].setTagCompound(new NBTTagCompound());

							NBTTagCompound nameTag = new NBTTagCompound();

							nameTag.setString("Name", args.checkString(0));

							printerItemStacks[x].getTagCompound().setTag("display", nameTag);

							getStackInSlot(0).setItemDamage(getStackInSlot(0).getItemDamage() + 1);
							if(getStackInSlot(0).getItemDamage() >= getStackInSlot(0).getMaxDamage()) {
								setInventorySlotContents(0, ItemStack.EMPTY);
							}
							decrStackSize(2, 1);
							return new Object[]{true};
						}
					} throw new Exception("No empty output slots.");
				} else { throw new Exception("Please load Name Tags."); }
			} else { throw new Exception("Please load Black Ink."); }
		} else {
			throw new Exception("Name Tag printing is disabled.");
		}
	}

	@Callback
	public Object[] print(Context context, Arguments args) throws Exception {
		int copies = args.optInteger(1, 9);
		boolean markColor = false;
		boolean markBlack = false;
		for (int i = 0; i < copies; i++) {
			if((!getStackInSlot(0).isEmpty() && !getStackInSlot(1).isEmpty())) { //No color ink
				if(!getStackInSlot(2).isEmpty()) { //No paper
					for (int x = 3; x <= 12; x++) { //Loop the 9 output slots checking for a empty one
						if (getStackInSlot(x).isEmpty()) { //The slot is empty lets make us a new page
							printerItemStacks[x] = new ItemStack(ContentRegistry.printedPage);
							printerItemStacks[x].setTagCompound(new NBTTagCompound());
							if(pageTitle != "") {
								printerItemStacks[x].getTagCompound().setString("pageTitle", pageTitle);
								printerItemStacks[x].setStackDisplayName(pageTitle);
								pageTitle = "";
							}
							printerItemStacks[x].getTagCompound().setDouble("version", PrinterFormatVersion);
							int iter = 0;
							for (String s : lines) {
								printerItemStacks[x].getTagCompound().setString("line"+iter, lines.get(iter) + "∞" + colors.get(iter) + "∞" + align.get(iter));

								if (colors.get(iter) != 0x000000) {
									markColor = true;
									colorUses++;
								} else {
									markBlack = true;
									blackUses++;
								}
								if(lines.get(iter).matches(".*§[0-9a-f].*")) {
									markColor = true;
									markBlack = false;
									Pattern regex = Pattern.compile("§[0-9a-f]*");
									Matcher matcher = regex.matcher(lines.get(iter));
									while (matcher.find())
										colorUses++;
								}
								iter++; 
							}
							lines.clear();
							colors.clear();
							align.clear();
							if (getStackInSlot(2).getItem() instanceof PrinterPaperRoll) {
								getStackInSlot(2).setItemDamage(getStackInSlot(2).getItemDamage() + 1);
								if(getStackInSlot(2).getItemDamage() >= getStackInSlot(2).getMaxDamage()) {
									setInventorySlotContents(2, ItemStack.EMPTY);
								}
							} else {
								decrStackSize(2, 1);
							}
							if (markColor) {
								getStackInSlot(1).setItemDamage(getStackInSlot(1).getItemDamage() + colorUses++);
								if(getStackInSlot(1).getItemDamage() >= getStackInSlot(1).getMaxDamage()) {
									setInventorySlotContents(1, ItemStack.EMPTY);
								}
							}
							if (markBlack) {
								getStackInSlot(0).setItemDamage(getStackInSlot(0).getItemDamage() + blackUses);
								if(getStackInSlot(0).getItemDamage() >= getStackInSlot(0).getMaxDamage()) {
									setInventorySlotContents(0, ItemStack.EMPTY);
								}
							}
							return new Object[] { true };
						}
					} throw new Exception("No empty output slots.");
				} else {
					throw new Exception("Please load Paper.");
				}
			} else {
				throw new Exception("Please load Ink.");
			}
		}
		return new Object[] { false };
	}

	@Callback
	public Object[] writeln(Context context, Arguments args) throws Exception{
		if(lines.size() >= 20) {
			throw new Exception("To many lines.");
		}
		int color = 0x000000;
		String alignment = "left";
		if (args.count() == 2){
			if (args.isInteger(1)) {
				color = args.checkInteger(1);
			} else if (args.isString(1)) {
				alignment = args.checkString(1);
			}
		} 

		if (args.count() == 3){
			if (args.isInteger(1)) {
				color = args.checkInteger(1);
			}
			if (args.isString(2)) {
				alignment = args.checkString(2);
			}
		}

		lines.add(args.checkString(0));
		colors.add(color);
		align.add(alignment);
		return new Object[] { true };
	}

	@Callback
	public Object[] setTitle(Context context, Arguments args) {
		pageTitle = args.checkString(0);
		return new Object[] { true };
	}

	@Callback
	public Object[] getPaperLevel(Context context, Arguments args) { 
		if(!getStackInSlot(2).isEmpty()) {
			if (getStackInSlot(2).getItem() instanceof PrinterPaperRoll) {
				return new Object[] { 256 - getStackInSlot(2).getItemDamage() };
			} else {
				return new Object[] { getStackInSlot(2).getCount() };
			}
		} else {
			return new Object[] { false };
		}
	}

	@Callback
	public Object[] getBlackInkLevel(Context context, Arguments args) { 
		if(!getStackInSlot(0).isEmpty()) {
			if (getStackInSlot(0).getItem() instanceof PrinterInkBlack) {
				return new Object[] { OpenPrinter.cfg.printerInkUse - getStackInSlot(0).getItemDamage()};
			} else {
				return new Object[] { false };
			}
		} else {
			return new Object[] { false };
		}
	}

	@Callback
	public Object[] getColorInkLevel(Context context, Arguments args) { 
		if(!getStackInSlot(1).isEmpty()) {
			if (getStackInSlot(1).getItem() instanceof PrinterInkColor) {
				return new Object[] { OpenPrinter.cfg.printerInkUse - getStackInSlot(1).getItemDamage() };
			} else {
				return new Object[] { false };
			}
		} else {
			return new Object[] { false };
		}
	}

	@Callback(direct = true)
	public Object[] charCount(Context context, Arguments args) {
		return new Object[] { args.checkString(0).replaceAll("(?:§[0-9a-fk-or])+", "").length() };
	}

	@Callback
	public Object[] clear(Context context, Arguments args) {
		lines.clear();
		colors.clear();
		align.clear();
		pageTitle = "";
		return new Object[] { true };
	}

	@Override
	public int getSizeInventory() {
		return this.printerItemStacks.length;
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack s : printerItemStacks)
		{
			if (!s.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.printerItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty()) {
			if (stack.getCount() <= amt) {
				setInventorySlotContents(slot, ItemStack.EMPTY);
			} else {
				stack = stack.splitStack(amt);
				if (stack.getCount() == 0) {
					setInventorySlotContents(slot, ItemStack.EMPTY);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (!getStackInSlot(i).isEmpty())
		{
			ItemStack var2 = getStackInSlot(i);
			setInventorySlotContents(i,ItemStack.EMPTY);
			return var2;
		}
		else
		{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.printerItemStacks[i] = itemstack;
		if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit())
		{
			itemstack.setCount(this.getInventoryStackLimit());
		}
	}


	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return getWorld().getTileEntity(pos) == this &&
				entityplayer.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64;
	}


	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0) {
			if (itemstack.getItem() instanceof PrinterInkBlack) {
				return true;
			}
		} else if(i == 1) {
			if (itemstack.getItem() instanceof PrinterInkColor) {
				return true;
			}
		} else if (i == 2) {
			if (itemstack.getItem() instanceof PrinterPaperRoll || itemstack.getItem().equals(Items.PAPER) || itemstack.getItem().equals(Items.NAME_TAG)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? slots_bottom : (side == EnumFacing.UP ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing face) {
		return this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing face) {
		return true;
	}

	@Override
	public String getName() {
		return "openprinter";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void onMessage(Message arg0) {
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return new TextComponentString(I18n.translateToLocal("gui.string.printer"));
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
