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
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.MapData;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.openprinter.ContentRegistry;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.books.BiblioCraftBigBook;
import pcl.openprinter.books.VanillaBook;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.ItemFolder;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.inventory.FolderInventory;
import pcl.openprinter.util.ItemUtils;
import pcl.openprinter.util.CharacterWidth;

import javax.annotation.Nonnull;

import static pcl.openprinter.tileentity.PrinterTE.InventoryMaterial.*;

public class PrinterTE extends TileEntity implements ITickable, Environment {
	private static final double PrinterFormatVersion = 2.0;
	private static final int MAX_BOOK_PAGES = 100;
	private static final int PAGE_PRINT_TICKS = 10;
	private static final int MAX_BOOK_PRINT_TICKS = 200;
	private static final int MAX_PRINT_QUEUE = 32;
	private static final int MAP_COLOR_INK_COST = 20;
	private static final int MAX_PRINT_COPIES = 64;

	private ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	private boolean addedToNetwork = false;

	private ItemStackHandler inventoryOutput = new InventoryOutput();
	private ItemStackHandler inventoryScanner = new InventoryScanner();
	private ItemStackHandler inventoryMaterials = new InventoryMaterial();

	private List<String> lines = new ArrayList<>();
	private List<String> align = new ArrayList<>();
	private List<Integer> colors = new ArrayList<>();
	private String pageTitle = "";
	private final Deque<PrintJob> printJobs = new ArrayDeque<>();

	private UUID uuid = UUID.randomUUID();

	public PrinterTE() {}

	private static final class PrintJob {
		private final UUID id;
		private final List<String> lines;
		private final List<String> align;
		private final List<Integer> colors;
		private final String title;
		private final int copies;
		private final boolean book;
		private final boolean image;
		private final MapPrintImage mapImage;
		private final boolean sign;
		private final String author;
		private final int outputPages;
		private int copiesDone;
		private int processingTime;

		private PrintJob(List<String> lines, List<String> align, List<Integer> colors,
						String title, int copies, boolean book, int outputPages){
			this(UUID.randomUUID(), lines, align, colors, title, copies, book, false,
					false, null, "OpenPrinter", outputPages, 0, 0);
		}

		private PrintJob(List<String> lines, List<String> align, List<Integer> colors,
						String title, int copies, boolean book, int outputPages,
						boolean sign, String author){
			this(UUID.randomUUID(), lines, align, colors, title, copies, book, sign,
					 false, null, author, outputPages, 0, 0);
		}

		private PrintJob(MapPrintImage mapImage, int copies){
			this(UUID.randomUUID(), Collections.<String>emptyList(), Collections.<String>emptyList(),
					Collections.<Integer>emptyList(), mapImage.title(), copies, false, false,
					true, mapImage, "OpenPrinter", 1, 0, 0);
		}

		private PrintJob(UUID id, List<String> lines, List<String> align, List<Integer> colors,
						String title, int copies, boolean book, boolean sign, String author, int outputPages,
						int copiesDone, int processingTime){
			this(id, lines, align, colors, title, copies, book, sign, false, null, author,
					outputPages, copiesDone, processingTime);
		}

		private PrintJob(UUID id, List<String> lines, List<String> align, List<Integer> colors,
						String title, int copies, boolean book, boolean sign, boolean image,
						MapPrintImage mapImage, String author, int outputPages,
						int copiesDone, int processingTime){
			this.id = id;
			this.lines = new ArrayList<>(lines);
			this.align = new ArrayList<>(align);
			this.colors = new ArrayList<>(colors);
			this.title = title;
			this.copies = copies;
			this.book = book;
			this.image = image;
			this.mapImage = mapImage;
			this.sign = sign;
			this.author = author;
			this.outputPages = Math.max(1, outputPages);
			this.copiesDone = copiesDone;
			this.processingTime = processingTime;
		}

		private int duration(){
			if(image)
				return PAGE_PRINT_TICKS;
			if(!book)
				return PAGE_PRINT_TICKS;

			return Math.min(MAX_BOOK_PRINT_TICKS,
					PAGE_PRINT_TICKS + (int) Math.ceil((outputPages - 1) *
							(double) (MAX_BOOK_PRINT_TICKS - PAGE_PRINT_TICKS) / (MAX_BOOK_PAGES - 1)));
		}

