package pcl.openprinter.books;

import java.util.HashMap;

public class BookPageLine {
    private int scale = 1;
    private String text = "";
    private int lineNumber = -1;

    public BookPageLine(String Text, int LineNumber, int Scale){
        this.scale = Scale;
        this.text = Text;
        this.lineNumber = LineNumber;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("line", lineNumber);
        map.put("text", text);
        map.put("scale", scale);
        return map;
    }
}
