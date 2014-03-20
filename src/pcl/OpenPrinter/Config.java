/**
 * 
 */
package pcl.OpenPrinter;

import net.minecraftforge.common.Configuration;

/**
 * @author Caitlyn
 *
 */
public class Config
{
    private int defaultBlockID = 780;
    public final int blockID;
    
    private int defaultItemID = 17800;
    public final int itemID;

    public Config(Configuration config)
    {
        config.load();
        
        blockID = config.get("blocks", "blockID", defaultBlockID).getInt(defaultBlockID);
        itemID = config.get("items", "itemID", defaultItemID).getInt(defaultItemID);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