		private NBTTagCompound writeToNBT(){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setUniqueId("id", id);
			tag.setString("title", title);
			tag.setInteger("copies", copies);
			tag.setInteger("copiesDone", copiesDone);
			tag.setInteger("processingTime", processingTime);
			tag.setBoolean("book", book);
			tag.setBoolean("image", image);
			tag.setBoolean("sign", sign);
			tag.setString("author", author);
			tag.setInteger("outputPages", outputPages);
			if(mapImage != null)
				tag.setTag("mapImage", mapImage.writeToNBT());

			NBTTagList savedLines = new NBTTagList();
			for(int i = 0; i < lines.size(); i++){
				NBTTagCompound line = new NBTTagCompound();
				line.setString("text", lines.get(i));
				line.setString("align", align.get(i));
				line.setInteger("color", colors.get(i));
				savedLines.appendTag(line);
			}
			tag.setTag("lines", savedLines);
			return tag;
		}

		private static PrintJob readFromNBT(NBTTagCompound tag){
			List<String> lines = new ArrayList<>();
			List<String> align = new ArrayList<>();
			List<Integer> colors = new ArrayList<>();
			NBTTagList savedLines = tag.getTagList("lines", 10);
			for(int i = 0; i < savedLines.tagCount(); i++){
				NBTTagCompound line = savedLines.getCompoundTagAt(i);
				lines.add(line.getString("text"));
				align.add(line.getString("align"));
				colors.add(line.getInteger("color"));
			}

			UUID id = tag.hasUniqueId("id") ? tag.getUniqueId("id") : UUID.randomUUID();
			if(tag.getBoolean("image")) {
				MapPrintImage image = MapPrintImage.readFromNBT(tag.getCompoundTag("mapImage"));
				return new PrintJob(id, lines, align, colors, image.title(),
						Math.max(1, tag.getInteger("copies")), false, false, true, image,
						"OpenPrinter", 1,
						tag.getInteger("copiesDone"), tag.getInteger("processingTime"));
			}

			return new PrintJob(id, lines, align, colors, tag.getString("title"),
					tag.getInteger("copies"), tag.getBoolean("book"), tag.getBoolean("sign"),
					tag.hasKey("author") ? tag.getString("author") : "OpenPrinter", tag.getInteger("outputPages"),
					tag.getInteger("copiesDone"), tag.getInteger("processingTime"));
		}
	}

