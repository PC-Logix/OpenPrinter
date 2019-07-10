package pcl.openprinter.books;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;

public class BiblioCraftBigBook extends GenericBook {

    @Override
    public HashMap<String, Object> get(){
        HashMap<String, Object> output = super.get();

        ArrayList<Object> chapterData = new ArrayList<>();
        for(BookChapter chapter : chapters){
            chapterData.add(chapter.toMap());
        }
        output.put("chapters", chapterData);

        return output;
    }

    public Object[] readFromStack(ItemStack book){
        if(!book.hasTagCompound() || !book.getTagCompound().hasKey("pages"))
            return new Object[] { false, "book doesnt have nbt data"};

        NBTTagCompound nbt = book.getTagCompound();

        // parse meta
        isSigned = nbt.hasKey("signed");

        if(isSigned){
            title = nbt.getCompoundTag("display").getString("Name");
            author = nbt.getString("author");
        }

        // parse pages
        NBTTagCompound pagesNBT = nbt.getCompoundTag("pages");

        for(int i=0; i < nbt.getInteger("pagesTotal"); i++){
            BookPage page = new BookPage(i+1);

            NBTTagList tags = pagesNBT.getTagList("page"+i, 8);
            int[] lineScale = pagesNBT.getIntArray("pageScale"+i);
            for(int j=0; j < tags.tagCount(); j++) {
                String line = tags.getStringTagAt(j);
                if(line.length() > 0) {
                    page.addLine(line, j+1, lineScale[j]);
                }
            }

            addPage(page);
        }

        // parse chapters
        NBTTagCompound chaptersNBT = nbt.getCompoundTag("chapters");
        int[] chapters = chaptersNBT.getIntArray("chapBools");
        int[] chaptersPages = chaptersNBT.getIntArray("chapPages");

        HashMap<String, Object> chapterOutput = new HashMap<>();
        for(int chapter=0; chapter < chapters.length; chapter++){
            if(chapters[chapter] == 1){
                ArrayList<String> titles = new ArrayList<>();

                String title1 = chaptersNBT.getString("chapline"+chapter*2);
                String title2 = chaptersNBT.getString("chapline"+(chapter*2+1));

                if(title1.length() > 0)
                    titles.add(title1);

                if(title2.length() > 0)
                    titles.add(title2);

                addChapter(new BookChapter(1+chaptersPages[chapter], titles));
            }
        }

        return new Object[]{ get() };
    }
}
