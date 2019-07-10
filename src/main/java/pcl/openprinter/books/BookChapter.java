package pcl.openprinter.books;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BookChapter {
    ArrayList<String> chapterTitle = new ArrayList<>();
    int pageNumber = -1;

    public BookChapter(int PageNumber, String[] title){
        this(PageNumber, Arrays.asList(title));
    }

    public BookChapter(int PageNumber, List<String> title){
        this.pageNumber = PageNumber;
        chapterTitle.addAll(title);
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", pageNumber);
        map.put("title", chapterTitle);
        return map;
    }
}