	class InventoryOutput extends ItemStackHandler {
		InventoryOutput(){
			super(9);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return stack.getItem() instanceof PrintedPage
					|| stack.getItem() instanceof ItemFolder
					|| stack.getItem().equals(Items.WRITABLE_BOOK)
					|| stack.getItem().equals(Items.WRITTEN_BOOK)
					|| stack.getItem().equals(Items.FILLED_MAP);
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Override
		protected void onContentsChanged(int slot){
			markDirty();
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
						|| stack.getItem().equals(Items.MAP)
						|| stack.getItem().equals(Items.WRITABLE_BOOK)
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

		if((world != null && world.isRemote) || printJobs.isEmpty())
			return;

		PrintJob job = printJobs.peekFirst();
		if(getPrintJobBlocker(job) != null){
			job.processingTime = 0;
			return;
		}

		if(++job.processingTime < job.duration())
			return;

		try {
			if(job.image){
				printMapCopy(job);
			}
			else if(job.book){
				writeBookCopy(job);
				if(job.copiesDone + 1 >= job.copies)
					ejectBook(job);
			}
			else
				printPrintedPageCopy(job);
		}
		catch(Exception e){
			// Keep the job queued if a material or output changed between the
			// blocker check and completion. It will be retried on a later tick.
			job.processingTime = 0;
			return;
		}

		job.processingTime = 0;
		job.copiesDone++;
		if(job.copiesDone >= job.copies)
			printJobs.removeFirst();
		markDirty();
	}

	private String getPrintJobBlocker(PrintJob job){
		if(job.image){
			if(!inventoryMaterials.getStackInSlot(PAPER_SLOT).getItem().equals(Items.MAP))
				return "Please load Empty Maps.";
			if(inventoryMaterials.getStackInSlot(COLOR_INK_SLOT).isEmpty()
					|| getColorInkLevelValue() < MAP_COLOR_INK_COST)
				return "Please load enough Color Ink.";
			if(getEmptyOutputSlot() == -1)
				return "no empty output slot";
			return null;
		}

		if(job.book){
			ItemStack book = inventoryMaterials.getStackInSlot(PAPER_SLOT);
			if(!(book.getItem() instanceof ItemWritableBook))
				return "Please load a writable book.";
			if(inventoryMaterials.getStackInSlot(BLACK_INK_SLOT).isEmpty())
				return "Please load Black Ink.";
			if(getBookPageCount(book) + getBookPages(job.lines).size() > MAX_BOOK_PAGES)
				return "book is full";
			if(job.copiesDone + 1 >= job.copies && getEmptyOutputSlot() == -1)
				return "no empty output slot for finished book";
			return null;
		}

		if(inventoryMaterials.getStackInSlot(BLACK_INK_SLOT).isEmpty()
				|| inventoryMaterials.getStackInSlot(COLOR_INK_SLOT).isEmpty())
			return "Please load Ink.";
		if(!inventoryMaterials.getStackInSlot(PAPER_SLOT).getItem().equals(Items.PAPER))
			return "Please load Paper.";
		if(!canStorePrintedPage())
			return getFolderOutputSlot() == -1
					? "no empty output slot"
					: "no empty output slot or folder space";
		return null;
	}

	@Callback(doc = "function():table; -- returns the printer queue")
	public Object[] getPrintQueue(Context context, Arguments args) {
		List<Map<String, Object>> queue = new ArrayList<>();
		int index = 1;
		for(PrintJob job : printJobs){
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("index", index);
			entry.put("id", job.id.toString());
			entry.put("type", job.image ? "map" : (job.book ? "book" : "page"));
			entry.put("pages", job.outputPages);
			entry.put("copies", job.copies);
			entry.put("copiesCompleted", job.copiesDone);
			entry.put("copiesRemaining", job.copies - job.copiesDone);
			entry.put("progress", index == 1 ? job.processingTime : 0);
			entry.put("duration", job.duration());
			if(job.image){
				entry.put("title", job.mapImage.title());
				entry.put("width", job.mapImage.sourceWidth());
				entry.put("height", job.mapImage.sourceHeight());
			}

			if(index == 1){
				String blocker = getPrintJobBlocker(job);
				if(blocker != null){
					entry.put("status", "waiting");
					entry.put("blocker", blocker);
				}
				else
					entry.put("status", job.processingTime > 0 ? "printing" : "queued");
			}
			else
				entry.put("status", "queued");

			queue.add(entry);
			index++;
		}
		return new Object[] { queue };
	}

	@Callback(doc = "function():Integer; -- cancels all queued print jobs")
	public Object[] clearPrintQueue(Context context, Arguments args) {
		int cleared = printJobs.size();
		if(cleared > 0){
			printJobs.clear();
			markDirty();
		}
		return new Object[] { cleared };
	}

	@Callback(doc = "function(Integer:index):boolean; -- cancels one queued print job")
	public Object[] removePrintJob(Context context, Arguments args) throws Exception {
		int index = args.checkInteger(0);
		if(index < 1 || index > printJobs.size())
			return new Object[] { false, "invalid queue index" };

		Iterator<PrintJob> iterator = printJobs.iterator();
		for(int i = 1; i < index; i++)
			iterator.next();
		iterator.next();
		iterator.remove();
		markDirty();
		return new Object[] { true };
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
	public void readFromNBT(NBTTagCompound nbt) {
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

		if(nbt.hasUniqueId("printerUUID"))
			setUniqueId(nbt.getUniqueId("printerUUID"));

		if(nbt.hasKey("buffers"))
			readBuffersFromNBT(nbt.getCompoundTag("buffers"));

		printJobs.clear();
		NBTTagList savedJobs = nbt.getTagList("printJobs", 10);
		for(int i = 0; i < savedJobs.tagCount() && printJobs.size() < MAX_PRINT_QUEUE; i++){
			PrintJob job = PrintJob.readFromNBT(savedJobs.getCompoundTagAt(i));
			if(job.copies > job.copiesDone)
				printJobs.addLast(job);
		}
	}

	private NBTTagCompound writeBuffersToNBT(NBTTagCompound nbt){
		for(int i=0; i < lines.size(); i++){
			NBTTagCompound lineNBT = new NBTTagCompound();
			lineNBT.setString("text", lines.get(i));
			lineNBT.setString("align", align.get(i));
			lineNBT.setInteger("color", colors.get(i));
			nbt.setTag("line"+i, lineNBT);
		}

		return nbt;
	}

	private void readBuffersFromNBT(NBTTagCompound nbt){
		lines.clear();
		align.clear();
		colors.clear();
		for(int i=0; nbt.hasKey("line"+i); i++){
			NBTTagCompound lineNBT = nbt.getCompoundTag("line"+i);
			lines.add(lineNBT.getString("text"));
			align.add(lineNBT.getString("align"));
			colors.add(lineNBT.getInteger("color"));
		}
	}

	@Override
	@Nonnull
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

		nbt.setTag("buffers", writeBuffersToNBT(new NBTTagCompound()));

		NBTTagList savedJobs = new NBTTagList();
		for(PrintJob job : printJobs)
			savedJobs.appendTag(job.writeToNBT());
		nbt.setTag("printJobs", savedJobs);

		nbt.setUniqueId("printerUUID", getUniqueId());

		return super.writeToNBT(nbt);
	}

	@Override
	@Nonnull
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
	@Callback(doc = "function(Integer:lineNumber):String; -- scans the given line of a printed page")
	public Object[] scanLine(Context context, Arguments args) {
		if(!args.isInteger(0))
			return new Object[] { false, "first argument has to be a line number" };

		ItemStack scannedPage = inventoryScanner.getStackInSlot(0);

		if (scannedPage.getItem() instanceof PrintedPage && scannedPage.hasTagCompound()) {
			return new Object[] { scannedPage.getTagCompound().getString("line" + args.checkInteger(0)) };
		} else {
			return new Object[] { false };
		}
	}

	@Callback(doc = "function():table; -- scans a printed page")
	public Object[] scan(Context context, Arguments args) {
		ItemStack scannerInput = inventoryScanner.getStackInSlot(0);

		if (scannerInput.getItem() instanceof PrintedPage) {
			return readPrintedPage(scannerInput);
		}

		return new Object[] { false };
	}

	@Callback(doc = "function():table; -- scans a book")
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

		return new Object[] { outPageTitle, output, scanPageMeta(printedPage) };
	}

