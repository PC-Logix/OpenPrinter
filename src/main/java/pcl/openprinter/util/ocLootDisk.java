package pcl.openprinter.util;

import li.cil.oc.api.IMC;
import li.cil.oc.api.fs.FileSystem;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.common.Optional;
import pcl.openprinter.OpenPrinter;

import java.util.concurrent.Callable;

public class ocLootDisk {
    public static void register(){
        //register loot disks
        li.cil.oc.api.Items.registerFloppy("OpenPrinter Copy Tool", EnumDyeColor.GREEN, new OCLootDiskFileSystem("printer/printerCopy"), true);
        IMC.registerProgramDiskLabel("printerCopy", "printerCopy", "Lua 5.2", "Lua 5.3", "LuaJ");
    }

    private static class OCLootDiskFileSystem implements Callable<FileSystem> {
        private final String name;
        OCLootDiskFileSystem(String name) {
            this.name = name;
        }

        @Override
        @Optional.Method(modid = "opencomputers")
        public FileSystem call() throws Exception {
            return li.cil.oc.api.FileSystem.asReadOnly(li.cil.oc.api.FileSystem.fromClass(OpenPrinter.class, OpenPrinter.MODID, "loot/" + this.name));
        }
    }
}
