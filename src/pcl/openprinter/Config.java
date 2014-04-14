/**
 * 
 */
package pcl.openprinter;

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
    
    private int defaultPrinterInkColorID = 17803;
	public final int printerInkColorID;
	
	private int defaultPrinterInkBlackID = 17804;
	public final int printerInkBlackID;
	
	private int defaultInkUse = 100;
	public final int printerInkUse;

    public Config(Configuration config)
    {
        config.load();
        printerBlockID = config.get("blocks", "PrinterID", defaultPrinterID).getInt(defaultPrinterID);
        printedPageID = config.get("items", "PrintedPageID", defaultPrintedPageID).getInt(defaultPrintedPageID);
        printerPaperID = config.get("items", "PrinterPaperID", defaultPrinterPaperID).getInt(defaultPrinterPaperID);
        printerPaperRollID = config.get("items", "PrinterPaperRollID", defaultPrinterPaperRollID).getInt(defaultPrinterPaperRollID);
        printerInkColorID = config.get("items", "PrinterInkColor", defaultPrinterInkColorID).getInt(defaultPrinterInkColorID);
        printerInkBlackID = config.get("items", "PrinterInkBlack", defaultPrinterInkBlackID).getInt(defaultPrinterInkBlackID);
        printerInkUse = config.get("options", "inkUses", defaultInkUse, "How many times you can print with a ink cartridge").getInt(defaultInkUse);
        render3D = config.get("options", "Render3D", true, "Should we use the 3D Model, or a block").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