	private static Map<String, String> scanPageMeta(ItemStack scannerInput) {
		Map<String, String> output = new HashMap<>();
		if (scannerInput.getItem() instanceof PrintedPage) {
			if(scannerInput.getTagCompound().hasUniqueId("printerUUID"))
				output.put("printerUUID", scannerInput.getTagCompound().getUniqueId("printerUUID").toString());
			if(scannerInput.getTagCompound().hasUniqueId("pageUUID"))
				output.put("pageUUID", scannerInput.getTagCompound().getUniqueId("pageUUID").toString());
		}

		return output;
	}

	@Callback(doc = "function(String:title):boolean; -- prints a name tag")
	public Object[] printTag(Context context, Arguments args) throws Exception {
		if (!OpenPrinter.cfg.enableNameTag)
			throw new Exception("Name Tag printing is disabled.");

		if(!args.isString(0))
			throw new Exception("First argument has to be a valid String");

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

		inventoryOutput.setStackInSlot(x, output);

		return new Object[]{ true };
	}

	private void damageMaterial(short materialSlot, int damage){
		ItemStack slotItem = inventoryMaterials.getStackInSlot(materialSlot);
		if(slotItem.getCount() > 1){
			inventoryMaterials.extractItem(materialSlot, 1, false);
			return;
		}

		slotItem.setItemDamage(slotItem.getItemDamage() + damage);
		if(slotItem.getItemDamage() >= slotItem.getMaxDamage()) {
			inventoryMaterials.setStackInSlot(materialSlot, ItemStack.EMPTY);
		}
		else
			inventoryMaterials.setStackInSlot(materialSlot, slotItem);
	}

