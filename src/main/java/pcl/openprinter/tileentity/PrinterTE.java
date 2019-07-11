package pcl.openprinter.tileentity;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.init.Items;
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

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.openprinter.ContentRegistry;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.books.BiblioCraftBigBook;
import pcl.openprinter.books.VanillaBook;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.util.ItemUtils;

import javax.annotation.Nonnull;

import static pcl.openprinter.tileentity.PrinterTE.InventoryMaterial.*;

public class PrinterTE extends TileEntity implements ITickable, Environment {
	private static final double PrinterFormatVersion = 2.0;

	private ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	private boolean addedToNetwork = false;

	private ItemStackHandler inventoryOutput = new InventoryOutput();
	private ItemStackHandler inventoryScanner = new InventoryScanner();
	private ItemStackHandler inventoryMaterials = new InventoryMaterial();

	private List<String> lines = new ArrayList<String>();
	private List<String> align = new ArrayList<String>();
	private List<Integer> colors = new ArrayList<Integer>();
	private String pageTitle = "";

	public PrinterTE() {}

	class InventoryOutput extends ItemStackHandler {
		InventoryOutput(){
			super(9);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return stack.getItem() instanceof PrintedPage;
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	}

	class InventoryScanner extends ItemStackHandler {
		InventoryScanner(){
			super(1);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return stack.getItem() instanceof PrintedPage
					|| stack.getItem().equals(Items.WRITTEN_BOOK)
					|| stack.getItem().equals(Items.WRITABLE_BOOK)
					|| stack.getItem().getRegistryName().toString().equals("bibliocraft:bigbook");
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	}

	class InventoryMaterial extends ItemStackHandler {
		static final short BLACK_INK_SLOT = 0;
		static final short COLOR_INK_SLOT = 1;
		static final short PAPER_SLOT = 2;

		InventoryMaterial(){
			super(3);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			switch(slot){
				case PAPER_SLOT:
					return stack.getItem() instanceof PrinterPaperRoll
						|| stack.getItem().equals(Items.PAPER)
						|| stack.getItem().equals(Items.NAME_TAG);
				case COLOR_INK_SLOT:
					return stack.getItem() instanceof PrinterInkColor;
				case BLACK_INK_SLOT:
					return stack.getItem() instanceof PrinterInkBlack;
			}
			return false;
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	}

	public void removed(){
		ItemUtils.dropItems(inventoryScanner, world, getPos(), true, 10);
		ItemUtils.dropItems(inventoryMaterials, world, getPos(), true, 10);
		ItemUtils.dropItems(inventoryOutput, world, getPos(), true, 10);
	}

	private String getComponentName() {
		return "openprinter";
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
	public void onConnect(final Node node) {}

	@Override
	public void onDisconnect(final Node node) {}

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


	@Deprecated
	public void readOldInventoryFromNBT(NBTTagCompound nbt) {
		if(!nbt.hasKey("Items"))
			return;

		NBTTagList var2 = nbt.getTagList("Items",nbt.getId());
		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");

			switch(var5){
				case 13:
					inventoryScanner.setStackInSlot(0, new ItemStack(var4));
					break;
				case 0:
					inventoryMaterials.setStackInSlot(0, new ItemStack(var4));
					break;
				case 1:
					inventoryMaterials.setStackInSlot(1, new ItemStack(var4));
					break;
				case 2:
					inventoryMaterials.setStackInSlot(2, new ItemStack(var4));
					break;
				default:
					var5-=3;
					if(var5 > 0 && var5 < inventoryOutput.getSlots())
						inventoryOutput.setStackInSlot(var5, new ItemStack(var4));
					break;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}

		if(nbt.hasKey("inventoryOutput")) {
			inventoryOutput.deserializeNBT(nbt.getCompoundTag("inventoryOutput"));
			inventoryScanner.deserializeNBT(nbt.getCompoundTag("inventoryScanner"));
			inventoryMaterials.deserializeNBT(nbt.getCompoundTag("inventoryMaterials"));
		}
		else
			readOldInventoryFromNBT(nbt); //remove when porting upwards

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}

		nbt.setTag("inventoryScanner", inventoryScanner.serializeNBT());
		nbt.setTag("inventoryMaterials", inventoryMaterials.serializeNBT());
		nbt.setTag("inventoryOutput", inventoryOutput.serializeNBT());

		return super.writeToNBT(nbt);
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
		ItemStack scannedPage = inventoryScanner.getStackInSlot(0);

		if (scannedPage.getItem() instanceof PrintedPage && scannedPage.hasTagCompound()) {
			return new Object[] { scannedPage.getTagCompound().getString("line" + args.checkInteger(0)) };
		} else {
			return new Object[] { false };
		}
	}

	@Callback
	public Object[] scan(Context context, Arguments args) {
		ItemStack scannerInput = inventoryScanner.getStackInSlot(0);

		if (scannerInput.getItem() instanceof PrintedPage) {
			return readPrintedPage(scannerInput);
		}

		return new Object[] { false };
	}

