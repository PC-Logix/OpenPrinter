package pcl.openprinter.books;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BookPage {
    int pageNumber = -1;
    ArrayList<BookPageLine> lines = new ArrayList<>();

    public BookPage(int PageNumber){
        this.pageNumber = PageNumber;
    }

    public void addLine(String Text, int LineNumber, int Scale){
        if(Text.length() > 0)
            lines.add(new BookPageLine(Text, LineNumber, Scale));
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();

        HashSet<Object> linesList = new HashSet<>();
        for(BookPageLine line : lines){
            linesList.add(line.toMap());
        }

        map.put("page", pageNumber);
        map.put("content", linesList);


        return map;
    }
}
