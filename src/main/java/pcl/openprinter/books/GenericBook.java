package pcl.openprinter.books;

import java.util.ArrayList;
import java.util.HashMap;

public class GenericBook {
    ArrayList<BookPage> pages = new ArrayList<>();
    ArrayList<BookChapter> chapters = new ArrayList<>();
    String author = "";
    String title = "";
    boolean isSigned = false;


    public void addPage(BookPage Page){
        pages.add(Page);
    }

    public void addChapter(BookChapter Chapter){
        chapters.add(Chapter);
    }

    public HashMap<String, Object> get(){
        HashMap<String, Object> output = new HashMap<>();

        if(isSigned){
            output.put("title", title);
            output.put("author", author);
        }

        ArrayList<Object> pageData = new ArrayList<>();
        for(BookPage page : pages){
            pageData.add(page.toMap());
        }
        output.put("pages", pageData);

        return output;
    }

}
