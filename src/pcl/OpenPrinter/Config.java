/**
 * 
 */
package pcl.OpenPrinter;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

/**
 * @author Caitlyn
 *
 */
public class Config
{
    private int defaultPrinterID = 780;
    public final int printerBlockID;
    
    private int defaultPrintedPageID = 17800;
    public final int printedPageID;
    
    private int defaultPrinterPaperID = 17801;
    public final int printerPaperID;
    
    private int defaultPrinterPaperRollID = 17802;
    public final int printerPaperRollID;
    
    public final boolean render3D;

    public Config(Configuration config)
    {
        config.load();
        printerBlockID = config.get("blocks", "PrinterID", defaultPrinterID).getInt(defaultPrinterID);
        printedPageID = config.get("items", "PrintedPageID", defaultPrintedPageID).getInt(defaultPrintedPageID);
        printerPaperID = config.get("items", "PrinterPaperID", defaultPrinterPaperID).getInt(defaultPrinterPaperID);
        printerPaperRollID = config.get("items", "PrinterPaperRollID", defaultPrinterPaperRollID).getInt(defaultPrinterPaperRollID);
        render3D = config.get("options", "Render3D", true, "Should we use the 3D Model, or a block").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
