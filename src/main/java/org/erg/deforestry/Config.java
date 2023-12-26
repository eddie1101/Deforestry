package org.erg.deforestry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@Mod.EventBusSubscriber(modid = Deforestry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_GLOBAL_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped by any item.\n0 Means that each chopper will have its own max defined as below.\n[0-64000]")
            .defineInRange("maxGlobalChop", 0, 0, 64000);
    private static final ModConfigSpec.IntValue MAX_FELLING_AXE_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Felling Axe.\n[1-64000]")
            .defineInRange("maxFellingAxeChop", 128, 1, 64000);

    private static final ModConfigSpec.IntValue MAX_REMOTE_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Remote Chopper.\n[1-64000]")
            .defineInRange("maxFellingAxeChop", 128, 1, 64000);

    private static final ModConfigSpec.IntValue MAX_BOOMERANG_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Boomerang Chopper.\n[1-64000]")
            .defineInRange("maxFellingAxeChop", 128, 1, 64000);

    private static final ModConfigSpec.IntValue MAX_CHAINSAW_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Chainsaw.\n[1-64000]")
            .defineInRange("maxFellingAxeChop", 128, 1, 64000);

    private static final ModConfigSpec.IntValue REMOTE_CHOPPER_RANGE = BUILDER
            .comment("The range in blocks that the remote chopper will be able to chop trees.\n[1-512]")
            .defineInRange("remoteChopperRange", 256, 1, 512);
    private static final ModConfigSpec.BooleanValue DO_VERBOSE_LOGGING = BUILDER
            .comment("Log extreme details of mod processes.\nOnly use if you are a developer or your logs will be flooded!\n[true-false]")
            .define("doVerboseLogging", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxFellingAxeChop;
    public static int maxRemoteChop;
    public static int maxBoomerangChop;
    public static int maxChainsawChop;
    public static float remoteChopperRange;
    public static boolean doVerboseLogging;
    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        int maxGlobalChop = MAX_GLOBAL_CHOP.get();
        maxFellingAxeChop = maxGlobalChop > 0 ? maxGlobalChop : MAX_FELLING_AXE_CHOP.get();
        maxRemoteChop = maxGlobalChop > 0 ? maxGlobalChop : MAX_REMOTE_CHOP.get();
        maxBoomerangChop = maxGlobalChop > 0 ? maxGlobalChop : MAX_BOOMERANG_CHOP.get();
        maxChainsawChop = maxGlobalChop > 0 ? maxGlobalChop : MAX_CHAINSAW_CHOP.get();

        remoteChopperRange = (float) REMOTE_CHOPPER_RANGE.get();

        doVerboseLogging = DO_VERBOSE_LOGGING.get();
    }
}
