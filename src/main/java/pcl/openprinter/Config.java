/**
 * 
 */
package pcl.openprinter;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author Caitlyn
 *
 */
public class Config
{ 
    public final boolean render3D;
	
	private int defaultInkUse = 4000;
	public final int printerInkUse;

	private boolean defaultEnableMUD = true;
	public final boolean enableMUD;
	
    public Config(Configuration config)
    {
        config.load();
        if (config.get("options", "inkUses", defaultInkUse).getInt(defaultInkUse) == 100) {
        	config.get("options", "inkUses", defaultInkUse, "How many times you can print with a ink cartridge").set(defaultInkUse);
        }
        printerInkUse = config.get("options", "inkUses", defaultInkUse, "How many times you can print with a ink cartridge").getInt(defaultInkUse);
        render3D = config.get("options", "Render3D", true, "Should we use the 3D Model, or a block").getBoolean(true);
		enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
