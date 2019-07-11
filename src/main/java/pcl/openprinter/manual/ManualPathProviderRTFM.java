package pcl.openprinter.manual;

import li.cil.manual.api.ManualAPI;
import li.cil.manual.api.detail.ManualDefinition;
import li.cil.manual.api.manual.PathProvider;
import li.cil.manual.api.prefab.manual.TextureTabIconRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

class ManualPathProviderRTFM extends ManualPathProvider implements PathProvider {
    private static ManualDefinition manual;

    void initialize(ResourceLocation iconResourceLocation, String tooltip, String path) {
        manual = ManualAPI.createManual(false);

        if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT)) {
            manual.addProvider(new ManualPathProviderRTFM());
            manual.addProvider(new ManualContentProviderRTFM());
            manual.setDefaultPage(path);

            manual.addTab(new TextureTabIconRenderer(iconResourceLocation), tooltip, path);
        }
    }

    static Item getManualItem(){
        return ManualAPI.createItemForManual(manual);
    }

}
