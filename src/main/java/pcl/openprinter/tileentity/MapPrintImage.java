package pcl.openprinter.tileentity;

import net.minecraft.block.material.MapColor;
import net.minecraft.nbt.NBTTagCompound;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Converts an RGB image into the byte palette used by a vanilla 1.12 map. */
final class MapPrintImage {
	static final int SIZE = 128;
	static final int MAX_ENCODED_BYTES = 8 * 1024 * 1024;
	static final int MAX_SOURCE_DIMENSION = 4096;
	static final long MAX_SOURCE_PIXELS = 16L * 1024L * 1024L;
	private static final int PIXELS = SIZE * SIZE;

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
		List<Object> rows = sequence(image);
		if(rows.isEmpty() || rows.size() > SIZE)
			throw new IllegalArgumentException("map image height must be between 1 and 128 pixels");

		int[][] rgb = new int[rows.size()][];
		int width = -1;
		for(int y = 0; y < rows.size(); y++) {
			if(!(rows.get(y) instanceof Map))
				throw new IllegalArgumentException("map image must be a table of pixel rows");
			List<Object> pixels = sequence((Map<?, ?>) rows.get(y));
			if(pixels.isEmpty() || pixels.size() > SIZE)
				throw new IllegalArgumentException("map image width must be between 1 and 128 pixels");
			if(width < 0)
				width = pixels.size();
			if(pixels.size() != width)
				throw new IllegalArgumentException("map image rows must have equal widths");
			rgb[y] = new int[width];
			for(int x = 0; x < width; x++) {
				if(!(pixels.get(x) instanceof Number))
					throw new IllegalArgumentException("map pixels must be RGB numbers");
				rgb[y][x] = ((Number) pixels.get(x)).intValue() & 0xFFFFFF;
			}
		}

		int[] scaled = new int[PIXELS];
		for(int y = 0; y < SIZE; y++) {
			int sourceY = y * rows.size() / SIZE;
			for(int x = 0; x < SIZE; x++)
				scaled[x + y * SIZE] = rgb[sourceY][x * width / SIZE];
		}

