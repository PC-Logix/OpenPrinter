package pcl.openprinter;

final class CharacterWidth {
    private static final int[] WIDTHS = new int[128];

    static {
        java.util.Arrays.fill(WIDTHS, 5);
        int[][] values = {{32,3},{33,1},{34,3},{39,1},{40,3},{41,3},{42,3},{44,1},{46,1},{58,1},{59,1},
                {60,4},{62,4},{64,6},{73,3},{91,3},{93,3},{96,2},{102,4},{105,1},{107,4},{108,2},
                {116,3},{123,3},{124,1},{125,3},{126,6}};
        for (int[] value : values) WIDTHS[value[0]] = value[1];
    }

    static int calculate(String text) {
        int width = 0;
        boolean formatting = false;
        boolean bold = false;
        for (char c : text.toCharArray()) {
            if (formatting) {
                if (c == 'l') bold = true;
                if (c == 'r') bold = false;
                formatting = false;
            } else if (c == '\u00a7') {
                formatting = true;
            } else {
                width += 1 + (c < WIDTHS.length ? WIDTHS[c] : 5) + (bold ? 1 : 0);
            }
        }
        return width;
    }

    private CharacterWidth() {}
}
