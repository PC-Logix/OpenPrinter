package pcl.openprinter.manual;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import pcl.openprinter.OpenPrinter;

import java.util.HashSet;

public class Manual {
    private static ResourceLocation iconResourceLocation = new ResourceLocation(OpenPrinter.MODID, "textures/items/printer_ink_color.png");
    private static String tooltip = "OpenPrinter";
    private static String homepage = "assets/" + OpenPrinter.MODID + "/doc/_Sidebar";

    public static HashSet<Item> items = new HashSet<>();


    public static void preInit(){
        if(Loader.isModLoaded("rtfm")) {
            new ManualPathProviderRTFM().initialize(iconResourceLocation, tooltip, homepage);
            items.add(ManualPathProviderRTFM.getManualItem().setUnlocalizedName("manual").setRegistryName("manual").setCreativeTab(OpenPrinter.CreativeTab));
        }

        if(Loader.isModLoaded("opencomputers"))
            new ManualPathProviderOC().initialize(iconResourceLocation, tooltip, homepage);
    }

}
