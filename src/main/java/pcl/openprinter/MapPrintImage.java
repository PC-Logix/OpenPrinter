package pcl.openprinter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.MapColor;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class MapPrintImage {
    static final int SIZE = 128;
    static final int MAX_ENCODED_BYTES = 8 * 1024 * 1024;
    static final int MAX_SOURCE_DIMENSION = 4096;
    static final long MAX_SOURCE_PIXELS = 16L * 1024L * 1024L;
    private static final int PIXELS = SIZE * SIZE;
    private static final int FIRST_OPAQUE_COLOR = 4;
    private static final int LAST_OPAQUE_COLOR = 247;

    private final byte[] colors;
    private final String title;
    private final int sourceWidth;
    private final int sourceHeight;

    private MapPrintImage(byte[] colors, String title, int sourceWidth, int sourceHeight) {
        this.colors = colors;
        this.title = title;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    static MapPrintImage parse(Map<?, ?> image, Map<?, ?> options) {
        List<Object> rows = PrintDocument.sequence(image);
        if (rows.isEmpty() || rows.size() > SIZE) {
            throw new IllegalArgumentException("map image height must be between 1 and 128 pixels");
        }

        int[][] rgb = new int[rows.size()][];
        int width = -1;
        for (int y = 0; y < rows.size(); y++) {
            if (!(rows.get(y) instanceof Map<?, ?> row)) {
                throw new IllegalArgumentException("map image must be a table of pixel rows");
            }
            List<Object> pixels = PrintDocument.sequence(row);
            if (pixels.isEmpty() || pixels.size() > SIZE) {
                throw new IllegalArgumentException("map image width must be between 1 and 128 pixels");
            }
            if (width < 0) width = pixels.size();
            if (pixels.size() != width) throw new IllegalArgumentException("map image rows must have equal widths");
            rgb[y] = new int[width];
            for (int x = 0; x < width; x++) {
                Object pixel = pixels.get(x);
                if (!(pixel instanceof Number number)) {
                    throw new IllegalArgumentException("map pixels must be RGB numbers");
                }
                rgb[y][x] = number.intValue() & 0xFFFFFF;
            }
        }

        int[] scaled = new int[PIXELS];
        for (int y = 0; y < SIZE; y++) {
            int sourceY = y * rows.size() / SIZE;
            for (int x = 0; x < SIZE; x++) {
                int sourceX = x * width / SIZE;
                scaled[x + y * SIZE] = rgb[sourceY][sourceX];
            }
        }

        return new MapPrintImage(toMapColors(scaled, PrintDocument.booleanValue(options.get("dither"), false)),
                title(options), width, rows.size());
    }

    static MapPrintImage decode(byte[] encoded, Map<?, ?> options) {
        if (encoded.length == 0) throw new IllegalArgumentException("image file is empty");
        if (encoded.length > MAX_ENCODED_BYTES) throw new IllegalArgumentException("image file exceeds 8 MiB");

        BufferedImage source;
        int sourceWidth;
        int sourceHeight;
        try (ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(encoded))) {
            if (stream == null) throw new IllegalArgumentException("could not open image data");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new IllegalArgumentException("unsupported image format; use PNG, JPEG, GIF, or BMP");
            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                sourceWidth = reader.getWidth(0);
                sourceHeight = reader.getHeight(0);
                if (sourceWidth < 1 || sourceHeight < 1
                        || sourceWidth > MAX_SOURCE_DIMENSION || sourceHeight > MAX_SOURCE_DIMENSION
                        || (long) sourceWidth * sourceHeight > MAX_SOURCE_PIXELS) {
                    throw new IllegalArgumentException("image dimensions exceed 4096x4096 or 16 megapixels");
                }
                source = reader.read(0);
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("could not decode image: " + exception.getMessage(), exception);
        }
        if (source == null) throw new IllegalArgumentException("unsupported or corrupt image");

        String fit = option(options, "fit", "contain");
        if (!fit.equals("contain") && !fit.equals("cover") && !fit.equals("stretch")) {
            throw new IllegalArgumentException("fit must be contain, cover, or stretch");
        }
        String filter = option(options, "filter", "bicubic");
        Object interpolation = switch (filter) {
            case "nearest" -> RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            case "bilinear" -> RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            case "bicubic" -> RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            default -> throw new IllegalArgumentException("filter must be nearest, bilinear, or bicubic");
        };

        int background = PrintDocument.intValue(options.get("background"), 0xFFFFFF) & 0xFFFFFF;
        BufferedImage scaled = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaled.createGraphics();
        try {
            graphics.setColor(new Color(background));
            graphics.fillRect(0, 0, SIZE, SIZE);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
            int x = 0;
            int y = 0;
            int width = SIZE;
            int height = SIZE;
            if (!fit.equals("stretch")) {
                double scale = fit.equals("contain")
                        ? Math.min(SIZE / (double) sourceWidth, SIZE / (double) sourceHeight)
                        : Math.max(SIZE / (double) sourceWidth, SIZE / (double) sourceHeight);
                width = Math.max(1, (int) Math.round(sourceWidth * scale));
                height = Math.max(1, (int) Math.round(sourceHeight * scale));
                x = (SIZE - width) / 2;
                y = (SIZE - height) / 2;
            }
            graphics.drawImage(source, x, y, width, height, null);
        } finally {
            graphics.dispose();
        }

        int[] rgb = scaled.getRGB(0, 0, SIZE, SIZE, null, 0, SIZE);
        boolean dither = PrintDocument.booleanValue(options.get("dither"), true);
        return new MapPrintImage(toMapColors(rgb, dither), title(options), sourceWidth, sourceHeight);
    }

    private static String option(Map<?, ?> options, String key, String fallback) {
        String value = PrintDocument.stringValue(options.get(key), fallback);
        return value == null ? fallback : value.toLowerCase(java.util.Locale.ROOT);
    }

    private static String title(Map<?, ?> options) {
        String title = PrintDocument.stringValue(options.get("title"), "");
        return title.length() > 64 ? title.substring(0, 64) : title;
    }

    private static byte[] toMapColors(int[] rgb, boolean dither) {
        byte[] colors = new byte[PIXELS];
        Map<Integer, Byte> paletteCache = new HashMap<>();
        if (!dither) {
            for (int i = 0; i < PIXELS; i++) {
                int color = rgb[i] & 0xFFFFFF;
                colors[i] = paletteCache.computeIfAbsent(color, MapPrintImage::nearestMapColor);
            }
            return colors;
        }

        float[] redError = new float[PIXELS];
        float[] greenError = new float[PIXELS];
        float[] blueError = new float[PIXELS];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int index = x + y * SIZE;
                int source = rgb[index];
                int red = clamp((int) (((source >> 16) & 0xFF) + redError[index]));
                int green = clamp((int) (((source >> 8) & 0xFF) + greenError[index]));
                int blue = clamp((int) ((source & 0xFF) + blueError[index]));
                byte packed = nearestMapColor(red << 16 | green << 8 | blue);
                colors[index] = packed;
                int rendered = MapColor.getColorFromPackedId(packed & 0xFF);
                diffuse(redError, index, x, y, red - (rendered & 0xFF));
                diffuse(greenError, index, x, y, green - (rendered >> 8 & 0xFF));
                diffuse(blueError, index, x, y, blue - (rendered >> 16 & 0xFF));
            }
        }
        return colors;
    }

    private static void diffuse(float[] errors, int index, int x, int y, float error) {
        if (x + 1 < SIZE) errors[index + 1] += error * 7 / 16;
        if (y + 1 >= SIZE) return;
        if (x > 0) errors[index + SIZE - 1] += error * 3 / 16;
        errors[index + SIZE] += error * 5 / 16;
        if (x + 1 < SIZE) errors[index + SIZE + 1] += error / 16;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static byte nearestMapColor(int rgb) {
        int red = rgb >> 16 & 0xFF;
        int green = rgb >> 8 & 0xFF;
        int blue = rgb & 0xFF;
        long bestDistance = Long.MAX_VALUE;
        int best = FIRST_OPAQUE_COLOR;
        for (int packed = FIRST_OPAQUE_COLOR; packed <= LAST_OPAQUE_COLOR; packed++) {
            // MapColor returns the native-image ABGR layout, so red and blue are reversed here.
            int rendered = MapColor.getColorFromPackedId(packed);
            int candidateRed = rendered & 0xFF;
            int candidateGreen = rendered >> 8 & 0xFF;
            int candidateBlue = rendered >> 16 & 0xFF;
            long redDelta = red - candidateRed;
            long greenDelta = green - candidateGreen;
            long blueDelta = blue - candidateBlue;
            long distance = redDelta * redDelta * 30 + greenDelta * greenDelta * 59 + blueDelta * blueDelta * 11;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = packed;
            }
        }
        return (byte) best;
    }

    byte[] colors() {
        return colors;
    }

    String title() {
        return title;
    }

    int sourceWidth() {
        return sourceWidth;
    }

    int sourceHeight() {
        return sourceHeight;
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("colors", colors);
        tag.putString("title", title);
        tag.putInt("sourceWidth", sourceWidth);
        tag.putInt("sourceHeight", sourceHeight);
        return tag;
    }

    static MapPrintImage load(CompoundTag tag) {
        byte[] savedColors = tag.getByteArray("colors");
        byte[] colors = savedColors.length == PIXELS ? savedColors : new byte[PIXELS];
        return new MapPrintImage(colors, tag.getString("title"),
                Math.max(1, tag.getInt("sourceWidth")), Math.max(1, tag.getInt("sourceHeight")));
    }
}
