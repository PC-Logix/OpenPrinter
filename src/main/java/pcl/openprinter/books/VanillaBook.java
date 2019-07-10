package pcl.openprinter.books;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

public class VanillaBook extends GenericBook {

    public Object[] readFromStack(ItemStack book) {
        if (!book.hasTagCompound() || !book.getTagCompound().hasKey("pages"))
            return new Object[]{false, "book doesnt have nbt data"};

        if (book.getItem() instanceof ItemWritableBook) {
            readUnsignedBookFromStack(book);
        }
        else if (book.getItem() instanceof ItemWrittenBook) {
            readSignedBookFromStack(book);
        }

        return new Object[]{ get() };
    }

    private void readUnsignedBookFromStack(ItemStack book){
        NBTTagList tags = book.getTagCompound().getTagList("pages", 8);

        for(int i=0; i < tags.tagCount(); i++){
            BookPage page = new BookPage(i+1);
            int l=1;
            for(String line : tags.getStringTagAt(i).split("\n")) {
                page.addLine(line, l, 0);
                l++;
            }
            addPage(page);
        }
    }

    // signed books store their page data as JSON and have author/title tags
    private void readSignedBookFromStack(ItemStack book){
        isSigned = true;
        title = book.getTagCompound().getString("title");
        author = book.getTagCompound().getString("author");

        NBTTagList tags = book.getTagCompound().getTagList("pages", 8);

        for(int i=0; i < tags.tagCount(); i++){
            BookPage page = new BookPage(i+1);
            int l=1;
            for(String line : ITextComponent.Serializer.jsonToComponent(tags.getStringTagAt(i)).getUnformattedText().split("\n")) {
                page.addLine(line, l, 0);
                l++;
            }
            addPage(page);
        }
    }
}
