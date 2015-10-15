package pcl.openprinter;

import net.minecraftforge.common.config.Configuration;

/**
 * @author Caitlyn
 *
 */
public class Config
{ 
    public final boolean render3D;

	private int defaultInkUse = 4000;
	public final int printerInkUse;
	public final boolean enableMUD;
    public final boolean enableNameTag;

    public Config(Configuration config)
    {
        config.load();
        printerInkUse = config.get("options", "inkUses", defaultInkUse, "How many times you can print with a ink cartridge").getInt(defaultInkUse);
        render3D = config.get("options", "Render3D", true, "Should we use the 3D Model, or a block").getBoolean(true);
		enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
        enableNameTag = config.get("options", "enableNameTag", true, "Allows the printer to print Name Tags, configurable because this usually costs XP.").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