	@Callback
	public Object[] scanBook(Context context, Arguments args) {
		ItemStack scannerInput = inventoryScanner.getStackInSlot(0);

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
		if (!OpenPrinter.cfg.enableNameTag)
			throw new Exception("Name Tag printing is disabled.");

		if (inventoryMaterials.getStackInSlot(BLACK_INK_SLOT).isEmpty())
			throw new Exception("Please load Black Ink.");

		if (!inventoryMaterials.getStackInSlot(PAPER_SLOT).getItem().equals(Items.NAME_TAG))
			throw new Exception("Please load Name Tags.");


		int x = getEmptyOutputSlot();
		if(x == -1)
			throw new Exception("no empty output slot");

		ItemStack output = new ItemStack(Items.NAME_TAG);
		output.setTagCompound(new NBTTagCompound());
		NBTTagCompound nameTag = new NBTTagCompound();
		nameTag.setString("Name", args.checkString(0));
		output.getTagCompound().setTag("display", nameTag);

		damageMaterial(BLACK_INK_SLOT, 1);
		damageMaterial(PAPER_SLOT, 1);

		return new Object[]{ true };
	}

	private void damageMaterial(short materialSlot, int damage){
		inventoryMaterials.getStackInSlot(materialSlot).setItemDamage(inventoryMaterials.getStackInSlot(materialSlot).getItemDamage() + damage);
		if(inventoryMaterials.getStackInSlot(materialSlot).getItemDamage() >= inventoryMaterials.getStackInSlot(materialSlot).getMaxDamage()) {
			inventoryMaterials.setStackInSlot(materialSlot, ItemStack.EMPTY);
		}
	}

	private int getEmptyOutputSlot(){
		for(int slot = 0; slot < inventoryOutput.getSlots(); slot++)
			if(inventoryOutput.getStackInSlot(slot).isEmpty())
				return slot;

		return -1;
	}


	@Callback
	public Object[] print(Context context, Arguments args) throws Exception {
		int copies = args.optInteger(0, 1);
		int copiesDone = 0;

		for (int i = 0; i < copies; i++) {
			if((inventoryMaterials.getStackInSlot(BLACK_INK_SLOT).isEmpty() && inventoryMaterials.getStackInSlot(COLOR_INK_SLOT).isEmpty())) //No color ink
				throw new Exception("Please load Ink.");

			if(inventoryMaterials.getStackInSlot(PAPER_SLOT).isEmpty())
				throw new Exception("Please load Paper.");

			int x = getEmptyOutputSlot();

			if(x == -1)
				throw new Exception("no empty output slot");

			ItemStack output = new ItemStack(ContentRegistry.printedPage);
			output.setTagCompound(new NBTTagCompound());
			if(pageTitle.length() > 0) {
				output.getTagCompound().setString("pageTitle", pageTitle);
				output.setStackDisplayName(pageTitle);
			}
			output.getTagCompound().setDouble("version", PrinterFormatVersion);
			int iter = 0;
			for (String s : lines) {
				output.getTagCompound().setString("line"+iter, lines.get(iter) + "∞" + colors.get(iter) + "∞" + align.get(iter));

				if (colors.get(iter) != 0x000000) {
					damageMaterial(COLOR_INK_SLOT, 1);
				} else {
					damageMaterial(BLACK_INK_SLOT, 1);
				}
				if(lines.get(iter).matches(".*§[0-9a-f].*")) {
					Pattern regex = Pattern.compile("§[0-9a-f]*");
					Matcher matcher = regex.matcher(lines.get(iter));
					while (matcher.find())
						damageMaterial(COLOR_INK_SLOT, 1);
				}
				iter++;
			}

			damageMaterial(PAPER_SLOT, 1);

			inventoryOutput.setStackInSlot(x, output);

			copiesDone++;

		}

		pageTitle = "";
		lines.clear();
		colors.clear();
		align.clear();

		return new Object[] { copiesDone == copies };
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
		if(!inventoryMaterials.getStackInSlot(2).isEmpty()) {
			if (inventoryMaterials.getStackInSlot(2).getItem() instanceof PrinterPaperRoll) {
				return new Object[] { 256 - inventoryMaterials.getStackInSlot(2).getItemDamage() };
			} else {
				return new Object[] { inventoryMaterials.getStackInSlot(2).getCount() };
			}
		} else {
			return new Object[] { false };
		}
	}

	@Callback
	public Object[] getBlackInkLevel(Context context, Arguments args) { 
		if(!inventoryMaterials.getStackInSlot(0).isEmpty()) {
			if (inventoryMaterials.getStackInSlot(0).getItem() instanceof PrinterInkBlack) {
				return new Object[] { OpenPrinter.cfg.printerInkUse - inventoryMaterials.getStackInSlot(0).getItemDamage()};
			} else {
				return new Object[] { false };
			}
		} else {
			return new Object[] { false };
		}
	}

	@Callback
	public Object[] getColorInkLevel(Context context, Arguments args) { 
		if(!inventoryMaterials.getStackInSlot(1).isEmpty()) {
			if (inventoryMaterials.getStackInSlot(1).getItem() instanceof PrinterInkColor) {
				return new Object[] { OpenPrinter.cfg.printerInkUse - inventoryMaterials.getStackInSlot(1).getItemDamage() };
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
	public void onMessage(Message arg0) {
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||  super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(EnumFacing.UP.equals(facing))
				return (T) inventoryScanner;
			else if(EnumFacing.DOWN.equals(facing))
				return (T) inventoryOutput;
			else
				return (T) inventoryMaterials;
		}

		return super.getCapability(capability, facing);
	}

}