	private int getColorInkLevelValue(){
		if(inventoryMaterials.getStackInSlot(COLOR_INK_SLOT).isEmpty())
			return 0;
		return OpenPrinter.cfg.printerInkUse
				- inventoryMaterials.getStackInSlot(COLOR_INK_SLOT).getItemDamage();
	}

	private int getFolderOutputSlot(){
		for(int slot = 0; slot < inventoryOutput.getSlots(); slot++)
			if(inventoryOutput.getStackInSlot(slot).getItem() instanceof ItemFolder)
				return slot;

		return -1;
	}

	private int getEmptyFolderSlot(int folderSlot){
		FolderInventory folder = new FolderInventory(inventoryOutput.getStackInSlot(folderSlot));
		for(int slot = 0; slot < folder.getSlots(); slot++)
			if(folder.getStackInSlot(slot).isEmpty())
				return slot;

		return -1;
	}

	private int getFolderOutputSlotWithSpace(){
		for(int slot = 0; slot < inventoryOutput.getSlots(); slot++)
			if(inventoryOutput.getStackInSlot(slot).getItem() instanceof ItemFolder
					&& getEmptyFolderSlot(slot) != -1)
				return slot;

		return -1;
	}

	private boolean canStorePrintedPage(){
		// Prefer folders, but retain ordinary output slots as overflow when all
		// loaded folders are full.
		return getFolderOutputSlotWithSpace() != -1 || getEmptyOutputSlot() != -1;
	}

	private void storePrintedPage(ItemStack page){
		int folderSlot = getFolderOutputSlotWithSpace();
		if(folderSlot != -1){
			FolderInventory folder = new FolderInventory(inventoryOutput.getStackInSlot(folderSlot));
			int emptySlot = getEmptyFolderSlot(folderSlot);
			if(emptySlot == -1)
				throw new IllegalStateException("folder is full");

			folder.setStackInSlot(emptySlot, page);
			markDirty();
			return;
		}

		int outputSlot = getEmptyOutputSlot();
		if(outputSlot == -1)
			throw new IllegalStateException("no empty output slot");

		inventoryOutput.setStackInSlot(outputSlot, page);
	}

	private int getEmptyOutputSlot(){
		for(int slot = 0; slot < inventoryOutput.getSlots(); slot++)
			if(inventoryOutput.getStackInSlot(slot).isEmpty())
				return slot;

		return -1;
	}

	private boolean isWritingBook(){
		return inventoryMaterials.getStackInSlot(PAPER_SLOT).getItem() instanceof ItemWritableBook;
	}

	private int getBookPageCount(ItemStack book){
		if(!book.hasTagCompound())
			return 0;

		return book.getTagCompound().getTagList("pages", 8).tagCount();
	}

	private List<String> getBookPages(List<String> sourceLines){
		StringBuilder text = new StringBuilder();
		for(int i = 0; i < sourceLines.size(); i++){
			if(i > 0)
				text.append('\n');
			text.append(sourceLines.get(i));
		}

		List<String> pages = new ArrayList<>();
		if(text.length() == 0){
			pages.add("");
			return pages;
		}

		for(int start = 0; start < text.length(); start += 256)
			pages.add(text.substring(start, Math.min(start + 256, text.length())));

		return pages;
	}

	private void validateBookLines(List<String> sourceLines, List<Integer> sourceColors) throws Exception {
		for(int i = 0; i < sourceLines.size(); i++){
			if(sourceColors.get(i) != 0x000000 || sourceLines.get(i).indexOf('§') != -1)
				throw new Exception("Books only support plain black text.");
		}
	}

