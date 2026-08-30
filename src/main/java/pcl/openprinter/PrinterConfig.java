package pcl.openprinter;

import net.neoforged.neoforge.common.ModConfigSpec;

final class PrinterConfig {
    static final ModConfigSpec SPEC;
    static final ModConfigSpec.BooleanValue ENABLE_NAME_TAGS;
    static final ModConfigSpec.IntValue INK_CAPACITY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("printer");
        ENABLE_NAME_TAGS = builder.comment("Allow computers to print named name tags.")
                .define("enableNameTagPrinting", true);
        INK_CAPACITY = builder.comment("Nominal ink cartridge capacity shown by the Lua API.")
                .defineInRange("inkCapacity", 400, 1, 100000);
        builder.pop();
        SPEC = builder.build();
    }

    private PrinterConfig() {}
}
