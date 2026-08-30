package pcl.openprinter;

import net.minecraft.nbt.CompoundTag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

final class PrintJob {
    enum Kind { DOCUMENT, LABEL, MAP }
    enum State { QUEUED, PRINTING, BLOCKED, COMPLETE, CANCELLED }

    final UUID id;
    final Kind kind;
    final PrintDocument document;
    final MapPrintImage mapImage;
    final String label;
    final int copies;
    State state = State.QUEUED;
    String reason = "";
    int completedPages;
    int printTicks;

    private PrintJob(UUID id, Kind kind, PrintDocument document, MapPrintImage mapImage, String label, int copies) {
        this.id = id;
        this.kind = kind;
        this.document = document;
        this.mapImage = mapImage;
        this.label = label;
        this.copies = copies;
    }

    static PrintJob document(PrintDocument document, int copies) {
        return new PrintJob(UUID.randomUUID(), Kind.DOCUMENT, document, null, "", copies);
    }

    static PrintJob label(String label, int copies) {
        return new PrintJob(UUID.randomUUID(), Kind.LABEL, null, null, label, copies);
    }

    static PrintJob map(MapPrintImage image, int copies) {
        return new PrintJob(UUID.randomUUID(), Kind.MAP, null, image, "", copies);
    }

    int pagesPerCopy() {
        return kind == Kind.DOCUMENT ? document.pageCount() : 1;
    }

    int totalPages() {
        return pagesPerCopy() * copies;
    }

    int pageIndex() {
        return completedPages % pagesPerCopy();
    }

    int copyIndex() {
        return completedPages / pagesPerCopy();
    }

    Map<String, Object> toLua() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id.toString());
        result.put("type", kind.name().toLowerCase());
        result.put("state", state.name().toLowerCase());
        result.put("reason", reason);
        result.put("pagesComplete", completedPages);
        result.put("pagesTotal", totalPages());
        result.put("copies", copies);
        result.put("copy", Math.min(copies, copyIndex() + 1));
        result.put("page", Math.min(pagesPerCopy(), pageIndex() + 1));
        double partialPage = Math.min(1.0, printTicks / (double) PrinterBlockEntity.PAGE_PRINT_TICKS);
        result.put("progress", totalPages() == 0 ? 100 : ((completedPages + partialPage) * 100.0) / totalPages());
        if (kind == Kind.DOCUMENT) result.put("title", document.title());
        else if (kind == Kind.LABEL) result.put("label", label);
        else {
            result.put("title", mapImage.title());
            result.put("width", mapImage.sourceWidth());
            result.put("height", mapImage.sourceHeight());
        }
        return result;
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("id", id);
        tag.putString("kind", kind.name());
        if (document != null) tag.put("document", document.save());
        if (mapImage != null) tag.put("mapImage", mapImage.save());
        tag.putString("label", label);
        tag.putInt("copies", copies);
        tag.putString("state", state.name());
        tag.putString("reason", reason);
        tag.putInt("completedPages", completedPages);
        tag.putInt("printTicks", printTicks);
        return tag;
    }

    static PrintJob load(CompoundTag tag) {
        Kind kind = enumValue(Kind.class, tag.getString("kind"), Kind.DOCUMENT);
        PrintDocument document = kind == Kind.DOCUMENT ? PrintDocument.load(tag.getCompound("document")) : null;
        MapPrintImage mapImage = kind == Kind.MAP ? MapPrintImage.load(tag.getCompound("mapImage")) : null;
        PrintJob job = new PrintJob(tag.hasUUID("id") ? tag.getUUID("id") : UUID.randomUUID(), kind,
                document, mapImage, tag.getString("label"), Math.max(1, tag.getInt("copies")));
        job.state = enumValue(State.class, tag.getString("state"), State.QUEUED);
        job.reason = tag.getString("reason");
        job.completedPages = Math.max(0, tag.getInt("completedPages"));
        job.printTicks = Math.max(0, tag.getInt("printTicks"));
        return job;
    }

    private static <T extends Enum<T>> T enumValue(Class<T> type, String value, T fallback) {
        try { return Enum.valueOf(type, value); }
        catch (IllegalArgumentException ignored) { return fallback; }
    }
}
