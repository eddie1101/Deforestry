package com.example.examplemod;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@Mod.EventBusSubscriber(modid = InventiveChoppers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    private static final ModConfigSpec.IntValue MAX_FELLING_AXE_CHOP = BUILDER
            .comment("The maximum number of logs that can be chopped at once by the Felling Axe. [1-8192]")
            .defineInRange("maxFellingAxeChop", 128, 1, 8192);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxFellingAxeChop;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxFellingAxeChop = MAX_FELLING_AXE_CHOP.get();
    }
}
