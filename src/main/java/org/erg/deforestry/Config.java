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
            .comment("""
                    The maximum number of logs that can be chopped by any item.
                    Warning: This config value, more than any of the others below, could drastically affect performance at high numbers
                    [1-64000]""")
            .defineInRange("maxGlobalChop", 192, 1, 64000);
    private static final ModConfigSpec.IntValue MAX_FELLING_AXE_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Felling Axe.\n[1-64000]")
            .defineInRange("maxFellingAxeChop", 192, 1, 64000);

    private static final ModConfigSpec.IntValue MAX_REMOTE_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Remote Chopper.\n[1-64000]")
            .defineInRange("maxRemoteChop", 192, 1, 64000);

    private static final ModConfigSpec.IntValue MAX_BOOMERANG_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Boomerang Chopper.\n[1-64000]")
            .defineInRange("maxBoomerangChop", 192, 1, 64000);

    private static final ModConfigSpec.IntValue CHAINSAW_SPEED = BUILDER
            .comment("The number of logs that the Chainsaw can chop per chop.\n[1-64000]")
            .defineInRange("chainsawSpeed", 1, 1, 64000);

    private static final ModConfigSpec.IntValue CHAINSAW_INTERVAL = BUILDER
            .comment("The number of ticks in between each chainsaw chop.\n[0-20]")
            .defineInRange("chainsawInterval", 1, 0, 20);

    private static final ModConfigSpec.IntValue REMOTE_CHOPPER_RANGE = BUILDER
            .comment("The range in blocks that the remote chopper will be able to chop trees.\n[1-512]")
            .defineInRange("remoteChopperRange", 256, 1, 512);

    private static final ModConfigSpec.IntValue REMOTE_CHOPPER_COOLDOWN = BUILDER
            .comment("The number of ticks in between remote choper uses (20 ticks/second).\n[0-72000]")
            .defineInRange("remoteChopperCooldown", 30, 0, 72000);

    private static final ModConfigSpec.IntValue BOOMERANG_DEFAULT_RANGE = BUILDER
            .comment("The range in blocks that the boomerang can travel from its owner (when launched at full power).\n[0-512]")
            .defineInRange("boomerangRange", 16, 0, 512);

    private static final ModConfigSpec.IntValue BOOMERANG_LIFESPAN = BUILDER
            .comment("The time in ticks (20 ticks/second) that the boomerang can attempt to seek its owner for.\n[0-1200]")
            .defineInRange("boomerangLifespan", 200, 0, 1200);

    private static final ModConfigSpec.IntValue BOOMERANG_OWNER_TIMEOUT = BUILDER
            .comment("""
                     The time in ticks (20 ticks/second) that the boomerang will wait for it's owner to log in on world load.
                     If on a server, consider setting this value higher.
                     [0-1728000]""")
            .defineInRange("boomerangOwnerTimeout", 20, 0, 1728000);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxGlobalChop;
    public static int maxFellingAxeChop;
    public static int maxRemoteChop;
    public static int maxBoomerangChop;
    public static int chainsawSpeed;
    public static int chainsawInterval;
    public static float remoteChopperRange;
    public static int remoteChopperCooldown;
    public static int boomerangDefaultRange;
    public static int boomerangLifespan;
    public static int boomerangOwnerTimeout;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxGlobalChop = MAX_GLOBAL_CHOP.get();
        maxFellingAxeChop = MAX_FELLING_AXE_CHOP.get();
        maxRemoteChop = MAX_REMOTE_CHOP.get();
        maxBoomerangChop = MAX_BOOMERANG_CHOP.get();

        chainsawSpeed = CHAINSAW_SPEED.get();
        chainsawInterval = CHAINSAW_INTERVAL.get();

        remoteChopperRange = (float) REMOTE_CHOPPER_RANGE.get();
        remoteChopperCooldown = REMOTE_CHOPPER_COOLDOWN.get();

        boomerangDefaultRange = BOOMERANG_DEFAULT_RANGE.get();
        boomerangLifespan = BOOMERANG_LIFESPAN.get();
        boomerangOwnerTimeout = BOOMERANG_OWNER_TIMEOUT.get();

    }
}
