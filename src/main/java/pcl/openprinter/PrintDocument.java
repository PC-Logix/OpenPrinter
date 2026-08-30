package pcl.openprinter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class PrintDocument {
    static final int MAX_WIDTH = 164;
    static final int LINES_PER_PAGE = 20;
    static final int MAX_DOCUMENT_PAGES = 64;
    static final int MAX_TEXT_LENGTH = 32768;

    record Line(String text, int color, String alignment) {
        CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putString("text", text);
            tag.putInt("color", color);
            tag.putString("alignment", alignment);
            return tag;
        }

        static Line load(CompoundTag tag) {
            return new Line(tag.getString("text"), tag.getInt("color"), normalizeAlignment(tag.getString("alignment")));
        }

        Map<String, Object> toLua() {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("text", text);
            result.put("color", color);
            result.put("alignment", alignment);
            return result;
        }
    }

    private final String title;
    private final List<List<Line>> pages;

    private PrintDocument(String title, List<List<Line>> pages) {
        this.title = title;
        this.pages = pages;
    }

    String title() {
        return title;
    }

    int pageCount() {
        return pages.size();
    }

    List<Line> page(int index) {
        return pages.get(index);
    }

    static PrintDocument parse(Object value, Map<?, ?> options) {
        boolean wrap = booleanValue(options.get("wrap"), true);
        String optionTitle = nullableString(options.get("title"));
        String title = optionTitle == null ? "" : optionTitle;
        List<List<Line>> pages = new ArrayList<>();

        if (value instanceof Map<?, ?> document) {
            if (optionTitle == null) title = stringValue(document.get("title"), "");
            Object explicitPages = document.get("pages");
            if (explicitPages instanceof Map<?, ?> pageTable) {
                for (Object pageValue : sequence(pageTable)) {
                    List<Line> pageLines = parsePageValue(pageValue, wrap);
                    addPaginated(pages, pageLines, true);
                }
            } else if (document.get("lines") instanceof Map<?, ?> lineTable) {
                addPaginated(pages, parseLines(lineTable, wrap), false);
            } else if (document.containsKey("text")) {
                addPaginated(pages, textLines(stringValue(document.get("text"), ""), 0, "left", wrap), false);
            } else {
                throw new IllegalArgumentException("document must contain text, lines, or pages");
            }
        } else {
            addPaginated(pages, textLines(stringValue(value, null), 0, "left", wrap), false);
        }

        if (pages.isEmpty()) pages.add(new ArrayList<>());
        if (pages.size() > MAX_DOCUMENT_PAGES) throw new IllegalArgumentException("document exceeds 64 pages");
        return new PrintDocument(limit(title, 64), pages);
    }

    static PrintDocument scanned(String title, List<Line> lines) {
        List<List<Line>> pages = new ArrayList<>();
        addPaginated(pages, new ArrayList<>(lines), false);
        if (pages.isEmpty()) pages.add(new ArrayList<>());
        return new PrintDocument(limit(title, 64), pages);
    }

    private static List<Line> parsePageValue(Object value, boolean wrap) {
        if (value instanceof Map<?, ?> page) {
            if (page.get("lines") instanceof Map<?, ?> lines) return parseLines(lines, wrap);
            if (page.containsKey("text")) return textLines(stringValue(page.get("text"), ""), 0, "left", wrap);
        }
        return textLines(stringValue(value, null), 0, "left", wrap);
    }

    private static List<Line> parseLines(Map<?, ?> table, boolean wrap) {
        List<Line> result = new ArrayList<>();
        for (Object value : sequence(table)) {
            if (value instanceof Map<?, ?> line) {
                String text = stringValue(line.get("text"), "");
                int color = intValue(line.get("color"), 0) & 0xFFFFFF;
                String alignment = normalizeAlignment(stringValue(line.get("alignment"), "left"));
                result.addAll(textLines(text, color, alignment, wrap));
            } else {
                result.addAll(textLines(stringValue(value, ""), 0, "left", wrap));
            }
        }
        return result;
    }

    private static List<Line> textLines(String text, int color, String alignment, boolean wrap) {
        if (text == null) throw new IllegalArgumentException("document must be a string or table");
        if (text.length() > MAX_TEXT_LENGTH) throw new IllegalArgumentException("document text is too large");
        List<Line> result = new ArrayList<>();
        for (String raw : text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            if (!wrap && CharacterWidth.calculate(raw) > MAX_WIDTH) {
                throw new IllegalArgumentException("line exceeds printable width");
            }
            if (!wrap || CharacterWidth.calculate(raw) <= MAX_WIDTH) {
                result.add(new Line(raw, color, alignment));
                continue;
            }
            String remaining = raw;
            while (CharacterWidth.calculate(remaining) > MAX_WIDTH) {
                int fit = fittingEnd(remaining);
                int space = remaining.lastIndexOf(' ', Math.max(0, fit - 1));
                int cut = space > 0 ? space : Math.max(1, fit);
                result.add(new Line(remaining.substring(0, cut).stripTrailing(), color, alignment));
                remaining = remaining.substring(cut).stripLeading();
            }
            result.add(new Line(remaining, color, alignment));
        }
        return result;
    }

    private static int fittingEnd(String text) {
        int end = 0;
        for (int i = 1; i <= text.length(); i++) {
            if (CharacterWidth.calculate(text.substring(0, i)) > MAX_WIDTH) break;
            end = i;
        }
        return end;
    }

    private static void addPaginated(List<List<Line>> pages, List<Line> lines, boolean forceNewPage) {
        if (forceNewPage && !pages.isEmpty() && !pages.getLast().isEmpty()) pages.add(new ArrayList<>());
        if (pages.isEmpty()) pages.add(new ArrayList<>());
        for (Line line : lines) {
            if (pages.getLast().size() >= LINES_PER_PAGE) pages.add(new ArrayList<>());
            pages.getLast().add(line);
        }
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("title", title);
        ListTag savedPages = new ListTag();
        for (List<Line> page : pages) {
            CompoundTag pageTag = new CompoundTag();
            ListTag savedLines = new ListTag();
            for (Line line : page) savedLines.add(line.save());
            pageTag.put("lines", savedLines);
            savedPages.add(pageTag);
        }
        tag.put("pages", savedPages);
        return tag;
    }

    static PrintDocument load(CompoundTag tag) {
        List<List<Line>> pages = new ArrayList<>();
        ListTag savedPages = tag.getList("pages", Tag.TAG_COMPOUND);
        for (int pageIndex = 0; pageIndex < savedPages.size(); pageIndex++) {
            List<Line> page = new ArrayList<>();
            ListTag savedLines = savedPages.getCompound(pageIndex).getList("lines", Tag.TAG_COMPOUND);
            for (int lineIndex = 0; lineIndex < savedLines.size(); lineIndex++) page.add(Line.load(savedLines.getCompound(lineIndex)));
            pages.add(page);
        }
        if (pages.isEmpty()) pages.add(new ArrayList<>());
        return new PrintDocument(tag.getString("title"), pages);
    }

    Map<String, Object> toLua() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "document");
        result.put("title", title);
        Map<Integer, Object> luaPages = new LinkedHashMap<>();
        for (int pageIndex = 0; pageIndex < pages.size(); pageIndex++) {
            Map<String, Object> page = new LinkedHashMap<>();
            Map<Integer, Object> luaLines = new LinkedHashMap<>();
            for (int lineIndex = 0; lineIndex < pages.get(pageIndex).size(); lineIndex++) {
                luaLines.put(lineIndex + 1, pages.get(pageIndex).get(lineIndex).toLua());
            }
            page.put("lines", luaLines);
            luaPages.put(pageIndex + 1, page);
        }
        result.put("pages", luaPages);
        return result;
    }

    static List<Object> sequence(Map<?, ?> table) {
        List<Map.Entry<?, ?>> entries = new ArrayList<>();
        for (Map.Entry<?, ?> entry : table.entrySet()) {
            if (entry.getKey() instanceof Number) entries.add(entry);
        }
        entries.sort(Comparator.comparingDouble(entry -> ((Number) entry.getKey()).doubleValue()));
        List<Object> values = new ArrayList<>(entries.size());
        for (Map.Entry<?, ?> entry : entries) values.add(entry.getValue());
        return values;
    }

    static String stringValue(Object value, String fallback) {
        if (value == null) return fallback;
        if (value instanceof String string) return string;
        if (value instanceof byte[] bytes) return new String(bytes, StandardCharsets.UTF_8);
        throw new IllegalArgumentException("expected string");
    }

    private static String nullableString(Object value) {
        return value == null ? null : stringValue(value, null);
    }

    static int intValue(Object value, int fallback) {
        return value instanceof Number number ? number.intValue() : fallback;
    }

    static boolean booleanValue(Object value, boolean fallback) {
        return value instanceof Boolean bool ? bool : fallback;
    }

    static String normalizeAlignment(String alignment) {
        if (alignment == null || alignment.isEmpty()) return "left";
        if (!alignment.equals("left") && !alignment.equals("center") && !alignment.equals("right")) {
            throw new IllegalArgumentException("alignment must be left, center, or right");
        }
        return alignment;
    }

    private static String limit(String value, int length) {
        return value.length() <= length ? value : value.substring(0, length);
    }
}
