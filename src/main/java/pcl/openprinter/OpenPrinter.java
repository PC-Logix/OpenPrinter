package pcl.openprinter;

import li.cil.oc.api.FileSystem;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(OpenPrinter.MOD_ID)
public final class OpenPrinter {
    public static final String MOD_ID = "openprinter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String TOOLS_DISK_LABEL = "OpenPrinter";
    private static final String[] TOOLS_DISK_PROGRAMS = {
            "print", "printercopypage", "printerstatus", "xerox", "printmap"
    };
    private static final String[] LUA_ARCHITECTURES = {"Lua 5.2", "Lua 5.3", "LuaJ"};
    private static ItemStack toolsDisk = ItemStack.EMPTY;

    public static final Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<Block, DeviceBlock> PRINTER = block("printer", DeviceBlock.Kind.PRINTER);
    public static final DeferredHolder<Block, DeviceBlock> SHREDDER = block("shredder", DeviceBlock.Kind.SHREDDER);
    public static final DeferredHolder<Block, DeviceBlock> FILE_CABINET = block("filecabinet", DeviceBlock.Kind.FILE_CABINET);
    public static final DeferredHolder<Block, DeviceBlock> BRIEFCASE = block("briefcase", DeviceBlock.Kind.BRIEFCASE);

    public static final DeferredHolder<Item, Item> PRINTED_PAGE = ITEMS.register("printed_page",
            () -> new PrintedPageItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BLACK_INK = ITEMS.register("printer_ink_black",
            () -> new Item(new Item.Properties().stacksTo(1).durability(400)));
    public static final DeferredHolder<Item, Item> COLOR_INK = ITEMS.register("printer_ink_color",
            () -> new Item(new Item.Properties().stacksTo(1).durability(400)));
    public static final DeferredHolder<Item, Item> PAPER_SHREDS = ITEMS.register("paper_shreds",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> FOLDER = ITEMS.register("folder",
            () -> new PortableFolderItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PrinterBlockEntity>> PRINTER_BE =
            BLOCK_ENTITIES.register("printer", () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, PRINTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShredderBlockEntity>> SHREDDER_BE =
            BLOCK_ENTITIES.register("shredder", () -> BlockEntityType.Builder.of(ShredderBlockEntity::new, SHREDDER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StorageBlockEntity>> FILE_CABINET_BE =
            BLOCK_ENTITIES.register("filecabinet", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new StorageBlockEntity(pos, state, 30), FILE_CABINET.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StorageBlockEntity>> BRIEFCASE_BE =
            BLOCK_ENTITIES.register("briefcase", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new StorageBlockEntity(pos, state, 18), BRIEFCASE.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<DeviceMenu>> DEVICE_MENU = MENUS.register("device",
            () -> IMenuTypeExtension.create(DeviceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<PortableStorageMenu>> PORTABLE_MENU = MENUS.register("portable_storage",
            () -> IMenuTypeExtension.create(PortableStorageMenu::new));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.openprinter"))
                    .icon(() -> new ItemStack(PRINTER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(PRINTER.get());
                        output.accept(SHREDDER.get());
                        output.accept(FILE_CABINET.get());
                        output.accept(BRIEFCASE.get());
                        output.accept(PRINTED_PAGE.get());
                        output.accept(BLACK_INK.get());
                        output.accept(COLOR_INK.get());
                        output.accept(PAPER_SHREDS.get());
                        output.accept(FOLDER.get());
                        if (!toolsDisk.isEmpty()) output.accept(toolsDisk.copy());
                    }).build());

    public OpenPrinter(IEventBus modBus, ModContainer container) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        MENUS.register(modBus);
        TABS.register(modBus);
        modBus.addListener(this::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, PrinterConfig.SPEC);
        modBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) ->
                event.enqueueWork(OpenPrinter::registerOpenComputersIntegration));
    }

    private static DeferredHolder<Block, DeviceBlock> block(String name, DeviceBlock.Kind kind) {
        DeferredHolder<Block, DeviceBlock> holder = BLOCKS.register(name, () -> {
            Block.Properties properties = Block.Properties.of().mapColor(MapColor.METAL)
                    .strength(2.5F).sound(SoundType.METAL);
            if (kind == DeviceBlock.Kind.PRINTER || kind == DeviceBlock.Kind.BRIEFCASE) {
                properties.noOcclusion();
            }
            return new DeviceBlock(kind, properties);
        });
        ITEMS.register(name, () -> kind == DeviceBlock.Kind.BRIEFCASE
                ? new PortableBlockItem(holder.get(), new Item.Properties().stacksTo(1))
                : new BlockItem(holder.get(), new Item.Properties()));
        return holder;
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerItemHandler(event, PRINTER_BE.get());
        registerItemHandler(event, SHREDDER_BE.get());
        registerItemHandler(event, FILE_CABINET_BE.get());
        registerItemHandler(event, BRIEFCASE_BE.get());
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), PRINTER_BE.get(),
                (blockEntity, side) -> blockEntity);
    }

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity & InventoryDevice>
    void registerItemHandler(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type,
                (blockEntity, side) -> blockEntity.itemHandler(side));
    }

    private void onServerTick(ServerTickEvent.Post event) {
        // Block entity tickers do the machine work; keeping the event hook makes later server jobs explicit.
    }

    private static void registerOpenComputersIntegration() {
        try {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(MOD_ID, "loot/printer/printercopy");
            toolsDisk = li.cil.oc.api.Items.registerFloppy(TOOLS_DISK_LABEL, location, DyeColor.GREEN,
                    () -> FileSystem.asReadOnly(FileSystem.fromResource(location)), true);
            for (String program : TOOLS_DISK_PROGRAMS) {
                li.cil.oc.api.IMC.registerProgramDiskLabel(program, TOOLS_DISK_LABEL, LUA_ARCHITECTURES);
            }
        } catch (RuntimeException exception) {
            LOGGER.warn("Could not register the OpenPrinter tools disk", exception);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
