package pcl.openprinter;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = OpenPrinter.MOD_ID, value = Dist.CLIENT)
public final class ClientRegistration {
    private static final ResourceLocation PRINTER_GUI = OpenPrinter.id("textures/gui/printer.png");
    private static final ResourceLocation SHREDDER_GUI = OpenPrinter.id("textures/gui/shredder.png");
    private static final ResourceLocation FILE_CABINET_GUI = OpenPrinter.id("textures/gui/filecabinet.png");
    private static final ResourceLocation BRIEFCASE_GUI = OpenPrinter.id("textures/gui/briefcaseinventory.png");
    private static final ResourceLocation FOLDER_GUI = OpenPrinter.id("textures/gui/inventoryitem.png");
    private static final ResourceLocation FOLDER_VIEW_BASE = OpenPrinter.id("textures/gui/folder_base.png");
    private static final ResourceLocation FOLDER_VIEW_OVERLAY = OpenPrinter.id("textures/gui/folder_overlay.png");

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(OpenPrinter.DEVICE_MENU.get(), DeviceScreen::new);
        event.register(OpenPrinter.PORTABLE_MENU.get(), PortableScreen::new);
    }

    static void openPage(ItemStack stack) {
        Minecraft.getInstance().setScreen(new PrintedPageScreen(stack));
    }

    static void openFolderView(ItemStack stack) {
        Minecraft.getInstance().setScreen(new FolderViewScreen(stack));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            var data = PrinterBlockEntity.tag(stack);
            int color = data.contains("FolderColor") ? data.getInt("FolderColor") : 0xFFD2B48C;
            return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
        }, OpenPrinter.FOLDER.get());
    }

    private static final class DeviceScreen extends AbstractContainerScreen<DeviceMenu> {
        private DeviceScreen(DeviceMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title);
            imageWidth = menu.kind() == DeviceBlock.Kind.BRIEFCASE ? 176 : 175;
            imageHeight = menu.kind() == DeviceBlock.Kind.BRIEFCASE ? 152 : 195;
        }

        @Override
        protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
            ResourceLocation texture = switch (menu.kind()) {
                case PRINTER -> PRINTER_GUI;
                case SHREDDER -> SHREDDER_GUI;
                case FILE_CABINET -> FILE_CABINET_GUI;
                case BRIEFCASE -> BRIEFCASE_GUI;
            };
            graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        }

        @Override
        protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
            switch (menu.kind()) {
                case PRINTER -> {
                    graphics.drawWordWrap(font, Component.translatable("gui.openprinter.string.blackInk"), 25, 25, 40, 0x404040);
                    graphics.drawWordWrap(font, Component.translatable("gui.openprinter.string.colorInk"), 55, 25, 40, 0x404040);
                    graphics.drawWordWrap(font, Component.translatable("gui.openprinter.string.paperInput"), 125, 25, 40, 0x404040);
                    graphics.drawString(font, Component.translatable("gui.openprinter.string.scannerInput"), 70, 4, 0x404040, false);
                    Component status = printerStatus();
                    graphics.drawString(font, status, (imageWidth - font.width(status)) / 2, 67, 0x404040, false);
                    if (menu.queueLength() > 1) {
                        Component queued = Component.translatable("gui.openprinter.status.queued", menu.queueLength() - 1);
                        graphics.drawString(font, queued, (imageWidth - font.width(queued)) / 2, 76, 0x404040, false);
                    }
                }
                case SHREDDER -> graphics.drawString(font, Component.translatable("gui.openprinter.string.shredder"), 65, 4, 0x404040, false);
                case FILE_CABINET -> graphics.drawCenteredString(font, Component.translatable("gui.openprinter.string.filecabinet"), imageWidth / 2, 4, 0x404040);
                case BRIEFCASE -> {
                    graphics.drawString(font, title, 7, 6, 0x505050, false);
                    graphics.drawString(font, playerInventoryTitle, 7, 60, 0x505050, false);
                }
            }
        }

        private Component printerStatus() {
            return switch (menu.printerState()) {
                case 1 -> Component.translatable("gui.openprinter.status.starting");
                case 2 -> Component.translatable("gui.openprinter.status.printing", menu.currentPage(),
                        menu.totalPages(), menu.pageProgress());
                case 3 -> Component.translatable("gui.openprinter.status.blocked." + menu.blocker());
                default -> Component.translatable("gui.openprinter.status.idle");
            };
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            super.render(graphics, mouseX, mouseY, partialTick);
            renderTooltip(graphics, mouseX, mouseY);
        }
    }

    private static final class PortableScreen extends AbstractContainerScreen<PortableStorageMenu> {
        private PortableScreen(PortableStorageMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title);
            imageWidth = 176;
            imageHeight = menu.isBriefcase() ? 152 : 136;
        }

        @Override
        protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
            graphics.blit(menu.isBriefcase() ? BRIEFCASE_GUI : FOLDER_GUI,
                    leftPos, topPos, 0, 0, imageWidth, imageHeight);
        }

        @Override
        protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
            if (menu.isBriefcase()) {
                graphics.drawString(font, title, 7, 6, 0x505050, false);
                graphics.drawString(font, playerInventoryTitle, 7, 60, 0x505050, false);
            } else {
                graphics.drawString(font, title, 8, 6, 0x404040, false);
            }
        }

        @Override
        public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Keep the handheld inventory panel, but skip the full-screen transparent/blur background.
            renderBg(graphics, partialTick, mouseX, mouseY);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            super.render(graphics, mouseX, mouseY, partialTick);
            renderTooltip(graphics, mouseX, mouseY);
        }
    }

    private static final class PrintedPageScreen extends Screen {
        private final ItemStack page;

        private PrintedPageScreen(ItemStack page) {
            super(Component.translatable("screen.openprinter.printed_page"));
            this.page = page;
        }

        @Override
        public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Screen.render calls this after the page has been drawn; a no-op prevents blurring the page itself.
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int left = (width - 192) / 2;
            int top = (height - 216) / 2;
            graphics.fill(left, top, left + 192, top + 216, 0xFFF7EBC8);
            graphics.renderOutline(left, top, 192, 216, 0xFF6B5438);

            var data = PrinterBlockEntity.tag(page);
            String pageTitle = data.getString("pageTitle");
            String heading = pageTitle.isEmpty() ? "Printed Page" : pageTitle;
            graphics.drawString(font, heading, width / 2 - font.width(heading) / 2, top + 12, 0xFF302010, false);
            for (int lineNumber = 0; lineNumber < 20 && data.contains("line" + lineNumber); lineNumber++) {
                String[] parts = data.getString("line" + lineNumber).split("\u221e", 3);
                String text = parts[0];
                int color = parts.length > 1 ? parseColor(parts[1]) : 0;
                String alignment = parts.length > 2 ? parts[2] : "left";
                int x = switch (alignment) {
                    case "center" -> width / 2 - font.width(text) / 2;
                    case "right" -> left + 178 - font.width(text);
                    default -> left + 14;
                };
                graphics.drawString(font, text, x, top + 32 + lineNumber * 8, 0xFF000000 | color, false);
            }
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        private static int parseColor(String value) {
            try { return Integer.parseInt(value) & 0xFFFFFF; }
            catch (NumberFormatException ignored) { return 0; }
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }

    private static final class FolderViewScreen extends Screen {
        private static final int VIEW_WIDTH = 200;
        private static final int VIEW_HEIGHT = 230;
        private final ItemStack folder;
        private final List<ItemStack> documents = new ArrayList<>();
        private int documentIndex;

        private FolderViewScreen(ItemStack folder) {
            super(folder.getHoverName());
            this.folder = folder;
            if (Minecraft.getInstance().level != null) {
                PortableInventory inventory = new PortableInventory(folder, 9,
                        Minecraft.getInstance().level.registryAccess(), true);
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    ItemStack document = inventory.getStackInSlot(slot);
                    if (!document.isEmpty()) documents.add(document.copy());
                }
            }
        }

        @Override
        public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // A handheld folder is drawn directly over the world.
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int left = (width - VIEW_WIDTH) / 2;
            int top = (height - VIEW_HEIGHT) / 2;
            int color = PrinterBlockEntity.tag(folder).contains("FolderColor")
                    ? PrinterBlockEntity.tag(folder).getInt("FolderColor")
                    : 0xD2B48C;
            graphics.setColor(((color >> 16) & 0xFF) / 255.0F,
                    ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
            graphics.blit(FOLDER_VIEW_BASE, left, top, 0, 0, VIEW_WIDTH, VIEW_HEIGHT);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.blit(FOLDER_VIEW_OVERLAY, left, top, 0, 0, VIEW_WIDTH, VIEW_HEIGHT);

            graphics.pose().pushPose();
            graphics.pose().translate(left + 192, top + 9, 0);
            graphics.pose().mulPose(Axis.ZP.rotationDegrees(90));
            graphics.drawString(font, limit(title.getString(), 14), 0, 0, 0xFF000000, false);
            graphics.pose().popPose();

            if (!documents.isEmpty()) {
                String pageCounter = "Page " + (documentIndex + 1) + " of " + documents.size();
                graphics.drawString(font, pageCounter, left + (VIEW_WIDTH - font.width(pageCounter)) / 2,
                        top + 4, 0xFF000000, false);
                renderDocument(graphics, documents.get(documentIndex), left + 16, top + 16, 164);
                if (documentIndex > 0) {
                    graphics.blit(FOLDER_VIEW_OVERLAY, left + 38, top + 217, 10, 246, 23, 13, 256, 256);
                }
                if (documentIndex + 1 < documents.size()) {
                    graphics.blit(FOLDER_VIEW_OVERLAY, left + 120, top + 217, 30, 233, 23, 13, 256, 256);
                }
            }
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int left = (width - VIEW_WIDTH) / 2;
            int top = (height - VIEW_HEIGHT) / 2;
            if (button == 0 && inside(mouseX, mouseY, left + 38, top + 217, 23, 13) && documentIndex > 0) {
                documentIndex--;
                return true;
            }
            if (button == 0 && inside(mouseX, mouseY, left + 120, top + 217, 23, 13)
                    && documentIndex + 1 < documents.size()) {
                documentIndex++;
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 263 && documentIndex > 0) {
                documentIndex--;
                return true;
            }
            if (keyCode == 262 && documentIndex + 1 < documents.size()) {
                documentIndex++;
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void renderDocument(GuiGraphics graphics, ItemStack document, int left, int top, int contentWidth) {
            if (document.is(OpenPrinter.PRINTED_PAGE.get())) {
                var data = PrinterBlockEntity.tag(document);
                for (int lineNumber = 0; lineNumber < 20 && data.contains("line" + lineNumber); lineNumber++) {
                    String[] parts = data.getString("line" + lineNumber).split("\u221e", 3);
                    String text = parts[0];
                    int textColor = parts.length > 1 ? PrintedPageScreen.parseColor(parts[1]) : 0;
                    String alignment = parts.length > 2 ? parts[2] : "left";
                    int x = switch (alignment) {
                        case "center" -> left + (contentWidth - font.width(text)) / 2;
                        case "right" -> left + contentWidth - font.width(text);
                        default -> left;
                    };
                    graphics.drawString(font, text, x, top + lineNumber * 9, 0xFF000000 | textColor, false);
                }
                return;
            }
            if (document.is(Items.WRITABLE_BOOK)) {
                WritableBookContent content = document.getOrDefault(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY);
                String text = content.pages().isEmpty() ? "" : content.pages().getFirst().raw();
                graphics.drawWordWrap(font, Component.literal(text), left, top, contentWidth, 0xFF000000);
                return;
            }
            if (document.is(Items.WRITTEN_BOOK)) {
                WrittenBookContent content = document.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY);
                String bookTitle = content.title().raw();
                graphics.drawString(font, bookTitle, left + (contentWidth - font.width(bookTitle)) / 2,
                        top, 0xFF000000, false);
                String text = content.pages().isEmpty() ? "" : content.pages().getFirst().raw().getString();
                graphics.drawWordWrap(font, Component.literal(text), left, top + 14, contentWidth, 0xFF000000);
                return;
            }
            graphics.renderItem(document, left + (contentWidth - 16) / 2, top + 36);
            graphics.drawString(font, document.getHoverName(),
                    left + (contentWidth - font.width(document.getHoverName())) / 2,
                    top + 58, 0xFF000000, false);
        }

        private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }

        private static String limit(String value, int length) {
            return value.length() <= length ? value : value.substring(0, length) + "...";
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }

    private ClientRegistration() {}
}