	private void writeBookCopy(PrintJob job) throws Exception {
		ItemStack book = inventoryMaterials.getStackInSlot(PAPER_SLOT);
		if(!(book.getItem() instanceof ItemWritableBook))
			throw new Exception("Please load a writable book.");

		validateBookLines(job.lines, job.colors);
		List<String> pages = getBookPages(job.lines);
		if(getBookPageCount(book) + pages.size() > MAX_BOOK_PAGES)
			throw new Exception("book is full");

		for(String page : pages){
			NBTTagCompound tag = book.hasTagCompound() ? book.getTagCompound() : new NBTTagCompound();
			NBTTagList bookPages = tag.getTagList("pages", 8);
			bookPages.appendTag(new NBTTagString(page));
			tag.setTag("pages", bookPages);
			book.setTagCompound(tag);
		}

		for(int line = 0; line < job.lines.size(); line++)
			damageMaterial(BLACK_INK_SLOT, 1);

		markDirty();
	}

	private void ejectBook(PrintJob job) throws Exception {
		int outputSlot = getEmptyOutputSlot();
		if(outputSlot == -1)
			throw new Exception("no empty output slot for finished book");

		ItemStack book = inventoryMaterials.getStackInSlot(PAPER_SLOT);
		if(!(book.getItem() instanceof ItemWritableBook))
			throw new Exception("Please load a writable book.");
		if(job.sign)
			book = createSignedBook(book, job.title, job.author);

		inventoryMaterials.setStackInSlot(PAPER_SLOT, ItemStack.EMPTY);
		inventoryOutput.setStackInSlot(outputSlot, book);
		markDirty();
	}

	private void printMapCopy(PrintJob job) throws Exception {
		if(job.mapImage == null)
			throw new Exception("map image is missing");
		if(!inventoryMaterials.getStackInSlot(PAPER_SLOT).getItem().equals(Items.MAP))
			throw new Exception("Please load Empty Maps.");
		if(getColorInkLevelValue() < MAP_COLOR_INK_COST)
			throw new Exception("Please load enough Color Ink.");
		int outputSlot = getEmptyOutputSlot();
		if(outputSlot == -1)
			throw new Exception("no empty output slot");

		ItemStack output = ItemMap.setupNewMap(world, getPos().getX(), getPos().getZ(),
				(byte) 0, false, false);
		MapData mapData = (MapData) world.loadData(MapData.class, "map_" + output.getMetadata());
		if(mapData == null || mapData.colors.length != MapPrintImage.SIZE * MapPrintImage.SIZE)
			throw new Exception("could not create map data");
		// 1.12 has no locked-map flag. A dimension mismatch makes vanilla's
		// ItemMap.onUpdate skip its terrain scan, preserving the printed pixels
		// while keeping the item itself a normal filled map.
		mapData.dimension = Integer.MIN_VALUE;
		mapData.trackingPosition = false;
		System.arraycopy(job.mapImage.colors(), 0, mapData.colors, 0, mapData.colors.length);
		mapData.markDirty();

		if(job.mapImage.title().length() > 0)
			output.setStackDisplayName(job.mapImage.title());
		damageMaterial(COLOR_INK_SLOT, MAP_COLOR_INK_COST);
		damageMaterial(PAPER_SLOT, 1);
		inventoryOutput.setStackInSlot(outputSlot, output);
		markDirty();
	}

	private ItemStack createSignedBook(ItemStack writableBook, String title, String author){
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = writableBook.hasTagCompound()
				? writableBook.getTagCompound().getTagList("pages", 8)
				: new NBTTagList();
		NBTTagList signedPages = new NBTTagList();
		for(int i = 0; i < pages.tagCount(); i++)
			signedPages.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(
					new TextComponentString(pages.getStringTagAt(i)))));

		tag.setTag("pages", signedPages);
		tag.setString("title", title);
		tag.setString("author", author);
		tag.setBoolean("resolved", true);

