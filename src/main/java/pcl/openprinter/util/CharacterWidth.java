package pcl.openprinter.util;

public class CharacterWidth {
    private static int CharacterWidths[] = new int[128];

    public static void initWidths() {
        /* I'm sorry */
        for (int i = 0; i < CharacterWidths.length; i++) {
            CharacterWidths[i] = 5;
        }
        CharacterWidths[32] = 3;
        CharacterWidths[33] = 1;
        CharacterWidths[34] = 3;
        CharacterWidths[39] = 1;
        CharacterWidths[40] = 3;
        CharacterWidths[41] = 3;
        CharacterWidths[42] = 3;
        CharacterWidths[44] = 1;
        CharacterWidths[46] = 1;
        CharacterWidths[58] = 1;
        CharacterWidths[59] = 1;
        CharacterWidths[60] = 4;
        CharacterWidths[62] = 4;
        CharacterWidths[64] = 6;
        CharacterWidths[73] = 3;
        CharacterWidths[91] = 3;
        CharacterWidths[93] = 3;
        CharacterWidths[96] = 2;
        CharacterWidths[102] = 4;
        CharacterWidths[105] = 1;
        CharacterWidths[107] = 4;
        CharacterWidths[108] = 2;
        CharacterWidths[116] = 3;
        CharacterWidths[123] = 3;
        CharacterWidths[124] = 1;
        CharacterWidths[125] = 3;
        CharacterWidths[126] = 6;
    }
    public static int calculateWidth(String str) {
	String dispstr = str.replaceAll("(?:§[0-9a-fk-or])+", "");
        int rlen = dispstr.length();
        boolean cc = false;
        int offset = 0;
        for (int i = 0; i < str.length(); i++) {
            if (cc) {
                // assume any bold character is an extra character wide
                switch (Character.toString(str.charAt(i))) {
                    case "l":
                        offset = 1;
                        System.out.println("Setting bold.");
                        break;
                    case "r":
                        offset = 0;
                        break;
                }
                cc = false;
            } else {
                if (str.charAt(i) == ("§").charAt(0)) {
                    cc =  true;
                } else {
                    rlen = rlen + offset;
                    if ((int) str.charAt(i) > CharacterWidths.length) {
                        rlen = rlen + 5;
                    } else {
                        rlen = rlen + CharacterWidths[(int) str.charAt(i)];
                    }
                }
            }
        }
        return rlen;
    }
    public static String limitWidth(String str, int limit) {
        String rstr = "";
        for (int i = 0; i < str.length(); i++) {
            if (calculateWidth(rstr.concat(Character.toString(str.charAt(i)))) < limit) {
                rstr = rstr.concat(Character.toString(str.charAt(i)));
            } else {
                break;
            }
        }
        return rstr;
    }
}