		return new MapPrintImage(toMapColors(scaled, booleanValue(options, "dither", false)),
			title(options), width, rows.size());
	}

	static MapPrintImage decode(byte[] encoded, Map<?, ?> options) {
		if(encoded.length == 0)
			throw new IllegalArgumentException("image file is empty");
		if(encoded.length > MAX_ENCODED_BYTES)
			throw new IllegalArgumentException("image file exceeds 8 MiB");

		BufferedImage source;
		int sourceWidth;
		int sourceHeight;
		try {
			ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(encoded));
			if(stream == null)
				throw new IllegalArgumentException("could not open image data");
			try {
				Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
				if(!readers.hasNext())
					throw new IllegalArgumentException("unsupported image format; use PNG, JPEG, GIF, or BMP");
				ImageReader reader = readers.next();
				try {
					reader.setInput(stream, true, true);
					sourceWidth = reader.getWidth(0);
					sourceHeight = reader.getHeight(0);
					if(sourceWidth < 1 || sourceHeight < 1
							|| sourceWidth > MAX_SOURCE_DIMENSION || sourceHeight > MAX_SOURCE_DIMENSION
							|| (long) sourceWidth * sourceHeight > MAX_SOURCE_PIXELS)
						throw new IllegalArgumentException("image dimensions exceed 4096x4096 or 16 megapixels");
					source = reader.read(0);
				}
				finally {
					reader.dispose();
				}
			}
			finally {
				stream.close();
			}
		}
		catch(IOException exception) {
			throw new IllegalArgumentException("could not decode image: " + exception.getMessage(), exception);
		}
		if(source == null)
			throw new IllegalArgumentException("unsupported or corrupt image");

		String fit = option(options, "fit", "contain");
		if(!fit.equals("contain") && !fit.equals("cover") && !fit.equals("stretch"))
			throw new IllegalArgumentException("fit must be contain, cover, or stretch");
		String filter = option(options, "filter", "bicubic");
		Object interpolation;
		if(filter.equals("nearest"))
			interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		else if(filter.equals("bilinear"))
			interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		else if(filter.equals("bicubic"))
			interpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		else
			throw new IllegalArgumentException("filter must be nearest, bilinear, or bicubic");

		int background = intValue(options, "background", 0xFFFFFF) & 0xFFFFFF;
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
			if(!fit.equals("stretch")) {
				double scale = fit.equals("contain")
						? Math.min(SIZE / (double) sourceWidth, SIZE / (double) sourceHeight)
						: Math.max(SIZE / (double) sourceWidth, SIZE / (double) sourceHeight);
				width = Math.max(1, (int) Math.round(sourceWidth * scale));
				height = Math.max(1, (int) Math.round(sourceHeight * scale));
				x = (SIZE - width) / 2;
				y = (SIZE - height) / 2;
			}
			graphics.drawImage(source, x, y, width, height, null);
		}
		finally {
			graphics.dispose();
		}

		int[] rgb = scaled.getRGB(0, 0, SIZE, SIZE, null, 0, SIZE);
		return new MapPrintImage(toMapColors(rgb, booleanValue(options, "dither", true)),
			title(options), sourceWidth, sourceHeight);
	}

	private static List<Object> sequence(Map<?, ?> table) {
		List<Map.Entry<?, ?>> entries = new ArrayList<>();
		for(Map.Entry<?, ?> entry : table.entrySet()) {
			if(entry.getKey() instanceof Number)
				entries.add(entry);
		}
		Collections.sort(entries, new Comparator<Map.Entry<?, ?>>(){
			@Override
			public int compare(Map.Entry<?, ?> left, Map.Entry<?, ?> right) {
				return Double.compare(((Number) left.getKey()).doubleValue(),
					((Number) right.getKey()).doubleValue());
			}
		});
		List<Object> values = new ArrayList<>();
		for(int i = 0; i < entries.size(); i++) {
			double key = ((Number) entries.get(i).getKey()).doubleValue();
			if(key != i + 1)
				throw new IllegalArgumentException("map image tables must use sequential numeric keys");
			values.add(entries.get(i).getValue());
		}
		return values;
	}

	private static String option(Map<?, ?> options, String key, String fallback) {
		Object value = options.get(key);
		if(!(value instanceof String))
			return fallback;
		return ((String) value).toLowerCase(Locale.ROOT);
	}

	private static String title(Map<?, ?> options) {
		Object value = options.get("title");
		String title = value instanceof String ? (String) value : "";
		return title.length() > 64 ? title.substring(0, 64) : title;
	}

	private static boolean booleanValue(Map<?, ?> options, String key, boolean fallback) {
		Object value = options.get(key);
		return value instanceof Boolean ? (Boolean) value : fallback;
	}

	private static int intValue(Map<?, ?> options, String key, int fallback) {
		Object value = options.get(key);
		return value instanceof Number ? ((Number) value).intValue() : fallback;
	}

	private static byte[] toMapColors(int[] rgb, boolean dither) {
		byte[] colors = new byte[PIXELS];
		Map<Integer, Byte> paletteCache = new HashMap<>();
		if(!dither) {
			for(int i = 0; i < PIXELS; i++) {
				int color = rgb[i] & 0xFFFFFF;
				Byte mapped = paletteCache.get(color);
				if(mapped == null) {
					mapped = nearestMapColor(color);
					paletteCache.put(color, mapped);
				}
				colors[i] = mapped;
			}
			return colors;
		}

		float[] redError = new float[PIXELS];
		float[] greenError = new float[PIXELS];
		float[] blueError = new float[PIXELS];
		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE; x++) {
				int index = x + y * SIZE;
				int source = rgb[index];
				int red = clamp((int) (((source >> 16) & 0xFF) + redError[index]));
				int green = clamp((int) (((source >> 8) & 0xFF) + greenError[index]));
				int blue = clamp((int) ((source & 0xFF) + blueError[index]));
				byte mapped = nearestMapColor(red << 16 | green << 8 | blue);
				colors[index] = mapped;
				int rendered = renderedMapColor(mapped & 0xFF);
				diffuse(redError, index, x, y, red - (rendered >> 16 & 0xFF));
				diffuse(greenError, index, x, y, green - (rendered >> 8 & 0xFF));
				diffuse(blueError, index, x, y, blue - (rendered & 0xFF));
			}
		}
		return colors;
	}

	private static void diffuse(float[] errors, int index, int x, int y, float error) {
		if(x + 1 < SIZE)
			errors[index + 1] += error * 7 / 16;
		if(y + 1 >= SIZE)
			return;
		if(x > 0)
			errors[index + SIZE - 1] += error * 3 / 16;
		errors[index + SIZE] += error * 5 / 16;
		if(x + 1 < SIZE)
			errors[index + SIZE + 1] += error / 16;
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}

	private static byte nearestMapColor(int rgb) {
		int red = rgb >> 16 & 0xFF;
		int green = rgb >> 8 & 0xFF;
		int blue = rgb & 0xFF;
		long bestDistance = Long.MAX_VALUE;
		int best = 4;
		for(int colorIndex = 1; colorIndex < MapColor.COLORS.length; colorIndex++) {
			if(MapColor.COLORS[colorIndex] == null)
				continue;
			for(int shade = 0; shade < 4; shade++) {
				int rendered = renderedMapColor(colorIndex * 4 + shade);
				int candidateRed = rendered >> 16 & 0xFF;
				int candidateGreen = rendered >> 8 & 0xFF;
				int candidateBlue = rendered & 0xFF;
				long redDelta = red - candidateRed;
				long greenDelta = green - candidateGreen;
				long blueDelta = blue - candidateBlue;
				long distance = redDelta * redDelta * 30 + greenDelta * greenDelta * 59
						+ blueDelta * blueDelta * 11;
				if(distance < bestDistance) {
					bestDistance = distance;
					best = colorIndex * 4 + shade;
				}
			}
		}
		return (byte) best;
	}

	private static int renderedMapColor(int packed) {
		int colorIndex = packed / 4;
		int shade = packed & 3;
		if(colorIndex < 0 || colorIndex >= MapColor.COLORS.length || MapColor.COLORS[colorIndex] == null)
			return 0xFF000000;
		int brightness = shade == 0 ? 180 : shade == 1 ? 220 : shade == 2 ? 255 : 135;
		int base = MapColor.COLORS[colorIndex].colorValue;
		int red = (base >> 16 & 0xFF) * brightness / 255;
		int green = (base >> 8 & 0xFF) * brightness / 255;
		int blue = (base & 0xFF) * brightness / 255;
		return 0xFF000000 | red << 16 | green << 8 | blue;
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

	NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByteArray("colors", colors);
		tag.setString("title", title);
		tag.setInteger("sourceWidth", sourceWidth);
		tag.setInteger("sourceHeight", sourceHeight);
		return tag;
	}

	static MapPrintImage readFromNBT(NBTTagCompound tag) {
		byte[] savedColors = tag.getByteArray("colors");
		byte[] colors = savedColors.length == PIXELS ? savedColors : new byte[PIXELS];
		return new MapPrintImage(colors, tag.getString("title"),
			Math.max(1, tag.getInteger("sourceWidth")), Math.max(1, tag.getInteger("sourceHeight")));
	}
}