		ItemStack signedBook = new ItemStack(Items.WRITTEN_BOOK);
		signedBook.setTagCompound(tag);
		return signedBook;
	}

	private void printPrintedPageCopy(PrintJob job) throws Exception {
		if(!canStorePrintedPage())
			throw new Exception("no empty output slot or folder space");

		ItemStack output = new ItemStack(ContentRegistry.printedPage);
		output.setTagCompound(new NBTTagCompound());
		if(job.title.length() > 0) {
			output.getTagCompound().setString("pageTitle", job.title);
			output.setStackDisplayName(job.title);
		}
		output.getTagCompound().setDouble("version", PrinterFormatVersion);
		for(int iter = 0; iter < job.lines.size(); iter++) {
			output.getTagCompound().setString("line"+iter,
					job.lines.get(iter) + "∞" + job.colors.get(iter) + "∞" + job.align.get(iter));

			if (job.colors.get(iter) != 0x000000)
				damageMaterial(COLOR_INK_SLOT, 1);
			else
				damageMaterial(BLACK_INK_SLOT, 1);
			if(job.lines.get(iter).matches(".*§[0-9a-f].*")) {
				Pattern regex = Pattern.compile("§[0-9a-f]*");
				Matcher matcher = regex.matcher(job.lines.get(iter));
				while (matcher.find())
					damageMaterial(COLOR_INK_SLOT, 1);
			}
		}

		damageMaterial(PAPER_SLOT, 1);

		output.getTagCompound().setUniqueId("printerUUID", getUniqueId());
		output.getTagCompound().setUniqueId("pageUUID", UUID.randomUUID());
		storePrintedPage(output);
	}

	private void clearPrintBuffer(){
		pageTitle = "";
		lines.clear();
		colors.clear();
		align.clear();
	}

	private Object[] queuePrint(int copies, boolean sign, String title, String author) throws Exception {
		if(copies <= 0){
			clearPrintBuffer();
			return new Object[] { true };
		}
		if(printJobs.size() >= MAX_PRINT_QUEUE)
			throw new Exception("print queue is full");

		boolean book = isWritingBook();
		if(sign && !book)
			throw new Exception("printAndSign requires a writable book.");

		PrintJob job = new PrintJob(lines, align, colors, title, copies, book,
				book ? getBookPages(lines).size() : 1, sign, author);
		if(book){
			validateBookLines(job.lines, job.colors);
			if(getBookPageCount(inventoryMaterials.getStackInSlot(PAPER_SLOT))
					+ job.outputPages * copies > MAX_BOOK_PAGES)
				throw new Exception("book is full");
		}
		else {
			String blocker = getPrintJobBlocker(job);
			if(blocker != null)
				throw new Exception(blocker);
		}

		printJobs.addLast(job);
		clearPrintBuffer();
		markDirty();
		return new Object[] { true };
	}

	@Callback(doc = "function([Integer:copyCount]):boolean; -- queues the current page or book for printing")
	public Object[] print(Context context, Arguments args) throws Exception {
		return queuePrint(args.optInteger(0, 1), false, pageTitle, "OpenPrinter");
	}

	@Callback(doc = "function(String:title[, String:author][, Integer:copyCount]):boolean; -- queues and seals a writable book")
	public Object[] printAndSign(Context context, Arguments args) throws Exception {
		String title = args.checkString(0);
		String author = "OpenPrinter";
		int copies = 1;
		if(args.count() > 1){
			if(args.isString(1))
				author = args.checkString(1);
			else
				copies = args.checkInteger(1);
		}
		if(args.count() > 2)
			copies = args.checkInteger(2);

		if(title.length() == 0)
			throw new Exception("Book title cannot be empty.");
		if(title.length() > 32)
			throw new Exception("Book title cannot be longer than 32 characters.");
		if(author.length() == 0)
			throw new Exception("Book author cannot be empty.");

		return queuePrint(copies, true, title, author);
	}

	private int getImageCopies(Map<?, ?> options) throws Exception {
		Object value = options.get("copies");
		if(value == null)
			return 1;
		if(!(value instanceof Number))
			throw new Exception("copies must be a number");
		int copies = ((Number) value).intValue();
		if(copies < 1 || copies > MAX_PRINT_COPIES)
			throw new Exception("copies must be between 1 and " + MAX_PRINT_COPIES);
		return copies;
	}

	private Object[] queueImage(MapPrintImage image, Map<?, ?> options) throws Exception {
		if(printJobs.size() >= MAX_PRINT_QUEUE)
			throw new Exception("print queue is full");
		PrintJob job = new PrintJob(image, getImageCopies(options));
		String blocker = getPrintJobBlocker(job);
		if(blocker != null)
			throw new Exception(blocker);
		printJobs.addLast(job);
		markDirty();
		return new Object[] { job.id.toString() };
	}

	@Callback(doc = "function(table:rgbImage[, table:options]):String; -- queues an RGB image for an empty map")
	public Object[] printMap(Context context, Arguments args) throws Exception {
		Map<?, ?> image = args.checkTable(0);
		Map<?, ?> options = args.count() > 1 ? args.checkTable(1) : Collections.emptyMap();
		return queueImage(MapPrintImage.parse(image, options), options);
	}

	@Callback(doc = "function(string:imageData[, table:options]):String; -- queues a PNG, JPEG, GIF, or BMP image")
	public Object[] printImage(Context context, Arguments args) throws Exception {
		byte[] image = args.checkByteArray(0);
		Map<?, ?> options = args.count() > 1 ? args.checkTable(1) : Collections.emptyMap();
		return queueImage(MapPrintImage.decode(image, options), options);
	}

	@Callback(doc = "function(String:text[, Integer:color, String:alignment]):boolean; -- prints a line to the printer buffer")
	public Object[] writeln(Context context, Arguments args) throws Exception{
		if(!args.isString(0))
			throw new Exception("First argument has to be a valid String");


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

	@Callback(doc = "function(String:text):boolean; -- sets the page title")
	public Object[] setTitle(Context context, Arguments args) {
		pageTitle = args.optString(0, "");
		return new Object[] { true };
	}

	@Callback(doc = "function():Integer; -- gets the current paper level")
	public Object[] getPaperLevel(Context context, Arguments args) { 
		if(inventoryMaterials.getStackInSlot(PAPER_SLOT).isEmpty())
			return new Object[] { 0 };

		if(isWritingBook())
			return new Object[] { MAX_BOOK_PAGES - getBookPageCount(inventoryMaterials.getStackInSlot(PAPER_SLOT)) };

		if (inventoryMaterials.getStackInSlot(PAPER_SLOT).getItem() instanceof PrinterPaperRoll) {
			return new Object[] { 256 - inventoryMaterials.getStackInSlot(PAPER_SLOT).getItemDamage() };
		} else {
			return new Object[] { inventoryMaterials.getStackInSlot(PAPER_SLOT).getCount() };
		}
	}

	@Callback(doc = "function():Integer; -- gets the current black ink level")
	public Object[] getBlackInkLevel(Context context, Arguments args) { 
		if(inventoryMaterials.getStackInSlot(BLACK_INK_SLOT).isEmpty())
			return new Object[] { 0 };

		return new Object[] { OpenPrinter.cfg.printerInkUse - inventoryMaterials.getStackInSlot(BLACK_INK_SLOT).getItemDamage()};
	}

	@Callback(doc = "function():Integer; -- gets the current color ink level")
	public Object[] getColorInkLevel(Context context, Arguments args) { 
		if(inventoryMaterials.getStackInSlot(COLOR_INK_SLOT).isEmpty())
			return new Object[] { false };

		return new Object[] { getColorInkLevelValue() };
	}

	@Callback(doc = "function(String:input):Integer; -- gets the character count for the given String", direct = true)
	public Object[] charCount(Context context, Arguments args) {
		return new Object[] { args.checkString(0).replaceAll("(?:§[0-9a-fk-or])+", "").length() };
	}

        @Callback(doc = "function(String:input):Integer; -- gets the pixel width of a given String", direct = true)
        public Object[] width(Context context, Arguments args) {
		return new Object[] { CharacterWidth.calculateWidth(args.checkString(0)) };
        }

        @Callback(doc = "function():Integer; -- returns the maximum allowed width of a line", direct = true)
        public Object[] maxWidth(Context context, Arguments args) {
		return new Object[] { 164 };
        }

	@Callback(doc = "function():boolean; -- clears the printer buffer")
	public Object[] clear(Context context, Arguments args) {
		clearPrintBuffer();
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

	public UUID getUniqueId(){
		return uuid;
	}

	public void setUniqueId(UUID uniqueId){
		this.uuid = uniqueId;
	}

}
